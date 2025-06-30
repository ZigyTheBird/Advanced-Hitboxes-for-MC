package com.zigythebird.advanced_hitboxes.mixin.client;

import com.zigythebird.advanced_hitboxes.compat.paapi.PlayerAnimatorAPICompat;
import com.zigythebird.playeranimatorapi.data.PlayerAnimationData;
import com.zigythebird.playeranimatorapi.playeranims.PlayerAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerAnimations.class)
public class PlayerAnimationsMixin_playeranimatorapiOnly {
    @Inject(method = "playAnimation(Lcom/zigythebird/playeranimatorapi/data/PlayerAnimationData;)V", at = @At("TAIL"))
    private static void playAnimation(PlayerAnimationData data, CallbackInfo ci) {
        PlayerAnimatorAPICompat.onAnimationPlayed(Minecraft.getInstance().level.getPlayerByUUID(data.playerUUID()), data);
    }

    @Inject(method = "stopAnimation(Ljava/util/UUID;Lnet/minecraft/resources/ResourceLocation;)V", at = @At("TAIL"))
    private static void stopAnimation(UUID playerUUID, ResourceLocation animationID, CallbackInfo ci) {
        PlayerAnimatorAPICompat.onAnimationStopped(Minecraft.getInstance().level.getPlayerByUUID(playerUUID), animationID);
    }
}
