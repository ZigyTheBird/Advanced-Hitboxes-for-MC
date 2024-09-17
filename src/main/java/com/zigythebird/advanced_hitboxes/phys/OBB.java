package com.zigythebird.advanced_hitboxes.phys;

import com.zigythebird.advanced_hitboxes.utils.ModMath;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;
import org.joml.Vector3d;

import java.util.Optional;

public class OBB extends AdvancedHitbox {

    public Vec3 center;
    public Vector3d rotation;
    public Vec3 size;

    public Vec3 extent;
    public Vec3 axisX;
    public Vec3 axisY;
    public Vec3 axisZ;
    public Vec3 scaledAxisX;
    public Vec3 scaledAxisY;
    public Vec3 scaledAxisZ;
    public Vec3 vertex1;
    public Vec3 vertex2;
    public Vec3 vertex3;
    public Vec3 vertex4;
    public Vec3 vertex5;
    public Vec3 vertex6;
    public Vec3 vertex7;
    public Vec3 vertex8;
    public Vec3[] vertices;
    public Matrix3d orientation;

    public OBB(String name, Vec3 center, Vec3 size, Vector3d rotation) {
        super(name);
        this.center = center;
        this.size = size;
        this.extent = new Vec3(size.x/2, size.y/2, size.z/2);
        this.rotation = rotation;
        calculateOrientation();
    }

    public OBB(String name, Vec3 center, double xSize, double ySize, double zSize, double xRot, double yRot, double zRot) {
        this(name, center, new Vec3(xSize, ySize, zSize), new Vector3d(xRot, yRot, zRot));
    }

    public OBB(String name, AABB box) {
        this(name, box.getCenter(), new Vec3(box.getXsize(), box.getYsize(), box.getZsize()), new Vector3d(0, 0, 0));
    }

    public OBB(String name, OBB obb) {
        this(name, obb.center, obb.size, new Vector3d(obb.rotation));
    }

    public OBB(OBB obb) {
        this(obb.name, obb);
    }

    public void update(Vec3 center, Vec3 size, Vector3d rotation) {
        this.center = center;
        this.size = size;
        this.rotation = rotation;
        this.extent = new Vec3(size.x/2, size.y/2, size.z/2);
        calculateOrientation();
    }

    public void rotateAroundPivot(Vector3d rotation, Vec3 pivot) {
        if (this.rotation != null) {
            this.rotation = rotation.add(rotation);
        }
        else {
            this.rotation = rotation;
        }

        //"l" is short for length here
        double l1 = Math.sqrt(Math.pow(this.center.z - pivot.z, 2) + Math.pow(this.center.y - pivot.y, 2));
        double inverse1 = Math.atan2(Math.abs((this.center.y - pivot.y) / l1), Math.abs((this.center.z - pivot.z) / l1));
        inverse1 = Double.isNaN(inverse1) ? 0 : inverse1;
        double transformL1 = Math.sqrt((2 * Math.pow(l1, 2)) - (2 * Math.pow(l1, 2)) * Math.cos(Math.toRadians(rotation.x)));
        this.center = this.center.add(0,
                Math.cos(Math.toRadians(rotation.x / 2) - inverse1) * transformL1,
                Math.sin(Math.toRadians(rotation.x / 2) - inverse1) * transformL1);
        double l2 = Math.sqrt(Math.pow(this.center.z - pivot.z, 2) + Math.pow(this.center.x - pivot.x, 2));
        double inverse2 = Math.atan2(Math.abs((this.center.x - pivot.x) / l2), Math.abs((this.center.z - pivot.z) / l2));
        inverse2 = Double.isNaN(inverse2) ? 0 : inverse2;
        double transformL2 = Math.sqrt((2 * Math.pow(l2, 2)) - (2 * Math.pow(l2, 2)) * Math.cos(Math.toRadians(rotation.y)));
        this.center = this.center.add(0,
                Math.cos(Math.toRadians(rotation.y / 2) - inverse2) * transformL2,
                Math.sin(Math.toRadians(rotation.y / 2) - inverse2) * transformL2);
        double l3 = Math.sqrt(Math.pow(this.center.x - pivot.x, 2) + Math.pow(this.center.y - pivot.y, 2));
        double inverse3 = Math.atan2(Math.abs((this.center.y - pivot.y) / l3), Math.abs((this.center.x - pivot.x) / l3));
        inverse3 = Double.isNaN(inverse3) ? 0 : inverse3;
        double transformL3 = Math.sqrt((2 * Math.pow(l3, 2)) - (2 * Math.pow(l3, 2)) * Math.cos(Math.toRadians(rotation.z)));
        this.center = this.center.add(0,
                Math.cos(Math.toRadians(rotation.z / 2) - inverse3) * transformL3,
                Math.sin(Math.toRadians(rotation.z / 2) - inverse3) * transformL3);
    }

    public OBB inflate(double x, double y, double z) {
        return new OBB(this.name, this.center, this.size.x + (x * 2), this.size.y + (y * 2), this.size.z + (z * 2), this.rotation.x, this.rotation.y, this.rotation.z);
    }

    public OBB inflate(double value) {
        return this.inflate(value, value, value);
    }

    public OBB copy() {
        return new OBB(this);
    }

    public OBB offsetAlongAxisX(double offset) {
        this.center = this.center.add(this.axisX.scale(offset));
        return this;
    }

    public OBB offsetAlongAxisY(double offset) {
        this.center = this.center.add(this.axisY.scale(offset));
        return this;
    }

    public OBB offsetAlongAxisZ(double offset) {
        this.center = this.center.add(this.axisZ.scale(offset));
        return this;
    }

    public OBB offset(Vec3 offset) {
        this.center = this.center.add(offset);
        return this;
    }

    public OBB offset(double offsetX, double offsetY, double offsetZ) {
        this.center = this.center.add(offsetX, offsetY, offsetZ);
        return this;
    }

    public OBB scale(double scale) {
        this.size = this.size.scale(scale);
        this.extent = this.size.scale(0.5);
        return this;
    }

    public void calculateOrientation() {
        orientation = ModMath.rotation3x3(rotation.x, rotation.y, rotation.z);
        Vector3d axisXTemp = new Vector3d();
        Vector3d axisYTemp = new Vector3d();
        Vector3d axisZTemp = new Vector3d();
        orientation.getRow(0, axisXTemp);
        orientation.getRow(1, axisYTemp);
        orientation.getRow(2, axisZTemp);
        axisX = ModMath.vector3dToVec3(axisXTemp);
        axisY = ModMath.vector3dToVec3(axisYTemp);
        axisZ = ModMath.vector3dToVec3(axisZTemp);
    }

    public OBB updateVertex() {
        this.scaledAxisX = this.axisX.scale(this.extent.x);
        this.scaledAxisY = this.axisY.scale(this.extent.y);
        this.scaledAxisZ = this.axisZ.scale(this.extent.z);
        this.vertex1 = this.center.subtract(this.scaledAxisZ).subtract(this.scaledAxisX).subtract(this.scaledAxisY); //bottom left back
        this.vertex2 = this.center.subtract(this.scaledAxisZ).add(this.scaledAxisX).subtract(this.scaledAxisY); //bottom right back
        this.vertex3 = this.center.subtract(this.scaledAxisZ).add(this.scaledAxisX).add(this.scaledAxisY); //top right back
        this.vertex4 = this.center.subtract(this.scaledAxisZ).subtract(this.scaledAxisX).add(this.scaledAxisY); //top left back
        this.vertex5 = this.center.add(this.scaledAxisZ).subtract(this.scaledAxisX).subtract(this.scaledAxisY); //bottom left front
        this.vertex6 = this.center.add(this.scaledAxisZ).add(this.scaledAxisX).subtract(this.scaledAxisY); //bottom right front
        this.vertex7 = this.center.add(this.scaledAxisZ).add(this.scaledAxisX).add(this.scaledAxisY); //top right front
        this.vertex8 = this.center.add(this.scaledAxisZ).subtract(this.scaledAxisX).add(this.scaledAxisY); //top left front
        this.vertices = new Vec3[]{this.vertex1, this.vertex2, this.vertex3, this.vertex4, this.vertex5, this.vertex6, this.vertex7, this.vertex8};
        return this;
    }

    public boolean contains(Vec3 point) {
        Vec3 distancef = point.subtract(this.center);
        Vector3d distance = new Vector3d(distancef.x, distancef.y, distancef.z);
        distance.mulTranspose(this.orientation);
        return Math.abs(distance.x()) < this.extent.x && Math.abs(distance.y()) < this.extent.y && Math.abs(distance.z()) < this.extent.z;
    }

    public RaycastResult raycast(Vec3 origin, Vec3 direction) {
        Vec3 p = center.subtract(origin);
        Vec3 f = new Vec3(axisX.dot(direction), axisY.dot(direction), axisZ.dot(direction));
        Vec3 e = new Vec3(axisX.dot(p), axisY.dot(p), axisZ.dot(p));

        NonNullList<Float> t = NonNullList.withSize(6, 0F);
        for (int i = 0; i < 3; ++i) {
            double value = f.x;
            double value2 = e.x;
            double size = extent.x;
            switch (i) {
                case 1 -> {
                    value = f.y;
                    value2 = e.y;
                    size = extent.y;
                }
                case 2 -> {
                    value = f.z;
                    value2 = e.z;
                    size = extent.z;
                }
            }
            if (value == 0) {
                if (-value2 - size > 0 || -value2 + size < 0) {
                    return new RaycastResult(Optional.empty(), -1);
                }
                value = 0.00001f; // Avoid div by 0!
            }
            t.set(i * 2, (float) ((value2 + size) / value)); // min
            t.set(i * 2 + 1, (float) ((value2 - size) / value)); // max
        }

        float tmin = Math.max(Math.max(Math.min(t.get(0), t.get(1)), Math.min(t.get(2), t.get(3))), Math.min(t.get(4), t.get(5)));
        float tmax = Math.min(Math.min(Math.max(t.get(0), t.get(1)), Math.max(t.get(2), t.get(3))), Math.max(t.get(4), t.get(5)));

        if (tmax < 0) {
            return new RaycastResult(Optional.empty(), -1);
        }

        if (tmin > tmax) {
            return new RaycastResult(Optional.empty(), -1);
        }

        if (tmin < 0.0f) {
            return new RaycastResult(Optional.of(direction.scale(tmax).add(origin)), tmax);
        }
        return new RaycastResult(Optional.of(direction.scale(tmin).add(origin)), tmin);
    }

    public Optional<Vec3> Linetest(Vec3 start, Vec3 end) {
        Vec3 endMinusStart = end.subtract(start);
        Vec3 startMinusEnd = start.subtract(end);
        if (endMinusStart.dot(endMinusStart) < 0.0000001f) {
            return contains(start) ? Optional.of(start) : Optional.empty();
        }
        RaycastResult t = raycast(start, endMinusStart.normalize());
        return t.result() >= 0 && t.result() * t.result() <= startMinusEnd.dot(startMinusEnd) && t.location().isPresent() ? t.location() : Optional.empty();
    }

    public boolean intersects(AABB boundingBox) {
        OBB otherOBB = (new OBB(null, boundingBox)).updateVertex();
        return Intersects(this, otherOBB);
    }

    public boolean intersects(OBB otherOBB) {
        return Intersects(this, otherOBB);
    }

    public static boolean Intersects(OBB a, OBB b) {
        if (Separated(a.vertices, b.vertices, a.scaledAxisX)) {
            return false;
        } else if (Separated(a.vertices, b.vertices, a.scaledAxisY)) {
            return false;
        } else if (Separated(a.vertices, b.vertices, a.scaledAxisZ)) {
            return false;
        } else if (Separated(a.vertices, b.vertices, b.scaledAxisX)) {
            return false;
        } else if (Separated(a.vertices, b.vertices, b.scaledAxisY)) {
            return false;
        } else if (Separated(a.vertices, b.vertices, b.scaledAxisZ)) {
            return false;
        } else if (Separated(a.vertices, b.vertices, a.scaledAxisX.cross(b.scaledAxisX))) {
            return false;
        } else if (Separated(a.vertices, b.vertices, a.scaledAxisX.cross(b.scaledAxisY))) {
            return false;
        } else if (Separated(a.vertices, b.vertices, a.scaledAxisX.cross(b.scaledAxisZ))) {
            return false;
        } else if (Separated(a.vertices, b.vertices, a.scaledAxisY.cross(b.scaledAxisX))) {
            return false;
        } else if (Separated(a.vertices, b.vertices, a.scaledAxisY.cross(b.scaledAxisY))) {
            return false;
        } else if (Separated(a.vertices, b.vertices, a.scaledAxisY.cross(b.scaledAxisZ))) {
            return false;
        } else if (Separated(a.vertices, b.vertices, a.scaledAxisZ.cross(b.scaledAxisX))) {
            return false;
        } else if (Separated(a.vertices, b.vertices, a.scaledAxisZ.cross(b.scaledAxisY))) {
            return false;
        } else {
            return !Separated(a.vertices, b.vertices, a.scaledAxisZ.cross(b.scaledAxisZ));
        }
    }

    private static boolean Separated(Vec3[] vertsA, Vec3[] vertsB, Vec3 axis) {
        if (axis.equals(Vec3.ZERO)) {
            return false;
        } else {
            double aMin = Double.POSITIVE_INFINITY;
            double aMax = Double.NEGATIVE_INFINITY;
            double bMin = Double.POSITIVE_INFINITY;
            double bMax = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < 8; ++i) {
                double aDist = vertsA[i].dot(axis);
                aMin = aDist < aMin ? aDist : aMin;
                aMax = aDist > aMax ? aDist : aMax;
                double bDist = vertsB[i].dot(axis);
                bMin = bDist < bMin ? bDist : bMin;
                bMax = bDist > bMax ? bDist : bMax;
            }

            double longSpan = Math.max(aMax, bMax) - Math.min(aMin, bMin);
            double sumSpan = aMax - aMin + bMax - bMin;
            return longSpan >= sumSpan;
        }
    }
}
