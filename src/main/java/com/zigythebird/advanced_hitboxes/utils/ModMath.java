package com.zigythebird.advanced_hitboxes.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class ModMath {
    public static void rotateAroundPivot(Vector3d rotation, Vector3d position, float pivotX, float pivotY, float pivotZ) {
        //"l" is short for length here
        double l1 = Math.sqrt(Math.pow(position.z - pivotZ, 2) + Math.pow(position.y - pivotY, 2));
        double inverse1 = Math.atan2(Math.abs((position.y - pivotY) / l1), Math.abs((position.z - pivotZ) / l1));
        inverse1 = Double.isNaN(inverse1) ? 0 : inverse1;
        double transformL1 = Math.sqrt((2 * Math.pow(l1, 2)) - (2 * Math.pow(l1, 2)) * Math.cos(rotation.x));
        position.add(0,
                (Math.cos(rotation.x / 2 - inverse1) * transformL1),
                (Math.sin(rotation.x / 2 - inverse1) * transformL1));
        double l2 = Math.sqrt(Math.pow(position.z - pivotZ, 2) + Math.pow(position.x - pivotX, 2));
        double inverse2 = Math.atan2(Math.abs((position.x - pivotX) / l2), Math.abs((position.z - pivotZ) / l2));
        inverse2 = Double.isNaN(inverse2) ? 0 : inverse2;
        double transformL2 = Math.sqrt((2 * Math.pow(l2, 2)) - (2 * Math.pow(l2, 2)) * Math.cos(rotation.y));
        position.add(0,
                (Math.cos(rotation.y / 2 - inverse2) * transformL2),
                (Math.sin(rotation.y / 2 - inverse2) * transformL2));
        double l3 = Math.sqrt(Math.pow(position.x - pivotX, 2) + Math.pow(position.y - pivotY, 2));
        double inverse3 = Math.atan2(Math.abs((position.y - pivotY) / l3), Math.abs((position.x - pivotX) / l3));
        inverse3 = Double.isNaN(inverse3) ? 0 : inverse3;
        double transformL3 = Math.sqrt((2 * Math.pow(l3, 2)) - (2 * Math.pow(l3, 2)) * Math.cos(rotation.z));
        position.add(0,
                (Math.cos(rotation.z / 2 - inverse3) * transformL3),
                (Math.sin(rotation.z / 2 - inverse3) * transformL3));
    }
    
    public static Vec3 vector3dToVec3(Vector3d vector3d) {
        return new Vec3(vector3d.x, vector3d.y, vector3d.z);
    }

    public static Vec3 moveInLocalSpace(Vec3 input, float xRot, float yRot) {
        Vec3 result = Vec3.directionFromRotation(xRot, yRot).scale(input.z);
        result = result.add(Vec3.directionFromRotation(Mth.wrapDegrees(xRot - 90), yRot).scale(input.y));
        result = result.add(Vec3.directionFromRotation(xRot, Mth.wrapDegrees(yRot - 90)).scale(input.x));
        return result;
    }

    public static Matrix3d rotation3x3(double pitch, double yaw, double roll) {
        return zRotation3x3(roll).mul(xRotation3x3(pitch)).mul(yRotation3x3(yaw));
    }

    public static Matrix3d xRotation3x3(double angle) {
        angle = angle * 0.017453292519943295F;
        return new Matrix3d(
                1.0f, 0.0f, 0.0f,
                0.0f, Math.cos(angle), -Math.sin(angle),
                0.0f, Math.sin(angle), Math.cos(angle)
        );
    }

    public static Matrix3d yRotation3x3(double angle) {
        angle = angle * 0.017453292519943295F;
        return new Matrix3d(
                Math.cos(angle), 0.0f, Math.sin(angle),
                0.0f, 1.0f, 0.0f,
                -Math.sin(angle), 0.0f, Math.cos(angle)
        );
    }
    public static Matrix3d zRotation3x3(double angle) {
        angle = angle * 0.017453292519943295F;
        return new Matrix3d(
                Math.cos(angle), -Math.sin(angle), 0.0f,
                Math.sin(angle), Math.cos(angle), 0.0f,
                0.0f, 0.0f, 1.0f
        );
    }
}
