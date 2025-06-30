package com.zigythebird.advanced_hitboxes.mixin.common;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1001)
public class ServerGamePacketListenerMixin {
    @Redirect(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;lengthSqr()D", ordinal = 0))
    public double preLengthSquared(Vec3 instance) {
        return Float.MAX_VALUE;
    }

    @Redirect(method = "handleMoveVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;lengthSqr()D", ordinal = 0))
    public double preVehicleLengthSquared(Vec3 instance) {
        return Float.MAX_VALUE;
    }

    @ModifyConstant(method = "handleMovePlayer", constant = @Constant(doubleValue = 0.0625))
    private double modifyPlayerMoveDoubleConst(double original) {
        return Float.MAX_VALUE;
    }

    @ModifyConstant(method = "handleMoveVehicle", constant = @Constant(doubleValue = 0.0625, ordinal = 1))
    private double modifyVehicleMoveDoubleConst(double original) {
        return Float.MAX_VALUE;
    }
}
