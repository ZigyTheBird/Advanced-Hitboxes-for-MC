package com.zigythebird.advanced_hitboxes.entity;

import com.zigythebird.advanced_hitboxes.geckolib.animation.AnimationProcessor;
import com.zigythebird.advanced_hitboxes.geckolib.animation.HitboxAnimatableManager;
import com.zigythebird.advanced_hitboxes.geckolib.animation.HitboxAnimationController;
import com.zigythebird.advanced_hitboxes.geckolib.animation.PlayState;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxGeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.instance.AdvancedHitboxInstanceCache;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.interfaces.EntityInterface;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import net.minecraft.world.entity.Entity;

import java.util.List;

public interface AdvancedHitboxEntity {
    default List<AdvancedHitbox> getHitboxes() {
        return ((EntityInterface)this).advanced_Hitboxes$getHitboxes();
    }

    HitboxModel getHitboxModel();

    /**
     * Register your {@link HitboxAnimationController AnimationControllers} and their respective animations and conditions
     * <p>
     * Override this method in your animatable object and add your controllers via {@link HitboxAnimatableManager.ControllerRegistrar#add ControllerRegistrar.add}
     * <p>
     * You may add as many controllers as wanted
     * <p>
     * Each controller can only play <u>one</u> animation at a time, and so animations that you intend to play concurrently should be handled in independent controllers
     * <p>
     * Note having multiple animations playing via multiple controllers can override parts of one animation with another if both animations use the same bones or child bones
     *
     * @param controllers The object to register your controller instances to
     */
    default void advanced_hitboxes$registerControllers(HitboxAnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new HitboxAnimationController<>(this, 0, state -> PlayState.STOP));
    }

    /**
     * Defines the speed in which the {@link AnimationProcessor}
     * should return {@link HitboxGeoBone GeoBones} that currently have no animations to their default position
     */
    default double advanced_hitboxes$getBoneResetTime() {
        return 1;
    }

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

    /**
     * Each instance of a {@code AdvancedHitboxEntity} must return an instance of an {@link AdvancedHitboxInstanceCache}, which handles instance-specific animation info
     * <p>
     * Generally speaking, you should create your cache using {@code GeckoLibUtil#createCache} and store it in your animatable instance, returning that cached instance when called
     *
     * @return A cached instance of an {@code AnimatableInstanceCache}
     */
    AdvancedHitboxInstanceCache advanced_hitboxes$getAnimatableInstanceCache();

    /**
     * Override the default handling for instantiating an AnimatableInstanceCache for this animatable
     * <p>
     * Don't override this unless you know what you're doing.
     */
    default AdvancedHitboxInstanceCache advanced_hitboxes$animatableCacheOverride() {
        return null;
    }

    default boolean useAdvancedHitboxesForCollision() {
        return false;
    }

    default boolean useHitboxForCollision(String name) {
        return true;
    }

    default void applyTransformationsToBone(HitboxGeoBone bone, boolean animPlaying) {}
}
