//package com.zigythebird.advanced_hitboxes.utils;
//
//import com.zigythebird.advanced_hitboxes.phys.OBB;
//import net.minecraft.ChatFormatting;
//import net.minecraft.util.Mth;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.entity.player.PlayerModelPart;
//import net.minecraft.world.phys.Vec3;
//import org.joml.Vector3f;
//
//import java.util.Map;
//
//public class PlayerHitboxUtils {
//
//    private static final Vec3 ONE = new Vec3(1, 1, 1);
//
//    private static final Vec3 headOrigin = new Vec3(0, 1.75, 0);
//    private static final Vec3 headDimensions = new Vec3(0.5, 0.5, 0.5);
//    private static final Vec3 headPivot = new Vec3(0, 1.5, 0);
//
//    private static final Vec3 torsoOrigin = new Vec3(0, 1.125, 0);
//    private static final Vec3 torsoDimensions = new Vec3(0.5, 0.75, 0.25);
//    private static final Vec3 torsoPivot = new Vec3(0, 1.5, 0);
//
//    public static void updateHitboxes(Player player, Map<PlayerPartsEnum, OBB> hitboxes) {
//        if (ArmsAndArtilleryMod.isClientTrusted()) {
//            return;
//        }
//        //head
//        boolean bl = player.getFallFlyingTicks() > 4;
//        boolean bl2 = player.isVisuallySwimming();
//        float headPitch = getHeadPitch(player);
//        float netHeadYaw = getNetHeadYaw(player);
//        float headxRot;
//        if (bl) {
//            headxRot = -45.00000343774687F;
//        }
////        else if (player.getSwimAmount(1) > 0.0F) {
////            if (bl2) {
////                headxRot = 45.00000343774687F;
////            } else {
////                headxRot = headPitch;
////            }
////        }
//        else {
//            headxRot = headPitch;
//        }
//        hitboxes.get(PlayerPartsEnum.HEAD).update(headOrigin, headPivot, 0.5, 0.5, 0.5, headxRot, netHeadYaw, 0);
//
//        //torso
//        double torsoPosY = 1.125;
//        hitboxes.get(PlayerPartsEnum.TORSO).update(new Vec3(0, torsoPosY - (player.isCrouching() ? 0.2 : 0), 0), new Vec3(0, torsoPosY + 0.375, 0), 0.5, 0.75, 0.25, player.isCrouching() ? 28.64789060997776F : 0.0F, 0, 0);
//    }
//
//    public static OBB makeOBBForPlayer(Player player, OBB obb) {
//        OBB obb2 = new OBB(obb);
//        float bodyRot = ((PlayerHitboxInterface)player).armsandartillery$commonGetYBodyRot();
//        obb2.rotation = new Vector3f(obb2.rotation.x, Mth.wrapDegrees(obb2.rotation.y + bodyRot), obb2.rotation.z);
//        obb2.calculateOrientation();
//        Vec3 adjustedPosition = obb.center.multiply(obb.axisX).add(obb.center.multiply(obb.axisY)).add(obb.center.multiply(obb.axisZ));
//        obb2.center = player.position().add(adjustedPosition);
//        return obb2;
//    }
//
//    public static Vec3 getOriginForPart(String name) {
//        switch (name) {
//            case "head" -> {
//                return headOrigin;
//            }
//            case "torso" -> {
//                return torsoOrigin;
//            }
////            case "right_arm" -> {
////                return rightArmOrigin;
////            }
////            case "left_arm" -> {
////                return leftArmOrigin;
////            }
////            case "right_leg" -> {
////                return rightLegOrigin;
////            }
////            case "left_leg" -> {
////                return leftLegOrigin;
////            }
//            default -> {
//                return Vec3.ZERO;
//            }
//        }
//    }
//
//    public static Vec3 getDimensionsForPart(String name) {
//        switch (name) {
//            case "head" -> {
//                return headDimensions;
//            }
//            case "torso" -> {
//                return torsoDimensions;
//            }
////            case "right_arm" -> {
////                return rightArmDimensions;
////            }
////            case "left_arm" -> {
////                return leftArmDimensions;
////            }
////            case "right_leg" -> {
////                return rightLegDimensions;
////            }
////            case "left_leg" -> {
////                return leftLegDimensions;
////            }
//            default -> {
//                return Vec3.ZERO;
//            }
//        }
//    }
//
//    public static Vec3 getPivotForPart(String name) {
//        switch (name) {
//            case "head" -> {
//                return headPivot;
//            }
//            case "torso" -> {
//                return torsoPivot;
//            }
////            case "right_arm" -> {
////                return rightArmPivot;
////            }
////            case "left_arm" -> {
////                return leftArmPivot;
////            }
////            case "right_leg" -> {
////                return rightLegPivot;
////            }
////            case "left_leg" -> {
////                return leftLegPivot;
////            }
//            default -> {
//                return Vec3.ZERO;
//            }
//        }
//    }
//
//    public static float getHeadPitch(Player player) {
//        float headPitch = player.getXRot();
//        if (isEntityUpsideDown(player)) {
//            headPitch *= -1.0F;
//        }
//        return headPitch;
//    }
//
//    public static float getNetHeadYaw(Player player) {
//        float f = ((PlayerHitboxInterface)player).armsandartillery$commonGetYBodyRot();
//        float g = player.getYRot();
//        float netHeadYaw = g - f;
//        float i;
////        if (player.isPassenger() && player.getVehicle() instanceof LivingEntity) {
////            LivingEntity livingEntity = (LivingEntity)player.getVehicle();
////            f = livingEntity.yBodyRot;
////            netHeadYaw = g - f;
////            i = Mth.wrapDegrees(netHeadYaw);
////            if (i < -85.0F) {
////                i = -85.0F;
////            }
////
////            if (i >= 85.0F) {
////                i = 85.0F;
////            }
////
////            f = g - i;
////            if (i * i > 2500.0F) {
////                f += i * 0.2F;
////            }
////
////            netHeadYaw = g - f;
////        }
//        if (isEntityUpsideDown(player)) {
//            netHeadYaw *= -1.0F;
//        }
//
//        return netHeadYaw;
//    }
//
//    public static boolean isEntityUpsideDown(LivingEntity entity) {
//        if (entity instanceof Player || entity.hasCustomName()) {
//            String string = ChatFormatting.stripFormatting(entity.getName().getString());
//            if ("Dinnerbone".equals(string) || "Grumm".equals(string)) {
//                return !(entity instanceof Player) || ((Player)entity).isModelPartShown(PlayerModelPart.CAPE);
//            }
//        }
//
//        return false;
//    }
//}
