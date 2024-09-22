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

/**
 * Animation state record that holds the state of an animation at a given point
 *
 * @param currentTick The lerped tick time (current tick + partial tick) of the point
 * @param transitionLength The length of time (in ticks) that the point should take to transition
 * @param animationStartValue The start value to provide to the animation handling system
 * @param animationEndValue The end value to provide to the animation handling system
 * @param keyFrame The {@code Nullable} Keyframe
 */
public record AnimationPoint(Keyframe<?> keyFrame, double currentTick, double transitionLength, double animationStartValue, double animationEndValue) {
	@Override
	public String toString() {
		return "Tick: " + this.currentTick +
				" | Transition Length: " + this.transitionLength +
				" | Start Value: " + this.animationStartValue +
				" | End Value: " + this.animationEndValue;
	}
}
