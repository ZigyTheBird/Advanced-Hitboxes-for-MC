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

package com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe;

import com.zigythebird.advanced_hitboxes.geckolib.animation.HitboxAnimationController;
import com.zigythebird.advanced_hitboxes.geckolib.animation.state.BoneSnapshot;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxGeoBone;

/**
 * A bone pseudo-stack for bone animation positions, scales, and rotations
 * <p>
 * Animation points are calculated then pushed onto their respective queues to be used for transformations in rendering
 */
public record BoneAnimationQueue(HitboxGeoBone bone, AnimationPointQueue rotationXQueue, AnimationPointQueue rotationYQueue,
                                 AnimationPointQueue rotationZQueue, AnimationPointQueue positionXQueue, AnimationPointQueue positionYQueue,
                                 AnimationPointQueue positionZQueue, AnimationPointQueue scaleXQueue, AnimationPointQueue scaleYQueue,
                                 AnimationPointQueue scaleZQueue) {
	public BoneAnimationQueue(HitboxGeoBone bone) {
		this(bone, new AnimationPointQueue(), new AnimationPointQueue(), new AnimationPointQueue(),
				new AnimationPointQueue(), new AnimationPointQueue(), new AnimationPointQueue(),
				new AnimationPointQueue(), new AnimationPointQueue(), new AnimationPointQueue());
	}

	/**
	 * Add a new {@link AnimationPoint} to the {@link BoneAnimationQueue#positionXQueue}
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (based on the {@link HitboxAnimationController})
	 * @param startValue The value of the point at the start of its transition
	 * @param endValue The value of the point at the end of its transition
	 */
	public void addPosXPoint(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, double startValue, double endValue) {
		this.positionXQueue.add(new AnimationPoint(keyFrame, lerpedTick, transitionLength, startValue, endValue));
	}

	/**
	 * Add a new {@link AnimationPoint} to the {@link BoneAnimationQueue#positionYQueue}
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (based on the {@link HitboxAnimationController})
	 * @param startValue The value of the point at the start of its transition
	 * @param endValue The value of the point at the end of its transition
	 */
	public void addPosYPoint(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, double startValue, double endValue) {
		this.positionYQueue.add(new AnimationPoint(keyFrame, lerpedTick, transitionLength, startValue, endValue));
	}

	/**
	 * Add a new {@link AnimationPoint} to the {@link BoneAnimationQueue#positionZQueue}
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (based on the {@link HitboxAnimationController})
	 * @param startValue The value of the point at the start of its transition
	 * @param endValue The value of the point at the end of its transition
	 */
	public void addPosZPoint(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, double startValue, double endValue) {
		this.positionZQueue.add(new AnimationPoint(keyFrame, lerpedTick, transitionLength, startValue, endValue));
	}

	/**
	 * Add a new X, Y, and Z position {@link AnimationPoint} to their respective queues
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (base on the {@link HitboxAnimationController}
	 * @param startSnapshot The {@link BoneSnapshot} that serves as the starting positions relevant to the keyframe provided
	 * @param nextXPoint The X {@code AnimationPoint} that is next in the queue, to serve as the end value of the new point
	 * @param nextYPoint The Y {@code AnimationPoint} that is next in the queue, to serve as the end value of the new point
	 * @param nextZPoint The Z {@code AnimationPoint} that is next in the queue, to serve as the end value of the new point
	 */
	public void addNextPosition(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, BoneSnapshot startSnapshot, AnimationPoint nextXPoint, AnimationPoint nextYPoint, AnimationPoint nextZPoint) {
		addPosXPoint(keyFrame, lerpedTick, transitionLength, startSnapshot.getOffsetX(), nextXPoint.animationStartValue());
		addPosYPoint(keyFrame, lerpedTick, transitionLength, startSnapshot.getOffsetY(), nextYPoint.animationStartValue());
		addPosZPoint(keyFrame, lerpedTick, transitionLength, startSnapshot.getOffsetZ(), nextZPoint.animationStartValue());
	}

	/**
	 * Add a new {@link AnimationPoint} to the {@link BoneAnimationQueue#scaleXQueue}
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (based on the {@link HitboxAnimationController})
	 * @param startValue The value of the point at the start of its transition
	 * @param endValue The value of the point at the end of its transition
	 */
	public void addScaleXPoint(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, double startValue, double endValue) {
		this.scaleXQueue.add(new AnimationPoint(keyFrame, lerpedTick, transitionLength, startValue, endValue));
	}

	/**
	 * Add a new {@link AnimationPoint} to the {@link BoneAnimationQueue#scaleYQueue}
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (based on the {@link HitboxAnimationController})
	 * @param startValue The value of the point at the start of its transition
	 * @param endValue The value of the point at the end of its transition
	 */
	public void addScaleYPoint(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, double startValue, double endValue) {
		this.scaleYQueue.add(new AnimationPoint(keyFrame, lerpedTick, transitionLength, startValue, endValue));
	}

	/**
	 * Add a new {@link AnimationPoint} to the {@link BoneAnimationQueue#scaleZQueue}
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (based on the {@link HitboxAnimationController})
	 * @param startValue The value of the point at the start of its transition
	 * @param endValue The value of the point at the end of its transition
	 */
	public void addScaleZPoint(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, double startValue, double endValue) {
		this.scaleZQueue.add(new AnimationPoint(keyFrame, lerpedTick, transitionLength, startValue, endValue));
	}

	/**
	 * Add a new X, Y, and Z scale {@link AnimationPoint} to their respective queues
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (base on the {@link HitboxAnimationController}
	 * @param startSnapshot The {@link BoneSnapshot} that serves as the starting scales relevant to the keyframe provided
	 * @param nextXPoint The X {@code AnimationPoint} that is next in the queue, to serve as the end value of the new point
	 * @param nextYPoint The Y {@code AnimationPoint} that is next in the queue, to serve as the end value of the new point
	 * @param nextZPoint The Z {@code AnimationPoint} that is next in the queue, to serve as the end value of the new point
	 */
	public void addNextScale(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, BoneSnapshot startSnapshot, AnimationPoint nextXPoint, AnimationPoint nextYPoint, AnimationPoint nextZPoint) {
		addScaleXPoint(keyFrame, lerpedTick, transitionLength, startSnapshot.getScaleX(), nextXPoint.animationStartValue());
		addScaleYPoint(keyFrame, lerpedTick, transitionLength, startSnapshot.getScaleY(), nextYPoint.animationStartValue());
		addScaleZPoint(keyFrame, lerpedTick, transitionLength, startSnapshot.getScaleZ(), nextZPoint.animationStartValue());
	}

	/**
	 * Add a new {@link AnimationPoint} to the {@link BoneAnimationQueue#rotationXQueue}
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (based on the {@link HitboxAnimationController})
	 * @param startValue The value of the point at the start of its transition
	 * @param endValue The value of the point at the end of its transition
	 */
	public void addRotationXPoint(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, double startValue, double endValue) {
		this.rotationXQueue.add(new AnimationPoint(keyFrame, lerpedTick, transitionLength, startValue, endValue));
	}

	/**
	 * Add a new {@link AnimationPoint} to the {@link BoneAnimationQueue#rotationYQueue}
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (based on the {@link HitboxAnimationController})
	 * @param startValue The value of the point at the start of its transition
	 * @param endValue The value of the point at the end of its transition
	 */
	public void addRotationYPoint(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, double startValue, double endValue) {
		this.rotationYQueue.add(new AnimationPoint(keyFrame, lerpedTick, transitionLength, startValue, endValue));
	}

	/**
	 * Add a new {@link AnimationPoint} to the {@link BoneAnimationQueue#rotationZQueue}
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (based on the {@link HitboxAnimationController})
	 * @param startValue The value of the point at the start of its transition
	 * @param endValue The value of the point at the end of its transition
	 */
	public void addRotationZPoint(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, double startValue, double endValue) {
		this.rotationZQueue.add(new AnimationPoint(keyFrame, lerpedTick, transitionLength, startValue, endValue));
	}

	/**
	 * Add a new X, Y, and Z scale {@link AnimationPoint} to their respective queues
	 *
	 * @param keyFrame The {@code Nullable} Keyframe relevant to the animation point
	 * @param lerpedTick The lerped time (current tick + partial tick) that the point starts at
	 * @param transitionLength The length of the transition (base on the {@link HitboxAnimationController}
	 * @param startSnapshot The {@link BoneSnapshot} that serves as the starting rotations relevant to the keyframe provided
	 * @param initialSnapshot The {@link BoneSnapshot} that serves as the unmodified rotations of the bone
	 * @param nextXPoint The X {@code AnimationPoint} that is next in the queue, to serve as the end value of the new point
	 * @param nextYPoint The Y {@code AnimationPoint} that is next in the queue, to serve as the end value of the new point
	 * @param nextZPoint The Z {@code AnimationPoint} that is next in the queue, to serve as the end value of the new point
	 */
	public void addNextRotation(Keyframe<?> keyFrame, double lerpedTick, double transitionLength, BoneSnapshot startSnapshot, BoneSnapshot initialSnapshot, AnimationPoint nextXPoint, AnimationPoint nextYPoint, AnimationPoint nextZPoint) {
		addRotationXPoint(keyFrame, lerpedTick, transitionLength, startSnapshot.getRotX() - initialSnapshot.getRotX(), nextXPoint.animationStartValue());
		addRotationYPoint(keyFrame, lerpedTick, transitionLength, startSnapshot.getRotY() - initialSnapshot.getRotY(), nextYPoint.animationStartValue());
		addRotationZPoint(keyFrame, lerpedTick, transitionLength, startSnapshot.getRotZ() - initialSnapshot.getRotZ(), nextZPoint.animationStartValue());
	}

	/**
	 * Add an X, Y, and Z position {@link AnimationPoint} to their respective queues
	 *
	 * @param xPoint The x position {@code AnimationPoint} to add
	 * @param yPoint The y position {@code AnimationPoint} to add
	 * @param zPoint The z position {@code AnimationPoint} to add
	 */
	public void addPositions(AnimationPoint xPoint, AnimationPoint yPoint, AnimationPoint zPoint) {
		this.positionXQueue.add(xPoint);
		this.positionYQueue.add(yPoint);
		this.positionZQueue.add(zPoint);
	}

	/**
	 * Add an X, Y, and Z scale {@link AnimationPoint} to their respective queues
	 *
	 * @param xPoint The x scale {@code AnimationPoint} to add
	 * @param yPoint The y scale {@code AnimationPoint} to add
	 * @param zPoint The z scale {@code AnimationPoint} to add
	 */
	public void addScales(AnimationPoint xPoint, AnimationPoint yPoint, AnimationPoint zPoint) {
		this.scaleXQueue.add(xPoint);
		this.scaleYQueue.add(yPoint);
		this.scaleZQueue.add(zPoint);
	}

	/**
	 * Add an X, Y, and Z rotation {@link AnimationPoint} to their respective queues
	 *
	 * @param xPoint The x rotation {@code AnimationPoint} to add
	 * @param yPoint The y rotation {@code AnimationPoint} to add
	 * @param zPoint The z rotation {@code AnimationPoint} to add
	 */
	public void addRotations(AnimationPoint xPoint, AnimationPoint yPoint, AnimationPoint zPoint) {
		this.rotationXQueue.add(xPoint);
		this.rotationYQueue.add(yPoint);
		this.rotationZQueue.add(zPoint);
	}
}
