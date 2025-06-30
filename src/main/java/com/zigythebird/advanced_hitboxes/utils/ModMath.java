package com.zigythebird.advanced_hitboxes.utils;

import com.mojang.math.Axis;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.GeoCube;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxGeoBone;
import com.zigythebird.advanced_hitboxes.misc.CustomPoseStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ModMath {
    public static Vec3 vector3dToVec3(Vector3d vector3d) {
        return new Vec3(vector3d.x, vector3d.y, vector3d.z);
    }

    public static Vec3 vector3fToVec3(Vector3f vector3f) {
        return new Vec3(vector3f.x, vector3f.y, vector3f.z);
    }

    public static void translateMatrixToBone(CustomPoseStack poseStack, HitboxGeoBone bone) {
        poseStack.translate(bone.getPosX() / 16f, bone.getPosY() / 16f, bone.getPosZ() / 16f);
    }

    public static void rotateMatrixAroundBone(CustomPoseStack poseStack, HitboxGeoBone bone) {
        if (bone.getRotZ() != 0)
            poseStack.mulPose(Axis.ZP.rotation(bone.getRotZ()));

        if (bone.getRotY() != 0)
            poseStack.mulPose(Axis.YP.rotation(bone.getRotY()));

        if (bone.getRotX() != 0)
            poseStack.mulPose(Axis.XP.rotation(bone.getRotX()));
    }

    public static void rotateMatrixAroundCube(CustomPoseStack poseStack, GeoCube cube) {
        Vec3 rotation = cube.rotation();

        poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, (float)rotation.z()));
        poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float)rotation.y(), 0));
        poseStack.mulPose(new Quaternionf().rotationXYZ((float)rotation.x(), 0, 0));
    }

    public static void scaleMatrixForBone(CustomPoseStack poseStack, HitboxGeoBone bone) {
        poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static void translateToPivotPoint(CustomPoseStack poseStack, GeoCube cube) {
        Vec3 pivot = cube.pivot();
        poseStack.translate(pivot.x() / 16f, pivot.y() / 16f, pivot.z() / 16f);
    }

    public static void translateToPivotPoint(CustomPoseStack poseStack, HitboxGeoBone bone) {
        poseStack.translate(bone.getPivotX() / 16f, bone.getPivotY() / 16f, bone.getPivotZ() / 16f);
    }

    public static void translateAwayFromPivotPoint(CustomPoseStack poseStack, GeoCube cube) {
        Vec3 pivot = cube.pivot();

        poseStack.translate(-pivot.x() / 16f, -pivot.y() / 16f, -pivot.z() / 16f);
    }

    public static void translateAwayFromPivotPoint(CustomPoseStack poseStack, HitboxGeoBone bone) {
        poseStack.translate(-bone.getPivotX() / 16f, -bone.getPivotY() / 16f, -bone.getPivotZ() / 16f);
    }

    public static void prepMatrixForBone(CustomPoseStack poseStack, HitboxGeoBone bone) {
        translateMatrixToBone(poseStack, bone);
        translateToPivotPoint(poseStack, bone);
        rotateMatrixAroundBone(poseStack, bone);
        scaleMatrixForBone(poseStack, bone);
        translateAwayFromPivotPoint(poseStack, bone);
    }
}
