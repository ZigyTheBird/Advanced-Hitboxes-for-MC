package com.zigythebird.advanced_hitboxes.phys;

import com.zigythebird.advanced_hitboxes.utils.ModMath;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OBB implements AdvancedHitbox {
    private final String name;

    public Vec3 center;
    public Vector3d rotation;
    public Vec3 size;

    public Vec3 extent;
    public Matrix3d orientation;
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

    public OBB(String name, Vec3 center, Vec3 size, Vector3d rotation) {
        this.name = name;
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

    public OBB inflate(double x, double y, double z) {
        this.size = new Vec3(this.size.x + (x * 2), this.size.y + (y * 2), this.size.z + (z * 2));
        this.extent = this.size.scale(0.5);
        return this;
    }

    public OBB inflate(double value) {
        return this.inflate(value, value, value);
    }

    public OBB copy() {
        return new OBB(this);
    }

    public AABB toAABB() {
        Vec3 min = getMinVertex();
        Vec3 max = getMaxVertex();
        return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
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
        orientation = new Matrix3d();
        new Quaterniond().rotationXYZ(rotation.x, rotation.y, rotation.z).get(orientation);
        Vector3d axisXTemp = new Vector3d();
        Vector3d axisYTemp = new Vector3d();
        Vector3d axisZTemp = new Vector3d();
        orientation.getColumn(0, axisXTemp);
        orientation.getColumn(1, axisYTemp);
        orientation.getColumn(2, axisZTemp);
        axisX = ModMath.vector3dToVec3(axisXTemp);
        axisY = ModMath.vector3dToVec3(axisYTemp);
        axisZ = ModMath.vector3dToVec3(axisZTemp);
    }

    public OBB updateScaledAxis() {
        this.scaledAxisX = axisX.scale(this.extent.x);
        this.scaledAxisY = axisY.scale(this.extent.y);
        this.scaledAxisZ = axisZ.scale(this.extent.z);
        return this;
    }

    public OBB updateVertex() {
        this.scaledAxisX = axisX.scale(this.extent.x);
        this.scaledAxisY = axisY.scale(this.extent.y);
        this.scaledAxisZ = axisZ.scale(this.extent.z);
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

    public Vec3 getMaxVertex() {
        return this.center.add(Math.abs(this.scaledAxisZ.x) + Math.abs(this.scaledAxisX.x) + Math.abs(this.scaledAxisY.x),
                Math.abs(this.scaledAxisZ.y) + Math.abs(this.scaledAxisX.y) + Math.abs(this.scaledAxisY.y),
                Math.abs(this.scaledAxisZ.z) + Math.abs(this.scaledAxisX.z) + Math.abs(this.scaledAxisY.z));
    }

    public Vec3 getMinVertex() {
        return this.center.subtract(Math.abs(this.scaledAxisZ.x) + Math.abs(this.scaledAxisX.x) + Math.abs(this.scaledAxisY.x),
                        Math.abs(this.scaledAxisZ.y) + Math.abs(this.scaledAxisX.y) + Math.abs(this.scaledAxisY.y),
                        Math.abs(this.scaledAxisZ.z) + Math.abs(this.scaledAxisX.z) + Math.abs(this.scaledAxisY.z));
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    public boolean contains(Vec3 point) {
        Vec3 dir = point.subtract(this.center);

        for (int i = 0; i < 3; ++i) {
		    Vector3d axis = new Vector3d();
            orientation.getColumn(i, axis);

            double distance = dir.dot(ModMath.vector3dToVec3(axis));

            double size = extent.x;
            switch (i) {
                case 1 -> size = extent.y;
                case 2 -> size = extent.z;
            }

            if (distance > size) {
                return false;
            }
            if (distance < -size) {
                return false;
            }
        }

        return true;
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

    public Optional<Vec3> linetest(Vec3 start, Vec3 end) {
        Vec3 endMinusStart = end.subtract(start);
        Vec3 startMinusEnd = start.subtract(end);
        if (endMinusStart.dot(endMinusStart) < 0.0000001f) {
            return contains(start) ? Optional.of(start) : Optional.empty();
        }
        RaycastResult t = raycast(start, endMinusStart.normalize());
        return t.result() >= 0 && t.result() * t.result() <= startMinusEnd.dot(startMinusEnd) && t.location().isPresent() ? t.location() : Optional.empty();
    }

    public boolean intersects(AABB boundingBox) {
        OBB otherOBB = new OBB(null, boundingBox);
        return Intersects(this, otherOBB);
    }

    public boolean intersects(OBB otherOBB) {
        return Intersects(this, otherOBB);
    }

    public static boolean Intersects(OBB a, OBB b) {
        List<Vector3d> test = new ArrayList<>();

        Vector3d vec0 = new Vector3d();
        Vector3d vec1 = new Vector3d();
        Vector3d vec2 = new Vector3d();
        Vector3d vec10 = new Vector3d();
        Vector3d vec11 = new Vector3d();
        Vector3d vec12 = new Vector3d();
        a.orientation.getColumn(0, vec0);
        a.orientation.getColumn(1, vec1);
        a.orientation.getColumn(2, vec2);
        b.orientation.getColumn(0, vec10);
        b.orientation.getColumn(1, vec11);
        b.orientation.getColumn(2, vec12);
        test.add(vec0);
        test.add(vec1);
        test.add(vec2);
        test.add(vec10);
        test.add(vec11);
        test.add(vec12);

        for (int i = 0; i < 3; ++i) { // Fill out rest of axis
            test.add(6 + i * 3, test.get(i).cross(test.get(0)));
            test.add(6 + i * 3 + 1, test.get(i).cross(test.get(1)));
            test.add(6 + i * 3 + 2, test.get(i).cross(test.get(2)));
        }

        for (int i = 0; i < 15; ++i) {
            if (!OverlapOnAxis(a, b, test.get(i))) {
                return false; // Seperating axis found
            }
        }

        return true; // Seperating axis not found
    }

    public static boolean OverlapOnAxis(OBB obb1, OBB obb2, Vector3d axis) {
        Vec3 axis2 = ModMath.vector3dToVec3(axis);
        Vector2d a = obb1.GetInterval(axis2);
        Vector2d b = obb2.GetInterval(axis2);
        return b.x <= a.y && a.x <= b.y;
    }

    /**
     * In the resulting vector:
     * x = min
     * y = max
     */
    public Vector2d GetInterval(Vec3 axis) {
        this.updateVertex();

        Vector2d result = new Vector2d(0, 0);
        result.x = result.y = axis.dot(vertices[0]);

        for (int i = 1; i < 8; ++i) {
            double projection = axis.dot(vertices[i]);
            result.x = Math.min(projection, result.x);
            result.y = Math.max(projection, result.y);
        }

        return result;
    }
}
