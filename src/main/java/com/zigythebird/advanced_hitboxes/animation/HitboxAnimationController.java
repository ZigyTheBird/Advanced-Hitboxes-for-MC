package com.zigythebird.advanced_hitboxes.animation;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxBone;
import com.zigythebird.advanced_hitboxes.utils.ModMath;
import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class HitboxAnimationController extends AnimationController {
    protected final AdvancedHitboxEntity entity;

    /**
     * Instantiates a new {@code AnimationController}
     *
     * @param entity           The object that will be animated by this controller
     * @param animationHandler The {@link AnimationStateHandler} animation state handler responsible for deciding which animations to play
     */
    public HitboxAnimationController(AdvancedHitboxEntity entity, AnimationStateHandler animationHandler) {
        super(animationHandler, MolangLoader::createNewEngine);
        this.entity = entity;
    }

    public AdvancedHitboxEntity getPlayer() {
        return this.entity;
    }

    public boolean triggerAnimation(ResourceLocation newAnimation, float startAnimFrom) {
        if (PlayerAnimResources.hasAnimation(newAnimation)) {
            triggerAnimation(PlayerAnimResources.getAnimation(newAnimation), startAnimFrom);
            return true;
        }
        return false;
    }

    public boolean triggerAnimation(ResourceLocation newAnimation) {
        return triggerAnimation(newAnimation, 0);
    }

    public boolean replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable ResourceLocation newAnimation, boolean fadeFromNothing) {
        if (PlayerAnimResources.hasAnimation(newAnimation)) {
            replaceAnimationWithFade(fadeModifier, PlayerAnimResources.getAnimation(newAnimation), fadeFromNothing);
            return true;
        }
        return false;
    }

    public boolean replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable ResourceLocation newAnimation) {
        return replaceAnimationWithFade(fadeModifier, newAnimation, true);
    }

    @Override
    public void registerBones() {
        //TODO Replace with bones from the model so it works for all entities
        this.registerPlayerAnimBone("body");
        this.registerTopPlayerAnimBone("right_arm");
        this.registerTopPlayerAnimBone("left_arm");
        this.registerPlayerAnimBone("right_leg");
        this.registerPlayerAnimBone("left_leg");
        this.registerTopPlayerAnimBone("head");
        this.registerPlayerAnimBone("torso");
        this.registerPlayerAnimBone("right_item");
        this.registerPlayerAnimBone("left_item");
        this.registerTopPlayerAnimBone("cape");
        this.registerPlayerAnimBone("elytra");
    }

    public void registerTopPlayerAnimBone(String name) {
        this.registerPlayerAnimBone(name);
    }

    @Override
    public Vec3f getBonePosition(String name) {
        Optional<HitboxBone> bone = this.entity.getHitboxModel().getBone(name);
        if (bone.isPresent()) return ModMath.vec3ToVec3f(bone.get().getPivot());
        if (pivotBones.containsKey(name)) return pivotBones.get(name).getPivot();
        return Vec3f.ZERO;
    }
}
