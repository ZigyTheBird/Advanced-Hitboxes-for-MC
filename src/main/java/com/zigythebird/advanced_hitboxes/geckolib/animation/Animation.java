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

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.BoneAnimation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A compiled animation instance for use by the {@link HitboxAnimationController}
 * <p>
 * Modifications or extensions of a compiled Animation are not supported, and therefore an instance of <code>Animation</code> is considered final and immutable
 */
public record Animation(String name, double length, LoopType loopType, BoneAnimation[] boneAnimations) {

    static Animation generateWaitAnimation(double length) {
        return new Animation(RawAnimation.Stage.WAIT, length, LoopType.PLAY_ONCE, new BoneAnimation[0]);
    }

    /**
     * Loop type functional interface to define post-play handling for a given animation
     * <p>
     * Custom loop types are supported by extending this class and providing the extended class instance as the loop type for the animation
     */
    @FunctionalInterface
    public interface LoopType {
        Map<String, LoopType> LOOP_TYPES = new ConcurrentHashMap<>(4);

        LoopType DEFAULT = (animatable, controller, currentAnimation) -> currentAnimation.loopType().shouldPlayAgain(animatable, controller, currentAnimation);
        LoopType PLAY_ONCE = register("play_once", register("false", (animatable, controller, currentAnimation) -> false));
        LoopType HOLD_ON_LAST_FRAME = register("hold_on_last_frame", (animatable, controller, currentAnimation) -> {
            controller.animationState = HitboxAnimationController.State.PAUSED;

            return true;
        });
        LoopType LOOP = register("loop", register("true", (animatable, controller, currentAnimation) -> true));

        /**
         * Override in a custom instance to dynamically decide whether an animation should repeat or stop
         *
         * @param animatable The animating object relevant to this method call
         * @param controller The {@link HitboxAnimationController} playing the current animation
         * @param currentAnimation The current animation that just played
         * @return Whether the animation should play again, or stop
         */
        boolean shouldPlayAgain(AdvancedHitboxEntity animatable, HitboxAnimationController<? extends AdvancedHitboxEntity> controller, Animation currentAnimation);

        /**
         * Retrieve a LoopType instance based on a {@link JsonElement}
         * <p>
         * Returns either {@link LoopType#PLAY_ONCE} or {@link LoopType#LOOP} based on a boolean or string element type,
         * or any other registered loop type with a matching type string
         *
         * @param json The <code>loop</code> {@link JsonElement} to attempt to parse
         * @return A usable LoopType instance
         */
        static LoopType fromJson(JsonElement json) {
            if (json == null || !json.isJsonPrimitive())
                return PLAY_ONCE;

            JsonPrimitive primitive = json.getAsJsonPrimitive();

            if (primitive.isBoolean())
                return primitive.getAsBoolean() ? LOOP : PLAY_ONCE;

            if (primitive.isString())
                return fromString(primitive.getAsString());

            return PLAY_ONCE;
        }

        static LoopType fromString(String name) {
            return LOOP_TYPES.getOrDefault(name, PLAY_ONCE);
        }

        /**
         * Register a LoopType with Geckolib for handling loop functionality of animations
         * <p>
         * <b><u>MUST be called during mod construct</u></b>
         * <p>
         * It is recommended you don't call this directly, and instead call it via {@code GeckoLibUtil#addCustomLoopType}
         *
         * @param name The name of the loop type
         * @param loopType The loop type to register
         * @return The registered {@code LoopType}
         */
        static LoopType register(String name, LoopType loopType) {
            LOOP_TYPES.put(name, loopType);

            return loopType;
        }
    }
}
