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
