package com.zigythebird.advanced_hitboxes.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class ModMath {
    public static void rotateAroundPivot(Vector3d rotation, Vector3d rotationToApply, Vector3d position, float pivotX, float pivotY, float pivotZ) {
        Matrix3d orientation = ModMath.rotation3x3(rotation.x, rotation.y, rotation.z);
        Vector3d axisX = new Vector3d();
        orientation.getRow(0, axisX);
        Quaterniond qx = new Quaterniond().fromAxisAngleRad(axisX, rotationToApply.x);
        position.set(new Vector3d(position.x - pivotX, position.y - pivotY, position.z - pivotZ).rotate(qx).add(pivotX, pivotY, pivotZ));
        rotation.add(rotationToApply.x, 0, 0);

        orientation = ModMath.rotation3x3(rotation.x, rotation.y, rotation.z);
        Vector3d axisY = new Vector3d();
        orientation.getRow(1, axisY);
        Quaterniond qy = new Quaterniond().fromAxisAngleRad(axisY, rotationToApply.y);
        position.set(new Vector3d(position.x - pivotX, position.y - pivotY, position.z - pivotZ).rotate(qy).add(pivotX, pivotY, pivotZ));
        rotation.add(0, rotationToApply.y, 0);

        orientation = ModMath.rotation3x3(rotation.x, rotation.y, rotation.z);
        Vector3d axisZ = new Vector3d();
        orientation.getRow(2, axisZ);
        Quaterniond qz = new Quaterniond().fromAxisAngleRad(axisZ, rotationToApply.z);
        position.set(new Vector3d(position.x - pivotX, position.y - pivotY, position.z - pivotZ).rotate(qz).add(pivotX, pivotY, pivotZ));
        rotation.add(0, 0, rotationToApply.z);
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
        return new Matrix3d(
                1.0f, 0.0f, 0.0f,
                0.0f, Math.cos(angle), -Math.sin(angle),
                0.0f, Math.sin(angle), Math.cos(angle)
        );
    }

    public static Matrix3d yRotation3x3(double angle) {
        return new Matrix3d(
                Math.cos(angle), 0.0f, Math.sin(angle),
                0.0f, 1.0f, 0.0f,
                -Math.sin(angle), 0.0f, Math.cos(angle)
        );
    }
    public static Matrix3d zRotation3x3(double angle) {
        return new Matrix3d(
                Math.cos(angle), -Math.sin(angle), 0.0f,
                Math.sin(angle), Math.cos(angle), 0.0f,
                0.0f, 0.0f, 1.0f
        );
    }

    public static double clampToRadian(double f){
        final double a = Math.PI*2;
        double b = ((f + Math.PI)%a);
        if(b < 0){
            b += a;
        }
        return (float) (b - Math.PI);
    }
}
