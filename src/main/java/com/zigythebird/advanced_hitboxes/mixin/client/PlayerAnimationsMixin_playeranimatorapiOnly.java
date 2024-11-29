package com.zigythebird.advanced_hitboxes.mixin.client;

import com.zigythebird.advanced_hitboxes.compat.PlayerAnimatorAPICompat;
import com.zigythebird.playeranimatorapi.data.PlayerAnimationData;
import com.zigythebird.playeranimatorapi.data.PlayerParts;
import com.zigythebird.playeranimatorapi.modifier.CommonModifier;
import com.zigythebird.playeranimatorapi.playeranims.PlayerAnimations;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerAnimations.class)
public class PlayerAnimationsMixin_playeranimatorapiOnly {
    @Inject(method = "playAnimation(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/zigythebird/playeranimatorapi/data/PlayerAnimationData;Lcom/zigythebird/playeranimatorapi/data/PlayerParts;Ljava/util/List;IIZZ)V", at = @At("TAIL"))
    private static void playAnimation(AbstractClientPlayer player, PlayerAnimationData data, PlayerParts parts, List<CommonModifier> modifiers, int fadeLength, int easeID, boolean firstPersonEnabled, boolean replaceTick, CallbackInfo ci) {
        PlayerAnimatorAPICompat.onAnimationPlayed(player, data);
    }
}
