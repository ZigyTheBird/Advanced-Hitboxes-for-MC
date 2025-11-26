package com.zigythebird.advanced_hitboxes.geckolib.cache.object;

import com.zigythebird.playeranimcore.animation.Animation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Container object that holds a deserialized map of {@link Animation Animations}
 */
public record BakedAnimations(Map<String, Animation> animations) {
    /**
     * Gets an {@link Animation} by its name, if present
     */
    @Nullable
    public Animation getAnimation(String name) {
        return animations.get(name);
    }
}
