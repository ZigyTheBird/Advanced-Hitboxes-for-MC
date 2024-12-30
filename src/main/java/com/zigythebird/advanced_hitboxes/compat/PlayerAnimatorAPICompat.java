package com.zigythebird.advanced_hitboxes.compat;

import com.zigythebird.advanced_hitboxes.geckolib.animation.Animation;
import com.zigythebird.advanced_hitboxes.geckolib.animation.HitboxAnimationController;
import com.zigythebird.advanced_hitboxes.geckolib.cache.HitboxCache;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.playeranimatorapi.data.PlayerAnimationData;
import com.zigythebird.playeranimatorapi.modifier.CommonModifier;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class PlayerAnimatorAPICompat {
    public static void onAnimationPlayed(Player player, PlayerAnimationData data) {
        HitboxAnimationController controller = ((AdvancedHitboxEntity)player).advanced_hitboxes$getAnimatableInstanceCache().getManagerForId(player.getId()).getAnimationControllers().get("base_controller");
        String name = data.animationID().toString();
        Map<String, Animation> animations = HitboxCache.getBakedAnimations().get(HitboxCache.PLAYER_ANIMATION_RESOURCE).animations();
        if (animations.containsKey(name)) {
            double speed = 1;
            for (CommonModifier modifier : data.modifiers()) {
                if (modifier.ID.toString().equals("player-animator:speed")) {
                    speed *= modifier.data.get("speed").getAsFloat();
                }
                else if (modifier.ID.toString().equals("playeranimatorapi:length")) {
                    speed *= (modifier.data.get("desiredLength").getAsFloat() / animations.get(name).length());
                }
            }
            controller.tryTriggerAnimation(new PAAPIRawAnimation(name, speed, data.priority(), data.parts()));
        }
    }
}
