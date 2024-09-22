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

package com.zigythebird.advanced_hitboxes.geckolib.util;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.Animation;
import com.zigythebird.advanced_hitboxes.geckolib.animation.EasingType;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.BakedModelFactory;
import com.zigythebird.advanced_hitboxes.geckolib.instance.AdvancedHitboxInstanceCache;
import com.zigythebird.advanced_hitboxes.geckolib.instance.InstancedAdvancedHitboxInstanceCache;

/**
 * Helper class for various GeckoLib-specific functions.
 */
public final class HitboxModelUtil {
    /**
     * Creates a new AnimatableInstanceCache for the given animatable object
     * <p>
     * Recommended to use {@link HitboxModelUtil#createInstanceCache(AdvancedHitboxEntity)} unless you know what you're doing
     *
     * @param animatable The animatable object
     */
    public static AdvancedHitboxInstanceCache createInstanceCache(AdvancedHitboxEntity animatable) {
        AdvancedHitboxInstanceCache cache = animatable.animatableCacheOverride();

        if (cache != null)
            return cache;

        return new InstancedAdvancedHitboxInstanceCache(animatable);
    }

    /**
     * Register a custom {@link Animation.LoopType} with GeckoLib, allowing for dynamic handling of post-animation looping
     * <p>
     * <b><u>MUST be called during mod construct</u></b>
     *
     * @param name The name of the {@code LoopType} handler
     * @param loopType The {@code LoopType} implementation to use for the given name
     */
    synchronized public static Animation.LoopType addCustomLoopType(String name, Animation.LoopType loopType) {
        return Animation.LoopType.register(name, loopType);
    }

    /**
     * Register a custom {@link EasingType} with GeckoLib allowing for dynamic handling of animation transitions and curves
     * <p>
     * <b><u>MUST be called during mod construct</u></b>
     *
     * @param name The name of the {@code EasingType} handler
     * @param easingType The {@code EasingType} implementation to use for the given name
     */
    synchronized public static EasingType addCustomEasingType(String name, EasingType easingType) {
        return EasingType.register(name, easingType);
    }

    /**
     * Register a custom {@link BakedModelFactory} with GeckoLib, allowing for dynamic handling of geo model loading
     * <p>
     * <b><u>MUST be called during mod construct</u></b>
     *
     * @param namespace The namespace (modid) to register the factory for
     * @param factory The factory responsible for model loading under the given namespace
     */
    synchronized public static void addCustomBakedModelFactory(String namespace, BakedModelFactory factory) {
        BakedModelFactory.register(namespace, factory);
    }
}
