package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.accessor.LivingEntityAccessor;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityAccessor {
    @Shadow protected abstract float getMaxHeadRotationRelativeToBody();

    @Shadow public float yHeadRot;
    @Unique
    private float advanced_Hitboxes$commonYBodyRot;
    @Unique
    private float advanced_Hitboxes$commonYBodyRot0;

    @Override
    public float advanced_Hitboxes$commonYBodyRot() {
        return advanced_Hitboxes$commonYBodyRot;
    }

    @Override
    public void advanced_Hitboxes$setCommonYBodyRot(float rot) {
        advanced_Hitboxes$commonYBodyRot = rot;
    }

    @Override
    public float advanced_Hitboxes$commonYBodyRot0() {
        return advanced_Hitboxes$commonYBodyRot0;
    }

    @Override
    public void advanced_Hitboxes$setCommonYBodyRot0(float rot) {
        advanced_Hitboxes$commonYBodyRot0 = rot;
    }

    @Inject(method = "tickHeadTurn", at = @At("RETURN"))
    private void tickHeadTurn(float yRot, float animStep, CallbackInfoReturnable<Float> cir) {
        float f = Mth.wrapDegrees(yRot - advanced_Hitboxes$commonYBodyRot);
        this.advanced_Hitboxes$commonYBodyRot += f * 0.3F;
        float f1 = Mth.wrapDegrees(((LivingEntity)(Object)this).getYRot() - this.advanced_Hitboxes$commonYBodyRot);
        float f2 = this.getMaxHeadRotationRelativeToBody();
        if (Math.abs(f1) > f2) {
            this.advanced_Hitboxes$commonYBodyRot = this.advanced_Hitboxes$commonYBodyRot + (f1 - (float)Mth.sign(f1) * f2);
        }
    }

    @Inject(method = "lookAt", at = @At("TAIL"))
    private void lookAt(EntityAnchorArgument.Anchor anchor, Vec3 target, CallbackInfo ci) {
        advanced_Hitboxes$commonYBodyRot = this.yHeadRot;
        advanced_Hitboxes$commonYBodyRot0 = advanced_Hitboxes$commonYBodyRot;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/LivingEntity;tickHeadTurn(FF)F"))
    private void tick(CallbackInfo ci) {
        while (this.advanced_Hitboxes$commonYBodyRot - this.advanced_Hitboxes$commonYBodyRot0 < -180.0F) {
            this.advanced_Hitboxes$commonYBodyRot0 -= 360.0F;
        }

        while (this.advanced_Hitboxes$commonYBodyRot - this.advanced_Hitboxes$commonYBodyRot0 >= 180.0F) {
            this.advanced_Hitboxes$commonYBodyRot0 += 360.0F;
        }
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void baseTick(CallbackInfo ci) {
        advanced_Hitboxes$commonYBodyRot0 = advanced_Hitboxes$commonYBodyRot;
    }
}
