package com.zigythebird.advanced_hitboxes.utils;

import com.zigythebird.advanced_hitboxes.misc.CommonArmPose;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;

//TODO Replace all add rot methods with per axis add rot methods once I add them to PAL
public class PlayerVanillaAnimationApplier {
    public static void setupAnim(PlayerAnimBone head, PlayerAnimBone body, PlayerAnimBone rightArm, PlayerAnimBone leftArm, PlayerAnimBone rightLeg, PlayerAnimBone leftLeg,
                                 float swimAmount, boolean riding, boolean crouching, CommonArmPose rightArmPose, CommonArmPose leftArmPose, float attackTime,
                                 Player entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = entity.getFallFlyingTicks() > 4;
        boolean flag1 = entity.isVisuallySwimming();
        head.setRotY(netHeadYaw * ((float)Math.PI / 180F));
        if (flag) {
            head.setRotX((-(float)Math.PI / 4F));
        } else if (swimAmount > 0.0F) {
            if (flag1) {
                head.setRotX(rotlerpRad(swimAmount, head.getRotX(), (-(float)Math.PI / 4F)));
            } else {
                head.setRotX(rotlerpRad(swimAmount, head.getRotX(), headPitch * ((float)Math.PI / 180F)));
            }
        } else {
            head.setRotX(headPitch * ((float)Math.PI / 180F));
        }

        body.setRotY(0.0F);
        rightArm.setPosZ(0.0F);
        rightArm.setPosX(-5.0F - (-5.0F)); // Adjusted by default value
        leftArm.setPosZ(0.0F);
        leftArm.setPosX(5.0F - 5.0F); // Adjusted by default value
        float f = 1.0F;
        if (flag) {
            f = (float)entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        rightArm.setRotX(Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f);
        leftArm.setRotX(Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f);
        rightArm.setRotZ(0.0F);
        leftArm.setRotZ(0.0F);
        rightLeg.setRotX(Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f);
        leftLeg.setRotX(Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / f);
        rightLeg.setRotY(0.005F);
        leftLeg.setRotY(-0.005F);
        rightLeg.setRotZ(0.005F);
        leftLeg.setRotZ(-0.005F);
        if (riding) {
            rightArm.setRotX(rightArm.getRotX() + (-(float)Math.PI / 5F));
            leftArm.setRotX(leftArm.getRotX() + (-(float)Math.PI / 5F));
            rightLeg.setRotX(-1.4137167F);
            rightLeg.setRotY(((float)Math.PI / 10F));
            rightLeg.setRotZ(0.07853982F);
            leftLeg.setRotX(-1.4137167F);
            leftLeg.setRotY((-(float)Math.PI / 10F));
            leftLeg.setRotZ(-0.07853982F);
        }

        rightArm.setRotY(0.0F);
        leftArm.setRotY(0.0F);
        boolean flag2 = entity.getMainArm() == HumanoidArm.RIGHT;
        if (entity.isUsingItem()) {
            boolean flag3 = entity.getUsedItemHand() == InteractionHand.MAIN_HAND;
            if (flag3 == flag2) {
                poseRightArm(rightArm, leftArm, head, rightArmPose, entity, crouching);
            } else {
                poseLeftArm(rightArm, leftArm, head, leftArmPose, entity, crouching);
            }
        } else {
            boolean flag4 = flag2 ? leftArmPose.isTwoHanded() : rightArmPose.isTwoHanded();
            if (flag2 != flag4) {
                poseLeftArm(rightArm, leftArm, head, leftArmPose, entity, crouching);
                poseRightArm(rightArm, leftArm, head, rightArmPose, entity, crouching);
            } else {
                poseRightArm(rightArm, leftArm, head, rightArmPose, entity, crouching);
                poseLeftArm(rightArm, leftArm, head, leftArmPose, entity, crouching);
            }
        }

        setupAttackAnimation(rightArm, leftArm, body, head, attackTime, entity, ageInTicks);
        if (crouching) {
            body.setRotX(0.5F);
            rightArm.setRotX(rightArm.getRotX() + 0.4F);
            leftArm.setRotX(leftArm.getRotX() + 0.4F);
            rightLeg.setPosZ(4.0F - 0.1f); // Adjusted by default value
            leftLeg.setPosZ(4.0F - 0.1f); // Adjusted by default value
            rightLeg.setPosY(-(12.2F - 12.0F)); // Negated Y and adjusted by default value
            leftLeg.setPosY(-(12.2F - 12.0F)); // Negated Y and adjusted by default value
            head.setPosY(-(4.2F - 0.0F)); // Negated Y and adjusted by default value
            body.setPosY(-(3.2F - 0.0F)); // Negated Y and adjusted by default value
            leftArm.setPosY(-(5.2F - 2.0F)); // Negated Y and adjusted by default value
            rightArm.setPosY(-(5.2F - 2.0F)); // Negated Y and adjusted by default value
        } else {
            body.setRotX(0.0F);
            rightLeg.setPosZ(0.0F - 0.1f); // Adjusted by default value
            leftLeg.setPosZ(0.0F - 0.1f); // Adjusted by default value
            rightLeg.setPosY(-(12.0F - 12.0F)); // Negated Y and adjusted by default value
            leftLeg.setPosY(-(12.0F - 12.0F)); // Negated Y and adjusted by default value
            head.setPosY(-(0.0F - 0.0F)); // Negated Y and adjusted by default value
            body.setPosY(-(0.0F - 0.0F)); // Negated Y and adjusted by default value
            leftArm.setPosY(-(2.0F - 2.0F)); // Negated Y and adjusted by default value
            rightArm.setPosY(-(2.0F - 2.0F)); // Negated Y and adjusted by default value
        }

        if (rightArmPose != CommonArmPose.SPYGLASS) {
            bobModelPart(rightArm, ageInTicks, 1.0F);
        }

        if (leftArmPose != CommonArmPose.SPYGLASS) {
            bobModelPart(leftArm, ageInTicks, -1.0F);
        }

        if (swimAmount > 0.0F) {
            float f5 = limbSwing % 26.0F;
            HumanoidArm humanoidarm = getAttackArm(entity);
            float f1 = humanoidarm == HumanoidArm.RIGHT && attackTime > 0.0F ? 0.0F : swimAmount;
            float f2 = humanoidarm == HumanoidArm.LEFT && attackTime > 0.0F ? 0.0F : swimAmount;
            if (!entity.isUsingItem()) {
                if (f5 < 14.0F) {
                    leftArm.setRotX(rotlerpRad(f2, leftArm.getRotX(), 0.0F));
                    rightArm.setRotX(Mth.lerp(f1, rightArm.getRotX(), 0.0F));
                    leftArm.setRotY(rotlerpRad(f2, leftArm.getRotY(), (float)Math.PI));
                    rightArm.setRotY(Mth.lerp(f1, rightArm.getRotY(), (float)Math.PI));
                    leftArm.setRotZ(rotlerpRad(f2, leftArm.getRotZ(), (float)Math.PI + 1.8707964F * quadraticArmUpdate(f5) / quadraticArmUpdate(14.0F)));
                    rightArm.setRotZ(Mth.lerp(f1, rightArm.getRotZ(), (float)Math.PI - 1.8707964F * quadraticArmUpdate(f5) / quadraticArmUpdate(14.0F)));
                } else if (f5 >= 14.0F && f5 < 22.0F) {
                    float f6 = (f5 - 14.0F) / 8.0F;
                    leftArm.setRotX(rotlerpRad(f2, leftArm.getRotX(), ((float)Math.PI / 2F) * f6));
                    rightArm.setRotX(Mth.lerp(f1, rightArm.getRotX(), ((float)Math.PI / 2F) * f6));
                    leftArm.setRotY(rotlerpRad(f2, leftArm.getRotY(), (float)Math.PI));
                    rightArm.setRotY(Mth.lerp(f1, rightArm.getRotY(), (float)Math.PI));
                    leftArm.setRotZ(rotlerpRad(f2, leftArm.getRotZ(), 5.012389F - 1.8707964F * f6));
                    rightArm.setRotZ(Mth.lerp(f1, rightArm.getRotZ(), 1.2707963F + 1.8707964F * f6));
                } else if (f5 >= 22.0F && f5 < 26.0F) {
                    float f3 = (f5 - 22.0F) / 4.0F;
                    leftArm.setRotX(rotlerpRad(f2, leftArm.getRotX(), ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f3));
                    rightArm.setRotX(Mth.lerp(f1, rightArm.getRotX(), ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f3));
                    leftArm.setRotY(rotlerpRad(f2, leftArm.getRotY(), (float)Math.PI));
                    rightArm.setRotY(Mth.lerp(f1, rightArm.getRotY(), (float)Math.PI));
                    leftArm.setRotZ(rotlerpRad(f2, leftArm.getRotZ(), (float)Math.PI));
                    rightArm.setRotZ(Mth.lerp(f1, rightArm.getRotZ(), (float)Math.PI));
                }
            }

            leftLeg.setRotX(Mth.lerp(swimAmount, leftLeg.getRotX(), 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI)));
            rightLeg.setRotX(Mth.lerp(swimAmount, rightLeg.getRotX(), 0.3F * Mth.cos(limbSwing * 0.33333334F)));
        }
    }

    private static void poseRightArm(PlayerAnimBone rightArm, PlayerAnimBone leftArm, PlayerAnimBone head, CommonArmPose rightArmPose, Player livingEntity, boolean crouching) {
        switch (rightArmPose.ordinal()) {
            case 0:
                rightArm.setRotY(0.0F);
                break;
            case 1:
                rightArm.setRotX(rightArm.getRotX() * 0.5F - ((float)Math.PI / 10F));
                rightArm.setRotY(0.0F);
                break;
            case 2:
                poseBlockingArm(rightArm, head, true);
                break;
            case 3:
                rightArm.setRotY(-0.1F + head.getRotY());
                leftArm.setRotY(0.1F + head.getRotY() + 0.4F);
                rightArm.setRotX((-(float)Math.PI / 2F) + head.getRotX());
                leftArm.setRotX((-(float)Math.PI / 2F) + head.getRotX());
                break;
            case 4:
                rightArm.setRotX(rightArm.getRotX() * 0.5F - (float)Math.PI);
                rightArm.setRotY(0.0F);
                break;
            case 5:
                animateCrossbowCharge(rightArm, leftArm, livingEntity, true);
                break;
            case 6:
                animateCrossbowHold(rightArm, leftArm, head, true);
                break;
            case 7:
                rightArm.setRotX(Mth.clamp(head.getRotX() - 1.9198622F - (crouching ? 0.2617994F : 0.0F), -2.4F, 3.3F));
                rightArm.setRotY(head.getRotY() - 0.2617994F);
                break;
            case 8:
                rightArm.setRotX(Mth.clamp(head.getRotX(), -1.2F, 1.2F) - 1.4835298F);
                rightArm.setRotY(head.getRotY() - ((float)Math.PI / 6F));
                break;
            case 9:
                rightArm.setRotX(rightArm.getRotX() * 0.5F - ((float)Math.PI / 5F));
                rightArm.setRotY(0.0F);
            default:
                // rightArmPose.applyTransform(this, livingEntity, HumanoidArm.RIGHT); - This method call was removed as we don't have access to the original model
        }
    }

    private static void poseLeftArm(PlayerAnimBone rightArm, PlayerAnimBone leftArm, PlayerAnimBone head, CommonArmPose leftArmPose, Player livingEntity, boolean crouching) {
        switch (leftArmPose.ordinal()) {
            case 0:
                leftArm.setRotY(0.0F);
                break;
            case 1:
                leftArm.setRotX(leftArm.getRotX() * 0.5F - ((float)Math.PI / 10F));
                leftArm.setRotY(0.0F);
                break;
            case 2:
                poseBlockingArm(leftArm, head, false);
                break;
            case 3:
                rightArm.setRotY(-0.1F + head.getRotY() - 0.4F);
                leftArm.setRotY(0.1F + head.getRotY());
                rightArm.setRotX((-(float)Math.PI / 2F) + head.getRotX());
                leftArm.setRotX((-(float)Math.PI / 2F) + head.getRotX());
                break;
            case 4:
                leftArm.setRotX(leftArm.getRotX() * 0.5F - (float)Math.PI);
                leftArm.setRotY(0.0F);
                break;
            case 5:
                animateCrossbowCharge(rightArm, leftArm, livingEntity, false);
                break;
            case 6:
                animateCrossbowHold(rightArm, leftArm, head, false);
                break;
            case 7:
                leftArm.setRotX(Mth.clamp(head.getRotX() - 1.9198622F - (crouching ? 0.2617994F : 0.0F), -2.4F, 3.3F));
                leftArm.setRotY(head.getRotY() + 0.2617994F);
                break;
            case 8:
                leftArm.setRotX(Mth.clamp(head.getRotX(), -1.2F, 1.2F) - 1.4835298F);
                leftArm.setRotY(head.getRotY() + ((float)Math.PI / 6F));
                break;
            case 9:
                leftArm.setRotX(leftArm.getRotX() * 0.5F - ((float)Math.PI / 5F));
                leftArm.setRotY(0.0F);
        }
    }

    protected static <T> void setupAttackAnimation(PlayerAnimBone rightArm, PlayerAnimBone leftArm, PlayerAnimBone body, PlayerAnimBone head, float attackTime, Player livingEntity, float ageInTicks) {
        if (!(attackTime <= 0.0F)) {
            HumanoidArm humanoidarm = getAttackArm(livingEntity);
            PlayerAnimBone mainAnim = humanoidarm == HumanoidArm.RIGHT ? rightArm : leftArm;
            float f = attackTime;
            body.setRotY(Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F);
            if (humanoidarm == HumanoidArm.LEFT) {
                body.setRotY(body.getRotY() * -1.0F);
            }

            rightArm.setPosZ(Mth.sin(body.getRotY()) * 5.0F - 0.0F); // Adjusted by default value
            rightArm.setPosX(-Mth.cos(body.getRotY()) * 5.0F - (-5.0F)); // Adjusted by default value
            leftArm.setPosZ(-Mth.sin(body.getRotY()) * 5.0F - 0.0F); // Adjusted by default value
            leftArm.setPosX(Mth.cos(body.getRotY()) * 5.0F - 5.0F); // Adjusted by default value
            rightArm.setRotY(rightArm.getRotY() + body.getRotY());
            leftArm.setRotY(leftArm.getRotY() + body.getRotY());
            leftArm.setRotX(leftArm.getRotX() + body.getRotY());
            f = 1.0F - attackTime;
            f *= f;
            f *= f;
            f = 1.0F - f;
            float f1 = Mth.sin(f * (float)Math.PI);
            float f2 = Mth.sin(attackTime * (float)Math.PI) * -(head.getRotX() - 0.7F) * 0.75F;
            mainAnim.setRotX(mainAnim.getRotX() - f1 * 1.2F + f2);
            mainAnim.setRotY(mainAnim.getRotY() + body.getRotY() * 2.0F);
            mainAnim.setRotZ(mainAnim.getRotZ() + Mth.sin(attackTime * (float)Math.PI) * -0.4F);
        }
    }

    private static void poseBlockingArm(PlayerAnimBone arm, PlayerAnimBone head, boolean isRightArm) {
        arm.setRotX(arm.getRotX() * 0.5F - 0.9424779F + Mth.clamp(head.getRotX(), -1.3962634F, 0.43633232F));
        arm.setRotY((isRightArm ? -30.0F : 30.0F) * ((float)Math.PI / 180F) + Mth.clamp(head.getRotY(), (-(float)Math.PI / 6F), ((float)Math.PI / 6F)));
    }


    public static void bobModelPart(PlayerAnimBone modelPart, float ageInTicks, float multiplier) {
        modelPart.setRotZ(modelPart.getRotZ() + multiplier * (Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F));
        modelPart.setRotX(modelPart.getRotX() + multiplier * Mth.sin(ageInTicks * 0.067F) * 0.05F);
    }

    public static void animateCrossbowHold(PlayerAnimBone rightArm, PlayerAnimBone leftArm, PlayerAnimBone head, boolean rightHanded) {
        PlayerAnimBone modelpart = rightHanded ? rightArm : leftArm;
        PlayerAnimBone modelpart1 = rightHanded ? leftArm : rightArm;
        modelpart.setRotY((rightHanded ? -0.3F : 0.3F) + head.getRotY());
        modelpart1.setRotY((rightHanded ? 0.6F : -0.6F) + head.getRotY());
        modelpart.setRotX((-(float)Math.PI / 2F) + head.getRotX() + 0.1F);
        modelpart1.setRotX(-1.5F + head.getRotX());
    }

    public static void animateCrossbowCharge(PlayerAnimBone rightArm, PlayerAnimBone leftArm, Player entity, boolean rightHanded) {
        PlayerAnimBone modelpart = rightHanded ? rightArm : leftArm;
        PlayerAnimBone modelpart1 = rightHanded ? leftArm : rightArm;
        modelpart.setRotY(rightHanded ? -0.8F : 0.8F);
        modelpart.setRotX(-0.97079635F);
        modelpart1.setRotX(modelpart.getRotX());
        float f = (float) CrossbowItem.getChargeDuration(entity.getUseItem(), entity);
        float f1 = Mth.clamp((float)entity.getTicksUsingItem(), 0.0F, f);
        float f2 = f1 / f;
        modelpart1.setRotY(Mth.lerp(f2, 0.4F, 0.85F) * (float)(rightHanded ? 1 : -1));
        modelpart1.setRotX(Mth.lerp(f2, modelpart1.getRotX(), (-(float)Math.PI / 2F)));
    }

    private static HumanoidArm getAttackArm(Player entity) {
        return entity.getMainArm();
    }

    private static float rotlerpRad(float angle, float maxAngle, float mul) {
        float f = (mul - maxAngle) % ((float)Math.PI * 2F);
        if (f < -(float)Math.PI) {
            f += ((float)Math.PI * 2F);
        }

        if (f >= (float)Math.PI) {
            f -= ((float)Math.PI * 2F);
        }

        return maxAngle + angle * f;
    }

    private static float quadraticArmUpdate(float limbSwing) {
        return -65.0F * limbSwing + limbSwing * limbSwing;
    }
}