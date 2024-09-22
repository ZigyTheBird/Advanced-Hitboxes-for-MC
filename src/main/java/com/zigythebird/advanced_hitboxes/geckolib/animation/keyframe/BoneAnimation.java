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

import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathValue;

/**
 * A record of a deserialized animation for a given bone
 * <p>
 * Responsible for holding the various {@link Keyframe Keyframes} for the bone's animation transformations
 *
 * @param boneName The name of the bone as listed in the {@code animation.json}
 * @param rotationKeyFrames The deserialized rotation {@code Keyframe} stack
 * @param positionKeyFrames The deserialized position {@code Keyframe} stack
 * @param scaleKeyFrames The deserialized scale {@code Keyframe} stack
 */
public record BoneAnimation(String boneName,
                            KeyframeStack<Keyframe<MathValue>> rotationKeyFrames,
                            KeyframeStack<Keyframe<MathValue>> positionKeyFrames,
                            KeyframeStack<Keyframe<MathValue>> scaleKeyFrames) {
}
