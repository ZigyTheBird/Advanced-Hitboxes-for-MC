package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.utils.CommonSideUtils;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmoothSwimmingMoveControl.class)
public class SmoothSwimmingMoveControlMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setYRot(F)V"))
    private void tick(CallbackInfo ci) {
        CommonSideUtils.setBodyYRot(((SmoothSwimmingMoveControl)(Object)this).mob, ((SmoothSwimmingMoveControl)(Object)this).mob.getYRot());
    }
}
