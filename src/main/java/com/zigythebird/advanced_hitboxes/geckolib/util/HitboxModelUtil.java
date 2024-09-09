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
