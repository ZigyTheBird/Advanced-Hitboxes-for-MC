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

package com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.event;

import com.zigythebird.advanced_hitboxes.interfaces.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.HitboxAnimationController;
import com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.Keyframe;
import com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.event.data.KeyFrameData;
/**
 * The base class for {@link Keyframe} events
 * <p>
 * These will be passed to one of the controllers in {@link HitboxAnimationController} when encountered during animation
 *
 * @see CustomInstructionKeyframeEvent
 * @see ParticleKeyframeEvent
 * @see SoundKeyframeEvent
 */
public abstract class KeyFrameEvent<T extends AdvancedHitboxEntity, E extends KeyFrameData> {
	private final T animatable;
	private final double animationTick;
	private final HitboxAnimationController<T> controller;
	private final E eventKeyFrame;

	public KeyFrameEvent(T animatable, double animationTick, HitboxAnimationController<T> controller, E eventKeyFrame) {
		this.animatable = animatable;
		this.animationTick = animationTick;
		this.controller = controller;
		this.eventKeyFrame = eventKeyFrame;
	}

	/**
	 * Gets the amount of ticks that have passed in either the current transition or
	 * animation, depending on the controller's AnimationState.
	 */
	public double getAnimationTick() {
		return animationTick;
	}

	/**
	 * Gets the {@link AdvancedHitboxEntity} object being rendered
	 */
	public T getAnimatable() {
		return animatable;
	}

	/**
	 * Gets the {@link HitboxAnimationController} responsible for the currently playing animation
	 */
	public HitboxAnimationController<T> getController() {
		return controller;
	}

	/**
	 * Returns the {@link KeyFrameData} relevant to the encountered {@link Keyframe}
	 */
	public E getKeyframeData() {
		return this.eventKeyFrame;
	}
}
