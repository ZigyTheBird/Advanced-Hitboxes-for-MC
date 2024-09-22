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

package com.zigythebird.advanced_hitboxes.geckolib.animation;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.AnimationPoint;
import com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.BoneAnimationQueue;
import com.zigythebird.advanced_hitboxes.geckolib.animation.state.BoneSnapshot;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.GeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Mth;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class AnimationProcessor<T extends AdvancedHitboxEntity> {
	private final Map<String, GeoBone> bones = new Object2ObjectOpenHashMap<>();
	private final HitboxModel<T> model;

	public boolean reloadAnimations = false;

	public AnimationProcessor(HitboxModel<T> model) {
		this.model = model;
	}

	/**
	 * Build an animation queue for the given {@link RawAnimation}
	 *
	 * @param animatable The animatable object being rendered
	 * @param rawAnimation The raw animation to be compiled
	 * @return A queue of animations and loop types to play
	 */
	public Queue<QueuedAnimation> buildAnimationQueue(T animatable, RawAnimation rawAnimation) {
		LinkedList<QueuedAnimation> animations = new LinkedList<>();
		boolean error = false;

		for (RawAnimation.Stage stage : rawAnimation.getAnimationStages()) {
			Animation animation = null;

			if (stage.animationName() == RawAnimation.Stage.WAIT) { // This is intentional. Do not change this or Tslat will be unhappy
				animation = Animation.generateWaitAnimation(stage.additionalTicks());
			}
			else {
				try {
					animation = this.model.getAnimation(animatable, stage.animationName());
				}
				catch (RuntimeException ex) {
					AdvancedHitboxesMod.LOGGER.error("Unable to find animation: " + stage.animationName() + " for " + animatable.getClass().getSimpleName());

					error = true;
					ex.printStackTrace();
				}
			}

			if (animation != null)
				animations.add(new QueuedAnimation(animation, stage.loopType()));
		}

		return error ? null : animations;
	}

	/**
	 * Tick and apply transformations to the model based on the current state of the {@link AnimationController}
	 *
	 * @param animatable            The animatable object relevant to the animation being played
	 * @param model                 The model currently being processed
	 * @param animatableManager			The AnimatableManager instance being used for this animation processor
	 * @param animTime              The internal tick counter kept by the {@link AnimatableManager} for this animatable
	 * @param state                 An {@link AnimationState} instance applied to this render frame
	 * @param crashWhenCantFindBone Whether to crash if unable to find a required bone, or to continue with the remaining bones
	 */
	public void tickAnimation(T animatable, HitboxModel<T> model, AnimatableManager<T> animatableManager, double animTime, AnimationState<T> state, boolean crashWhenCantFindBone) {
		Map<String, BoneSnapshot> boneSnapshots = updateBoneSnapshots(animatableManager.getBoneSnapshotCollection());

		for (AnimationController<T> controller : animatableManager.getAnimationControllers().values()) {
			if (this.reloadAnimations) {
				controller.forceAnimationReset();
				controller.getBoneAnimationQueues().clear();
			}

			controller.isJustStarting = animatableManager.isFirstTick();

			state.withController(controller);
			controller.process(model, state, this.bones, boneSnapshots, animTime, crashWhenCantFindBone);

			for (BoneAnimationQueue boneAnimation : controller.getBoneAnimationQueues().values()) {
				GeoBone bone = boneAnimation.bone();
				BoneSnapshot snapshot = boneSnapshots.get(bone.getName());
				BoneSnapshot initialSnapshot = bone.getInitialSnapshot();

				AnimationPoint rotXPoint = boneAnimation.rotationXQueue().poll();
				AnimationPoint rotYPoint = boneAnimation.rotationYQueue().poll();
				AnimationPoint rotZPoint = boneAnimation.rotationZQueue().poll();
				AnimationPoint posXPoint = boneAnimation.positionXQueue().poll();
				AnimationPoint posYPoint = boneAnimation.positionYQueue().poll();
				AnimationPoint posZPoint = boneAnimation.positionZQueue().poll();
				AnimationPoint scaleXPoint = boneAnimation.scaleXQueue().poll();
				AnimationPoint scaleYPoint = boneAnimation.scaleYQueue().poll();
				AnimationPoint scaleZPoint = boneAnimation.scaleZQueue().poll();
				EasingType easingType = controller.overrideEasingTypeFunction.apply(animatable);

				if (rotXPoint != null && rotYPoint != null && rotZPoint != null) {
					bone.setRotX((float)EasingType.lerpWithOverride(rotXPoint, easingType) + initialSnapshot.getRotX());
					bone.setRotY((float)EasingType.lerpWithOverride(rotYPoint, easingType) + initialSnapshot.getRotY());
					bone.setRotZ((float)EasingType.lerpWithOverride(rotZPoint, easingType) + initialSnapshot.getRotZ());
					snapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
					snapshot.startRotAnim();
					bone.markRotationAsChanged();
				}

				if (posXPoint != null && posYPoint != null && posZPoint != null) {
					bone.setPosX((float)EasingType.lerpWithOverride(posXPoint, easingType));
					bone.setPosY((float)EasingType.lerpWithOverride(posYPoint, easingType));
					bone.setPosZ((float)EasingType.lerpWithOverride(posZPoint, easingType));
					snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
					snapshot.startPosAnim();
					bone.markPositionAsChanged();
				}

				if (scaleXPoint != null && scaleYPoint != null && scaleZPoint != null) {
					bone.setScaleX((float)EasingType.lerpWithOverride(scaleXPoint, easingType));
					bone.setScaleY((float)EasingType.lerpWithOverride(scaleYPoint, easingType));
					bone.setScaleZ((float)EasingType.lerpWithOverride(scaleZPoint, easingType));
					snapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
					snapshot.startScaleAnim();
					bone.markScaleAsChanged();
				}
			}
		}

		this.reloadAnimations = false;
		double resetTickLength = animatable.getBoneResetTime();

		for (GeoBone bone : getRegisteredBones()) {
			if (!bone.hasRotationChanged()) {
				BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
				BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

				if (saveSnapshot.isRotAnimInProgress())
					saveSnapshot.stopRotAnim(animTime);

				double percentageReset = Math.min((animTime - saveSnapshot.getLastResetRotationTick()) / resetTickLength, 1);

				bone.setRotX((float)Mth.lerp(percentageReset, saveSnapshot.getRotX(), initialSnapshot.getRotX()));
				bone.setRotY((float)Mth.lerp(percentageReset, saveSnapshot.getRotY(), initialSnapshot.getRotY()));
				bone.setRotZ((float)Mth.lerp(percentageReset, saveSnapshot.getRotZ(), initialSnapshot.getRotZ()));

				if (percentageReset >= 1)
					saveSnapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
			}

			if (!bone.hasPositionChanged()) {
				BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
				BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

				if (saveSnapshot.isPosAnimInProgress())
					saveSnapshot.stopPosAnim(animTime);

				double percentageReset = Math.min((animTime - saveSnapshot.getLastResetPositionTick()) / resetTickLength, 1);

				bone.setPosX((float)Mth.lerp(percentageReset, saveSnapshot.getOffsetX(), initialSnapshot.getOffsetX()));
				bone.setPosY((float)Mth.lerp(percentageReset, saveSnapshot.getOffsetY(), initialSnapshot.getOffsetY()));
				bone.setPosZ((float)Mth.lerp(percentageReset, saveSnapshot.getOffsetZ(), initialSnapshot.getOffsetZ()));

				if (percentageReset >= 1)
					saveSnapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
			}

			if (!bone.hasScaleChanged()) {
				BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
				BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

				if (saveSnapshot.isScaleAnimInProgress())
					saveSnapshot.stopScaleAnim(animTime);

				double percentageReset = Math.min((animTime - saveSnapshot.getLastResetScaleTick()) / resetTickLength, 1);

				bone.setScaleX((float)Mth.lerp(percentageReset, saveSnapshot.getScaleX(), initialSnapshot.getScaleX()));
				bone.setScaleY((float)Mth.lerp(percentageReset, saveSnapshot.getScaleY(), initialSnapshot.getScaleY()));
				bone.setScaleZ((float)Mth.lerp(percentageReset, saveSnapshot.getScaleZ(), initialSnapshot.getScaleZ()));

				if (percentageReset >= 1)
					saveSnapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
			}
		}

		resetBoneTransformationMarkers();
		animatableManager.finishFirstTick();
	}

	/**
	 * Reset the transformation markers applied to each {@link GeoBone} ready for the next render frame
	 */
	private void resetBoneTransformationMarkers() {
		getRegisteredBones().forEach(GeoBone::resetStateChanges);
	}

	/**
	 * Create new bone {@link BoneSnapshot} based on the bone's initial snapshot for the currently registered {@link GeoBone GeoBones},
	 * filtered by the bones already present in the master snapshots map
	 *
	 * @param snapshots The master bone snapshots map from the related {@link AnimatableManager}
	 * @return The input snapshots map, for easy assignment
	 */
	private Map<String, BoneSnapshot> updateBoneSnapshots(Map<String, BoneSnapshot> snapshots) {
		for (GeoBone bone : getRegisteredBones()) {
			if (!snapshots.containsKey(bone.getName()))
				snapshots.put(bone.getName(), BoneSnapshot.copy(bone.getInitialSnapshot()));
		}

		return snapshots;
	}

	/**
	 * Gets a bone by name
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	public GeoBone getBone(String boneName) {
		return this.bones.get(boneName);
	}

	/**
	 * Adds the given bone to the bones list for this processor
	 * <p>
	 * This is normally handled automatically by Geckolib
	 * <p>
	 * Failure to properly register a bone will break things.
	 */
	public void registerGeoBone(GeoBone bone) {
		bone.saveInitialSnapshot();
		this.bones.put(bone.getName(), bone);

		for (GeoBone child : bone.getChildBones()) {
			registerGeoBone(child);
		}
	}

	/**
	 * Clear the {@link GeoBone GeoBones} currently registered to the processor,
	 * then prepares the processor for a new model
	 * <p>
	 * Should be called whenever switching models to render/animate
	 */
	public void setActiveModel(BakedHitboxModel model) {
		this.bones.clear();

		for (GeoBone bone : model.topLevelBones()) {
			registerGeoBone(bone);
		}
	}

	/**
	 * Get an iterable collection of the {@link GeoBone GeoBones} currently registered to the processor
	 */
	public Collection<GeoBone> getRegisteredBones() {
		return this.bones.values();
	}

	/**
	 * Apply transformations and settings prior to acting on any animation-related functionality
	 */
	public void preAnimationSetup(AnimationState<T> animationState, double animTime) {
		this.model.applyMolangQueries(animationState, animTime);
	}

	/**
	 * {@link Animation} and {@link Animation.LoopType} override pair,
	 * used to define a playable animation stage for a {@link AdvancedHitboxEntity}
	 */
	public record QueuedAnimation(Animation animation, Animation.LoopType loopType) {}
}
