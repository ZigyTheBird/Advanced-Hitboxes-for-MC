package com.zigythebird.advanced_hitboxes.entity;

import com.zigythebird.advanced_hitboxes.accessor.EntityAccessor;
import com.zigythebird.advanced_hitboxes.animation.HitboxAnimManager;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxBone;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.playeranimcore.animation.layered.AnimationStack;
import net.minecraft.world.entity.Entity;

import java.util.List;

public interface AdvancedHitboxEntity {
    default List<AdvancedHitbox> getHitboxes() {
        return ((EntityAccessor)this).advanced_Hitboxes$getHitboxes();
    }

    HitboxModel getHitboxModel();

    //TODO Add java docs
    default void registerHitboxControllers(HitboxAnimManager manager) {}

    /**
     * Defines whether the animations for this animatable should continue playing in the background when the game is paused
     * <p>
     * By default, animation progress will be stalled while the game is paused.
     */
    default boolean advanced_hitboxes$shouldPlayAnimsWhileGamePaused() {
        return false;
    }

    default double advanced_hitboxes$getTick(Object entity) {
        return ((Entity)entity).tickCount;
    }

    default boolean useAdvancedHitboxesForCollision() {
        return false;
    }

    default boolean useHitboxForCollision(String name) {
        return true;
    }
}
