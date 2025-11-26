package com.zigythebird.advanced_hitboxes.geckolib.model;

import com.mojang.math.Axis;
import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.misc.CustomPoseStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class PlayerHitboxModel<T extends AdvancedHitboxEntity> extends DefaultedEntityHitboxModel<T> {
    public PlayerHitboxModel() {
        super(AdvancedHitboxesMod.id("player_hitbox"));
    }

    @Override
    public void setupRotations(LivingEntity entity, CustomPoseStack poseStack, float ageInTicks, float yBodyRot, float partialTick, float scale) {
        float f = entity.getSwimAmount(partialTick);
        float f1 = entity.getViewXRot(partialTick);
        if (entity.isFallFlying()) {
            super.setupRotations(entity, poseStack, ageInTicks, yBodyRot, partialTick, scale);
            float f2 = (float)entity.getFallFlyingTicks() + partialTick;
            float f3 = Mth.clamp(f2 * f2 / 100.0F, 0.0F, 1.0F);
            if (!entity.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(f3 * (-90.0F - f1)));
            }

            Vec3 vec3 = entity.getViewVector(partialTick);
            //This was the getDeltaMovementLerped method of the AbstractClientEntity class, but for the sake of simplicity I just got the current delta movement
            Vec3 vec31 = entity.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > (double)0.0F && d1 > (double)0.0F) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                poseStack.mulPose(Axis.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            super.setupRotations(entity, poseStack, ageInTicks, yBodyRot, partialTick, scale);
            float f4 = !entity.isInWater() && !entity.isInFluidType((fluidType, height) -> entity.canSwimInFluidType(fluidType)) ? -90.0F : -90.0F - entity.getXRot();
            float f5 = Mth.lerp(f, 0.0F, f4);
            poseStack.mulPose(Axis.XP.rotationDegrees(f5));
            if (entity.isVisuallySwimming()) {
                poseStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.setupRotations(entity, poseStack, ageInTicks, yBodyRot, partialTick, scale);
        }

    }
}
