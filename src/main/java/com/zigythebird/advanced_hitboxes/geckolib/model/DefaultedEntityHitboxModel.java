package com.zigythebird.advanced_hitboxes.geckolib.model;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.AnimationState;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.GeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.constant.DataTickets;
import com.zigythebird.advanced_hitboxes.geckolib.model.data.EntityModelData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * {@link DefaultedHitboxModel} specific to {@link net.minecraft.world.entity.Entity Entities}
 * <p>
 * Using this class pre-sorts provided asset paths into the "entity" subdirectory
 * <p>
 * Additionally it can automatically handle head-turning if the entity has a "head" bone
 */
public class DefaultedEntityHitboxModel<T extends AdvancedHitboxEntity> extends DefaultedHitboxModel<T> {
	protected final boolean turnsHead;

	/**
	 * Create a new instance of this model class
	 * <p>
	 * The asset path should be the truncated relative path from the base folder
	 * <p>
	 * E.G.
	 * <pre>{@code
	 * 	new ResourceLocation("myMod", "animals/red_fish")
	 * }</pre>
	 */
	public DefaultedEntityHitboxModel(ResourceLocation assetSubpath) {
		this(assetSubpath, false);
	}

	public DefaultedEntityHitboxModel(ResourceLocation assetSubpath, boolean turnsHead) {
		super(assetSubpath);

		this.turnsHead = turnsHead;
	}

	/**
	 * Returns the subtype string for this type of model
	 * <p>
	 * This allows for sorting of asset files into neat subdirectories for clean management.
	 */
	@Override
	protected String subtype() {
		return "entity";
	}

	@Override
	public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
		if (!this.turnsHead)
			return;

		GeoBone head = getAnimationProcessor().getBone("head");

		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
	}

	/**
	 * Changes the constructor-defined model path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	 */
	@Override
	public DefaultedEntityHitboxModel<T> withAltModel(ResourceLocation altPath) {
		return (DefaultedEntityHitboxModel<T>)super.withAltModel(altPath);
	}

	/**
	 * Changes the constructor-defined animations path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	 */
	@Override
	public DefaultedEntityHitboxModel<T> withAltAnimations(ResourceLocation altPath) {
		return (DefaultedEntityHitboxModel<T>)super.withAltAnimations(altPath);
	}
}
