package com.zigythebird.advanced_hitboxes.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class ModMath {
    public static void rotateAroundPivot(Vector3d rotation, Vector3d rotationToApply, Vector3d position, float pivotX, float pivotY, float pivotZ) {
        Matrix3d orientation = new Matrix3d();
        new Quaterniond().rotationXYZ(rotation.x, rotation.y, rotation.z).get(orientation);
        Vector3d axisX = new Vector3d();
        orientation.getColumn(0, axisX);
        Quaterniond qx = new Quaterniond().fromAxisAngleRad(axisX, rotationToApply.x);
        position.set(new Vector3d(position.x - pivotX, position.y - pivotY, position.z - pivotZ).rotate(qx).add(pivotX, pivotY, pivotZ));
        rotation.add(rotationToApply.x, 0, 0);

        new Quaterniond().rotationXYZ(rotation.x, rotation.y, rotation.z).get(orientation);
        Vector3d axisY = new Vector3d();
        orientation.getColumn(1, axisY);
        Quaterniond qy = new Quaterniond().fromAxisAngleRad(axisY, rotationToApply.y);
        position.set(new Vector3d(position.x - pivotX, position.y - pivotY, position.z - pivotZ).rotate(qy).add(pivotX, pivotY, pivotZ));
        rotation.add(0, rotationToApply.y, 0);

        new Quaterniond().rotationXYZ(rotation.x, rotation.y, rotation.z).get(orientation);
        Vector3d axisZ = new Vector3d();
        orientation.getColumn(2, axisZ);
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

    public static double clampToRadian(double f){
        final double a = Math.PI*2;
        double b = ((f + Math.PI)%a);
        if(b < 0){
            b += a;
        }
        return b - Math.PI;
    }
}
