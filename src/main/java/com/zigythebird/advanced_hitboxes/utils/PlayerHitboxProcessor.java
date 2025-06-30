package com.zigythebird.advanced_hitboxes.utils;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.HitboxAnimationController;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxGeoBone;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PlayerHitboxProcessor {
    public static void tickAnimation(Player player, HitboxGeoBone bone) {
        AdvancedHitboxEntity animatable = (AdvancedHitboxEntity) player;
        HitboxAnimationController controller = animatable.getHitboxInstanceCache().getManagerForId(player.getId()).getAnimationControllers().get("base_controller");
        if (controller.getAnimationState() != HitboxAnimationController.State.STOPPED) return;

        float tickDelta = CommonSideUtils.getTickDelta(player.level());

        if (bone.getName().equals("head_hitbox")) animateHead(player, bone, tickDelta);
    }

    public static void animateHead(Player player, HitboxGeoBone bone, float tickDelta) {
        float lerpedBodyRot = Mth.rotLerp(tickDelta, CommonSideUtils.getBodyYRot0(player), CommonSideUtils.getBodyYRot(player));
        bone.setRotY(Mth.wrapDegrees(player.getYRot() - lerpedBodyRot) * -Mth.DEG_TO_RAD);
        bone.setRotX(-player.getXRot() * Mth.DEG_TO_RAD);
        if (player.isCrouching()) bone.setPosY((float) -4.2);
    }
}
