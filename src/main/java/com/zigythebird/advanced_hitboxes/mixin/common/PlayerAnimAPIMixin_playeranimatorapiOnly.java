package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.compat.paapi.PlayerAnimatorAPICompat;
import com.zigythebird.playeranimatorapi.API.PlayerAnimAPI;
import com.zigythebird.playeranimatorapi.data.PlayerAnimationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerAnimAPI.class)
public class PlayerAnimAPIMixin_playeranimatorapiOnly {
    @Inject(method = "playPlayerAnim(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;Lcom/zigythebird/playeranimatorapi/data/PlayerAnimationData;)V", at = @At("TAIL"))
    private static void play(ServerLevel level, Player player, PlayerAnimationData data, CallbackInfo ci) {
        PlayerAnimatorAPICompat.onAnimationPlayed(player, data);
    }

    @Inject(method = "stopPlayerAnim", at = @At("TAIL"))
    private static void stop(ServerLevel level, Player player, ResourceLocation animationID, CallbackInfo ci) {
        PlayerAnimatorAPICompat.onAnimationStopped(player, animationID);
    }
}
