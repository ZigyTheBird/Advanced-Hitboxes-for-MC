package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.interfaces.LivingEntityInterface;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityInterface {
    @Unique
    private float advanced_Hitboxes$commonYBodyRot;

    @Override
    public float advanced_Hitboxes$commonYBodyRot() {
        return advanced_Hitboxes$commonYBodyRot;
    }

    @Override
    public void advanced_Hitboxes$setCommonYBodyRot(float rot) {
        advanced_Hitboxes$commonYBodyRot = rot;
    }

    @Inject(method = "recreateFromPacket", at = @At("TAIL"))
    private void recreateFrompacket(ClientboundAddEntityPacket packet, CallbackInfo ci) {
        advanced_Hitboxes$commonYBodyRot = packet.getYHeadRot();
    }
}
