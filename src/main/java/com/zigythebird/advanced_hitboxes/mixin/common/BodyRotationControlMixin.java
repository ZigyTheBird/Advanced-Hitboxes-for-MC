package com.zigythebird.advanced_hitboxes.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.zigythebird.advanced_hitboxes.utils.CommonSideUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BodyRotationControl.class)
public class BodyRotationControlMixin {
    @Shadow @Final private Mob mob;

    @Inject(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getYRot()F"))
    private void clientTick(CallbackInfo ci) {
        CommonSideUtils.setBodyYRot(this.mob, this.mob.getYRot());
    }

    @Inject(method = "rotateBodyIfNecessary", at = @At("TAIL"))
    private void rotateBodyIfNecessary(CallbackInfo ci) {
        CommonSideUtils.setBodyYRot(this.mob, Mth.rotateIfNecessary(CommonSideUtils.getBodyYRot(this.mob), this.mob.yHeadRot, (float)this.mob.getMaxHeadYRot()));
    }

    @Inject(method = "rotateHeadTowardsFront", at = @At("TAIL"))
    private void rotateHeadTowardsFront(CallbackInfo ci, @Local(ordinal = 1) float f1) {
        CommonSideUtils.setBodyYRot(this.mob, Mth.rotateIfNecessary(CommonSideUtils.getBodyYRot(this.mob), this.mob.yHeadRot, f1));
    }
}
