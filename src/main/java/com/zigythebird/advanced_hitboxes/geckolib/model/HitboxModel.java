/*
 * MIT License
 *
 * Copyright (c) 2024 GeckoThePecko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.zigythebird.advanced_hitboxes.geckolib.model;

import com.mojang.math.Axis;
import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.animation.HitboxAnimManager;
import com.zigythebird.advanced_hitboxes.animation.HitboxAnimationData;
import com.zigythebird.advanced_hitboxes.animation.HitboxAnimationProcessor;
import com.zigythebird.advanced_hitboxes.client.utils.ClientUtils;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.cache.HitboxCache;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedAnimations;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxBone;
import com.zigythebird.advanced_hitboxes.misc.CommonArmPose;
import com.zigythebird.advanced_hitboxes.misc.CustomPoseStack;
import com.zigythebird.advanced_hitboxes.utils.PlayerVanillaAnimationApplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Base class for all code-based model objects
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Models">GeckoLib Wiki - Models</a>
 */
public abstract class HitboxModel<T extends AdvancedHitboxEntity> {
	private final HitboxAnimationProcessor PROCESSOR = new HitboxAnimationProcessor();
	private final Map<AdvancedHitboxEntity, HitboxAnimManager> MANAGERS = new WeakHashMap<>();

	private BakedHitboxModel currentModel = null;

	/**
	 * Returns the resource path for the {@link BakedHitboxModel} (model JSON file) to render based on the provided animatable
	 */
	public abstract ResourceLocation getModelResource(T animatable);

	/**
	 * Returns the resourcepath for the {@link BakedAnimations} (animation JSON file) to use for animations based on the provided animatable
	 */
	public abstract ResourceLocation getAnimationResource(T animatable);

	/**
	 * Override this and return true if the game should crash when attempting to animate the model, but fails to find a bone
	 * <p>
	 * By default, the mod will just gracefully ignore a missing bone, which might cause oddities with incorrect models or mismatching variables
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
				throw AdvancedHitboxesMod.exception(location, "Invalid model resource path provided - models must be placed in assets/<modid>/geo/");

			throw AdvancedHitboxesMod.exception(location, "Unable to find model");
		}

		if (model != this.currentModel) {
			this.currentModel = model;
		}

		return this.currentModel;
	}

	/**
	 * Gets a bone from this model by name
	 *
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link HitboxBone} if one matches, otherwise an empty Optional
	 */
	public Optional<HitboxBone> getBone(String name) {
		return this.currentModel.getBone(name);
	}

	/**
	 * Gets the {@link HitboxAnimationProcessor} for this model.
	 */
	public HitboxAnimationProcessor getAnimationProcessor() {
		return this.PROCESSOR;
	}

	/**
	 * Gets the {@link HitboxAnimManager} for this model.
	 */
	public HitboxAnimManager getManager(AdvancedHitboxEntity entity) {
		return MANAGERS.computeIfAbsent(entity, (entity1 -> {
			HitboxAnimManager manager1 = new HitboxAnimManager();
			entity1.registerHitboxControllers(manager1);
			return manager1;
		}));
	}

	//TODO Make an alternative
//	/**
//	 * Add additional {@link DataTicket DataTickets} to the {@link AnimationState} to be handled by your animation handler at render time
//	 *
//	 * @param animatable The animatable instance currently being animated
//	 * @param instanceId The unique instance id of the animatable being animated
//	 * @param dataConsumer The DataTicket + data consumer to be added to the AnimationEvent
//	 */
//	public void addAdditionalStateData(T animatable, long instanceId, BiConsumer<DataTicket<T>, T> dataConsumer) {}

	/**
	 * This method is called once per render frame for each {@link AdvancedHitboxEntity} being rendered
	 * <p>
	 * It is an internal method for automated animation parsing. Use {@link HitboxModel#setCustomAnimations} for custom animation work
	 */
	@ApiStatus.Internal
	public void handleAnimations(T entity, float partialTick, boolean fullTick, float netHeadYaw) {
		if (entity instanceof Entity actualEntity) {
			Vec3 velocity = actualEntity.getDeltaMovement();
			HitboxAnimManager manager = getManager(entity);
			
			int currentTick = actualEntity.tickCount;

			float currentFrameTime = currentTick + partialTick;

			HitboxAnimationData animationData = new HitboxAnimationData(entity, (float) ((Math.abs(velocity.x) + Math.abs(velocity.z)) / 2f), partialTick);

			if (fullTick) manager.tick(animationData.copy());

			if (!manager.isFirstTick() && currentFrameTime == manager.getLastUpdateTime())
				return;

			//TODO Look into doing this in a better way
			boolean paused = false;
			if (ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().isSingleplayer()) {
				paused = ClientUtils.isGamePaused();
			}
			if (!paused || entity.advanced_hitboxes$shouldPlayAnimsWhileGamePaused()) {
				manager.updatedAt(currentFrameTime);
			}

			this.PROCESSOR.tickAnimation(manager, animationData);

			if (this.currentModel == null) return;

			for (HitboxBone bone : this.currentModel.topLevelBones()) {
				resetBone(bone);
			}

			if (entity instanceof Player player) {
				float f4 = 0.0F;
				float f5 = 0.0F;
				if (/*!shouldSit &&*/ player.isAlive()) {
					f4 = player.walkAnimation.speed(partialTick);
					f5 = player.walkAnimation.position(partialTick);
					if (player.isBaby()) {
						f5 *= 3.0F;
					}

					if (f4 > 1.0F) {
						f4 = 1.0F;
					}
				}

				try {
					//TODO Make sure this works correctly if you swap your main hand to your left hand
					//TODO Make sure none of the values used aren't client only or only updated on the client
					PlayerVanillaAnimationApplier.setupAnim(getBone("head_hitbox").get(), getBone("torso_hitbox").get(),
							getBone("right_arm_hitbox").get(), getBone("left_arm_hitbox").get(),
							getBone("right_leg_hitbox").get(), getBone("left_leg_hitbox").get(),
							player.getSwimAmount(partialTick), player.isPassenger(), player.isCrouching(),
							getArmPose(player, InteractionHand.MAIN_HAND), getArmPose(player, InteractionHand.OFF_HAND),
							player.getAttackAnim(partialTick), player, f5, f4,
							player.tickCount + partialTick, netHeadYaw, player.getXRot());
				} catch (NoSuchElementException e) {
                    AdvancedHitboxesMod.LOGGER.warn("Player model is missing some of the expected bones.");
				}
			}

			for (HitboxBone bone : this.currentModel.topLevelBones()) {
				updateBone(bone, manager);
			}

			setCustomAnimations(entity, animationData);
		}
	}

	private void resetBone(HitboxBone bone) {
		bone.setToInitialPose();
		for (HitboxBone bone1 : bone.getChildBones())
			resetBone(bone1);
	}

	private void updateBone(HitboxBone bone, HitboxAnimManager manager) {
		manager.get3DTransform(bone);
		for (HitboxBone bone1 : bone.getChildBones())
			updateBone(bone1, manager);
	}

	/**
	 * This method is called once per render frame for each {@link AdvancedHitboxEntity} being rendered
	 * <p>
	 * Override to set custom animations (such as head rotation, etc)
	 *
	 * @param animatable The {@code AdvancedHitboxEntity} instance currently being rendered
	 * @param data An {@link HitboxAnimationData} instance created to hold animation data for the {@code animatable} for this method call
	 */
	public void setCustomAnimations(T animatable, HitboxAnimationData data) {}

	//TODO Make an alternative
//	/**
//	 * This method is called once per render frame for each {@link AdvancedHitboxEntity} being rendered
//	 * <p>
//	 * Use this method to set custom {@link Variable Variable} values via
//	 * {@link MathParser#setVariable(String, DoubleSupplier) MathParser.setVariable}
//	 *
//	 * @param animationState The AnimationState data for the current render frame
//	 * @param animTime The internal tick counter kept by the {@link Hitboxmanager manager} for this animatable
//	 */
//	public void applyMolangQueries(AnimationState<T> animationState, double animTime) {}

	private static CommonArmPose getArmPose(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.isEmpty()) {
			return CommonArmPose.EMPTY;
		} else {
			if (player.getUsedItemHand() == hand && player.getUseItemRemainingTicks() > 0) {
				UseAnim useanim = itemstack.getUseAnimation();
				if (useanim == UseAnim.BLOCK) {
					return CommonArmPose.BLOCK;
				}

				if (useanim == UseAnim.BOW) {
					return CommonArmPose.BOW_AND_ARROW;
				}

				if (useanim == UseAnim.SPEAR) {
					return CommonArmPose.THROW_SPEAR;
				}

				if (useanim == UseAnim.CROSSBOW && hand == player.getUsedItemHand()) {
					return CommonArmPose.CROSSBOW_CHARGE;
				}

				if (useanim == UseAnim.SPYGLASS) {
					return CommonArmPose.SPYGLASS;
				}

				if (useanim == UseAnim.TOOT_HORN) {
					return CommonArmPose.TOOT_HORN;
				}

				if (useanim == UseAnim.BRUSH) {
					return CommonArmPose.BRUSH;
				}
			} else if (!player.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
				return CommonArmPose.CROSSBOW_HOLD;
			}

			return CommonArmPose.ITEM;
		}
	}

	//TODO Maybe replace LivingEntity with an instance of T
	public void setupRotations(LivingEntity entity, CustomPoseStack poseStack, float ageInTicks, float yBodyRot, float partialTick, float scale) {
		if (isShaking(entity)) {
			yBodyRot += (float)(Math.cos((double)entity.tickCount * (double)3.25F) * Math.PI * (double)0.4F);
		}

		if (!entity.hasPose(Pose.SLEEPING)) {
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yBodyRot));
		}

		if (entity.deathTime > 0) {
			float f = ((float)entity.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
			f = Mth.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			poseStack.mulPose(Axis.ZP.rotationDegrees(f * getFlipDegrees(entity)));
		} else if (entity.isAutoSpinAttack()) {
			poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - entity.getXRot()));
			poseStack.mulPose(Axis.YP.rotationDegrees(((float)entity.tickCount + partialTick) * -75.0F));
		} else if (entity.hasPose(Pose.SLEEPING)) {
			Direction direction = entity.getBedOrientation();
			float f1 = direction != null ? sleepDirectionToRotation(direction) : yBodyRot;
			poseStack.mulPose(Axis.YP.rotationDegrees(f1));
			poseStack.mulPose(Axis.ZP.rotationDegrees(getFlipDegrees(entity)));
			poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
		} else if (isEntityUpsideDown(entity)) {
			poseStack.translate(0.0F, (entity.getBbHeight() + 0.1F) / scale, 0.0F);
			poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		}
	}

	protected float sleepDirectionToRotation(Direction facing) {
		switch (facing) {
			case SOUTH -> {
				return 90.0F;
			}
			case WEST -> {
				return 0.0F;
			}
			case NORTH -> {
				return 270.0F;
			}
			case EAST -> {
				return 180.0F;
			}
			default -> {
				return 0.0F;
			}
		}
	}

	protected boolean isEntityUpsideDown(LivingEntity entity) {
		if (entity instanceof Player || entity.hasCustomName()) {
			String s = ChatFormatting.stripFormatting(entity.getName().getString());
			if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
				return !(entity instanceof Player) || ((Player)entity).isModelPartShown(PlayerModelPart.CAPE);
			}
		}

		return false;
	}

	protected boolean isShaking(LivingEntity entity) {
		return entity.isFullyFrozen();
	}

	protected float getFlipDegrees(LivingEntity entity) {
		return 90;
	}
}
