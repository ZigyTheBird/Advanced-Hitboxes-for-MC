package com.zigythebird.advanced_hitboxes.geckolib.model;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.AnimatableManager;
import com.zigythebird.advanced_hitboxes.geckolib.animation.Animation;
import com.zigythebird.advanced_hitboxes.geckolib.animation.AnimationProcessor;
import com.zigythebird.advanced_hitboxes.geckolib.animation.AnimationState;
import com.zigythebird.advanced_hitboxes.geckolib.cache.HitboxCache;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.GeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.constant.DataTickets;
import com.zigythebird.advanced_hitboxes.geckolib.constant.dataticket.DataTicket;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathParser;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.value.Variable;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.BakedAnimations;
import com.zigythebird.advanced_hitboxes.geckolib.util.RenderUtil;
import com.zigythebird.advanced_hitboxes.utils.ClientUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;

/**
 * Base class for all code-based model objects
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Models">GeckoLib Wiki - Models</a>
 */
public abstract class HitboxModel<T extends AdvancedHitboxEntity> {
	private final AnimationProcessor<T> processor = new AnimationProcessor<>(this);

	private BakedHitboxModel currentModel = null;
	private double animTime;
	private double lastGameTickTime;
	private long lastRenderedInstance = -1;

	/**
	 * Returns the resource path for the {@link BakedHitboxModel} (model json file) to render based on the provided animatable
	 */
	public abstract ResourceLocation getModelResource(T animatable);

	/**
	 * Returns the resourcepath for the {@link BakedAnimations} (animation json file) to use for animations based on the provided animatable
	 */
	public abstract ResourceLocation getAnimationResource(T animatable);

	/**
	 * Override this and return true if Geckolib should crash when attempting to animate the model, but fails to find a bone
	 * <p>
	 * By default, GeckoLib will just gracefully ignore a missing bone, which might cause oddities with incorrect models or mismatching variables
	 */
	public boolean crashIfBoneMissing() {
		return false;
	}

	/**
	 * Get the baked geo model object used for rendering from the given resource path
	 */
	public BakedHitboxModel getBakedModel(ResourceLocation location) {
		BakedHitboxModel model = HitboxCache.getBakedModels().get(location);

		if (model == null) {
			if (!location.getPath().contains("geo/"))
				throw AdvancedHitboxesMod.exception(location, "Invalid model resource path provided - GeckoLib models must be placed in assets/<modid>/geo/");

			throw AdvancedHitboxesMod.exception(location, "Unable to find model");
		}

		if (model != this.currentModel) {
			this.processor.setActiveModel(model);
			this.currentModel = model;
		}

		return this.currentModel;
	}

	/**
	 * Gets a bone from this model by name
	 *
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link GeoBone} if one matches, otherwise an empty Optional
	 */
	public Optional<GeoBone> getBone(String name) {
		return Optional.ofNullable(getAnimationProcessor().getBone(name));
	}

	/**
	 * Gets the loaded {@link Animation} for the given animation {@code name}, if it exists
	 *
	 * @param animatable The {@code AdvancedHitboxEntity} instance being referred to
	 * @param name The name of the animation to retrieve
	 * @return The {@code Animation} instance for the provided {@code name}, or null if none match
	 */
	public Animation getAnimation(T animatable, String name) {
		ResourceLocation location = getAnimationResource(animatable);
		BakedAnimations bakedAnimations = HitboxCache.getBakedAnimations().get(location);

		if (bakedAnimations == null) {
			if (!location.getPath().contains("animations/"))
				throw AdvancedHitboxesMod.exception(location, "Invalid animation resource path provided - GeckoLib animations must be placed in assets/<modid>/animations/");

			throw AdvancedHitboxesMod.exception(location, "Unable to find animation file.");
		}

		return bakedAnimations.getAnimation(name);
	}

	/**
	 * Gets the {@link AnimationProcessor} for this model.
	 */
	public AnimationProcessor<T> getAnimationProcessor() {
		return this.processor;
	}

	/**
	 * Add additional {@link DataTicket DataTickets} to the {@link AnimationState} to be handled by your animation handler at render time
	 *
	 * @param animatable The animatable instance currently being animated
	 * @param instanceId The unique instance id of the animatable being animated
	 * @param dataConsumer The DataTicket + data consumer to be added to the AnimationEvent
	 */
	public void addAdditionalStateData(T animatable, long instanceId, BiConsumer<DataTicket<T>, T> dataConsumer) {}

	/**
	 * This method is called once per render frame for each {@link AdvancedHitboxEntity} being rendered
	 * <p>
	 * It is an internal method for automated animation parsing. Use {@link HitboxModel#setCustomAnimations(AdvancedHitboxEntity, long, AnimationState)} for custom animation work
	 */
	@ApiStatus.Internal
	public void handleAnimations(T animatable, long instanceId, AnimationState<T> animationState, float partialTick) {
		AnimatableManager<T> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);
		Double currentTick = animationState.getData(DataTickets.TICK);

		if (currentTick == null)
			currentTick = animatable instanceof Entity entity ? (double)entity.tickCount : RenderUtil.getCurrentTick();

		if (animatableManager.getFirstTickTime() == -1)
			animatableManager.startedAt(currentTick + partialTick);

		double currentFrameTime = animatable instanceof Entity ? currentTick + partialTick : currentTick - animatableManager.getFirstTickTime();
		boolean isReRender = !animatableManager.isFirstTick() && currentFrameTime == animatableManager.getLastUpdateTime();

		if (isReRender && instanceId == this.lastRenderedInstance)
			return;

		boolean paused = false;
		if (((Entity)animatable).level().isClientSide || ServerLifecycleHooks.getCurrentServer().isSingleplayer()) {
			paused = ClientUtils.isGamePaused();
		}
		if (!paused || animatable.shouldPlayAnimsWhileGamePaused()) {
			animatableManager.updatedAt(currentFrameTime);

			double lastUpdateTime = animatableManager.getLastUpdateTime();
			this.animTime += lastUpdateTime - this.lastGameTickTime;
			this.lastGameTickTime = lastUpdateTime;
		}

		animationState.animationTick = this.animTime;
		this.lastRenderedInstance = instanceId;
		AnimationProcessor<T> processor = getAnimationProcessor();

		processor.preAnimationSetup(animationState, this.animTime);

		if (!processor.getRegisteredBones().isEmpty())
			processor.tickAnimation(animatable, this, animatableManager, this.animTime, animationState, crashIfBoneMissing());

		setCustomAnimations(animatable, instanceId, animationState);
	}

	/**
	 * This method is called once per render frame for each {@link AdvancedHitboxEntity} being rendered
	 * <p>
	 * Override to set custom animations (such as head rotation, etc)
	 *
	 * @param animatable The {@code AdvancedHitboxEntity} instance currently being rendered
	 * @param instanceId The instance id of the {@code AdvancedHitboxEntity}
	 * @param animationState An {@link AnimationState} instance created to hold animation data for the {@code animatable} for this method call
	 */
	public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {}

	/**
	 * This method is called once per render frame for each {@link AdvancedHitboxEntity} being rendered
	 * <p>
	 * Use this method to set custom {@link Variable Variable} values via
	 * {@link MathParser#setVariable(String, DoubleSupplier) MathParser.setVariable}
	 *
	 * @param animationState The AnimationState data for the current render frame
	 * @param animTime The internal tick counter kept by the {@link AnimatableManager manager} for this animatable
	 */
	public void applyMolangQueries(AnimationState<T> animationState, double animTime) {}
}
