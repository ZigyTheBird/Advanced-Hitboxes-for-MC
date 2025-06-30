package com.zigythebird.advanced_hitboxes.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.zigythebird.advanced_hitboxes.utils.CommonSideUtils;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmoothSwimmingLookControl.class)
public class SmoothSwimmingLookControlMixin {
    @Shadow @Final private int maxYRotFromCenter;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci, @Local float f) {
        if (f < (float)(-this.maxYRotFromCenter)) {
            CommonSideUtils.setBodyYRot(((SmoothSwimmingLookControl)(Object)this).mob, CommonSideUtils.getBodyYRot(((SmoothSwimmingLookControl)(Object)this).mob) - 4);
        } else if (f > (float)this.maxYRotFromCenter) {
            CommonSideUtils.setBodyYRot(((SmoothSwimmingLookControl)(Object)this).mob, CommonSideUtils.getBodyYRot(((SmoothSwimmingLookControl)(Object)this).mob) + 4);
        }
    }
}
