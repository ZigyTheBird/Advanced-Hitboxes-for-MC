package com.zigythebird.advanced_hitboxes.phys;

import com.zigythebird.advanced_hitboxes.utils.ModMath;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Mostly based on Gabor Szauer's Game Physics Cookbook.
 */
public class OBB implements AdvancedHitbox {
    private final String name;

    public Vec3 center;
    public Vec3 extent;
    public Matrix3f orientation;
    public Vec3[] vertices;

    public OBB(String name, Vec3[] vertices, Vec3 extent, Matrix3f orientation) {
        this.name = name;
        this.vertices = vertices;
        this.updateCenter();
        this.extent = extent;
        this.orientation = orientation;
    }

    public OBB(AABB box) {
        this(null, box);
    }

    public OBB(String name, AABB box) {
        this.name = name;
        this.center = box.getCenter();
        this.extent = new Vec3(box.getXsize()/2, box.getYsize()/2, box.getZsize()/2);
        this.orientation = new Matrix3f();
        
        Vec3 scaledAxisX = new Vec3(1, 0, 0).scale(extent.x);
        Vec3 scaledAxisY = new Vec3(0, 1, 0).scale(extent.y);
        Vec3 scaledAxisZ = new Vec3(0, 0, 1).scale(extent.z);
        Vec3 vertex1 = center.subtract(scaledAxisZ).subtract(scaledAxisX).subtract(scaledAxisY); //bottom left back
        Vec3 vertex2 = center.subtract(scaledAxisZ).add(scaledAxisX).subtract(scaledAxisY); //bottom right back
        Vec3 vertex3 = center.subtract(scaledAxisZ).add(scaledAxisX).add(scaledAxisY); //top right back
        Vec3 vertex4 = center.subtract(scaledAxisZ).subtract(scaledAxisX).add(scaledAxisY); //top left back
        Vec3 vertex5 = center.add(scaledAxisZ).subtract(scaledAxisX).subtract(scaledAxisY); //bottom left front
        Vec3 vertex6 = center.add(scaledAxisZ).add(scaledAxisX).subtract(scaledAxisY); //bottom right front
        Vec3 vertex7 = center.add(scaledAxisZ).add(scaledAxisX).add(scaledAxisY); //top right front
        Vec3 vertex8 = center.add(scaledAxisZ).subtract(scaledAxisX).add(scaledAxisY); //top left front
        vertices = new Vec3[]{vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7, vertex8};
    }

    public OBB(String name, OBB obb) {
        this.name = name;
        this.center = obb.center;
        this.extent = obb.extent;
        this.orientation = new Matrix3f(obb.orientation);
        this.vertices = obb.vertices.clone();
    }

    public OBB(OBB obb) {
        this(obb.name, obb);
    }

    public void update(Vec3[] vertices, Vec3 extent, Matrix3f orientation) {
        this.vertices = vertices;
        this.updateCenter();
        this.extent = extent;
        this.orientation = orientation;
    }

    public void updateCenter() {
        Vector3d center = new Vector3d();
        for (Vec3 vec3 : vertices) {
            center.add(vec3.x(), vec3.y(), vec3.z());
        }
        center.div(8);
        this.center = ModMath.vector3dToVec3(center);
    }

    public OBB inflate(double value) {
        this.extent = this.extent.add(value, value, value);
        Vec3[] newVertices = new Vec3[8];
        for (int i=0; i<8; i++) {
            newVertices[i] = vertices[i].add(vertices[i].subtract(center).normalize().scale(value));
        }
        vertices = newVertices;
        return this;
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
        return this.offset(offset.x(), offset.y(), offset.z());
    }

    public OBB offset(double offsetX, double offsetY, double offsetZ) {
        this.center = this.center.add(offsetX, offsetY, offsetZ);
        Vec3[] newVertices = new Vec3[8];
        for (int i=0; i<8; i++) {
            Vec3 vec3 = vertices[i];
            newVertices[i] = new Vec3(vec3.x() + offsetX, vec3.y() + offsetY, vec3.z() + offsetZ);
        }
        vertices = newVertices;
        return this;
    }

    public OBB scale(double scale) {
        this.extent = this.extent.scale(scale);
        Vec3[] newVertices = new Vec3[8];
        for (int i=0; i<8; i++) {
            Vec3 vec3 = vertices[i].subtract(center);
            newVertices[i] = new Vec3(vec3.x() * scale + center.x(), vec3.y() * scale + center.y(), vec3.z() * scale + center.z());
        }
        vertices = newVertices;
        return this;
    }

    public Vec3 getMaxVertex() {
        Vec3 scaledAxisX = getAxisX().scale(extent.x);
        Vec3 scaledAxisY = getAxisY().scale(extent.y);
        Vec3 scaledAxisZ = getAxisZ().scale(extent.z);
        return this.center.add(scaledAxisZ.x + scaledAxisX.x + scaledAxisY.x,
                scaledAxisZ.y + scaledAxisX.y + scaledAxisY.y,
                scaledAxisZ.z + scaledAxisX.z + scaledAxisY.z);
    }

    public Vec3 getMinVertex() {
        Vec3 scaledAxisX = getAxisX().scale(extent.x);
        Vec3 scaledAxisY = getAxisY().scale(extent.y);
        Vec3 scaledAxisZ = getAxisZ().scale(extent.z);
        return this.center.subtract(scaledAxisZ.x + scaledAxisX.x + scaledAxisY.x,
                        scaledAxisZ.y + scaledAxisX.y + scaledAxisY.y,
                        scaledAxisZ.z + scaledAxisX.z + scaledAxisY.z);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }
    
    public Vec3 getAxisX() {
        Vector3f axis = new Vector3f();
        orientation.getColumn(0, axis);
        return ModMath.vector3fToVec3(axis);
    }

    public Vec3 getAxisY() {
        Vector3f axis = new Vector3f();
        orientation.getColumn(1, axis);
        return ModMath.vector3fToVec3(axis);
    }

    public Vec3 getAxisZ() {
        Vector3f axis = new Vector3f();
        orientation.getColumn(2, axis);
        return ModMath.vector3fToVec3(axis);
    }

    public boolean contains(Vec3 point) {
        Vec3 dir = point.subtract(this.center);

        for (int i = 0; i < 3; ++i) {
		    Vector3f axis = new Vector3f();
            orientation.getColumn(i, axis);

            double distance = dir.dot(ModMath.vector3fToVec3(axis));

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
        Vec3 axisX = getAxisX();
        Vec3 axisY = getAxisY();
        Vec3 axisZ = getAxisZ();
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
        return intersects(this, otherOBB).intersects();
    }

    public boolean intersects(OBB otherOBB) {
        return intersects(this, otherOBB).intersects();
    }

    /**
     * <a href="https://www.youtube.com/watch?v=EB6NY5sGd08">...</a>
     * SethBling the LEGEND coming in clutch!!!
     * <p>
     * The axis in the result is the axis with the shortest overlap.
     * And the length value is the length of said overlap.
     */
    public static OBBIntersectResult intersects(OBB a, OBB b) {
        List<Vector3d> test = new ArrayList<>();

        //TODO Clean up this horrible code
        test.add(new Vector3d(a.getAxisX().x, a.getAxisX().y, a.getAxisX().z));
        test.add(new Vector3d(a.getAxisY().x, a.getAxisY().y, a.getAxisY().z));
        test.add(new Vector3d(a.getAxisY().x, a.getAxisY().y, a.getAxisY().z));
        test.add(new Vector3d(b.getAxisX().x, b.getAxisX().y, b.getAxisX().z));
        test.add(new Vector3d(b.getAxisY().x, b.getAxisY().y, b.getAxisY().z));
        test.add(new Vector3d(b.getAxisZ().x, b.getAxisZ().y, b.getAxisZ().z));

        for (int i = 0; i < 3; ++i) { // Fill out rest of axis
            test.add(6 + i * 3, test.get(i).cross(test.get(0)));
            test.add(6 + i * 3 + 1, test.get(i).cross(test.get(1)));
            test.add(6 + i * 3 + 2, test.get(i).cross(test.get(2)));
        }

        Vector3d axis = null;
        double distance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < 15; ++i) {
            Vector3d currentAxis = test.get(i);
            if (currentAxis.x() == 0 && currentAxis.y() == 0 && currentAxis.z() == 0) continue;
            OBBOverlapResult result = overlapOnAxis(a, b, currentAxis);
            if (!result.overlap()) {
                return new OBBIntersectResult(false, null, null); // Seperating axis found
            }
            else if (result.result() < distance) {
                distance = result.result();
                axis = currentAxis;
            }
        }

        if (axis == null) {
            return new OBBIntersectResult(false, null, null); //IDFK
        }
        return new OBBIntersectResult(true, axis, distance); // Seperating axis not found
    }

    public static OBBOverlapResult overlapOnAxis(OBB obb1, OBB obb2, Vector3d axis) {
        Vec3 axis2 = ModMath.vector3dToVec3(axis);
        Vector2d a = obb1.getInterval(axis2);
        Vector2d b = obb2.getInterval(axis2);
        float length = (float) (Math.min(a.y(), b.y()) - Math.max(a.x(), b.x()));
        return new OBBOverlapResult(!(b.y() < a.x() || a.y() < b.x()), length);
    }

    /**
     * In the resulting vector:
     * x = min
     * y = max
     */
    public Vector2d getInterval(Vec3 axis) {
        Vector2d result = new Vector2d(0, 0);
        result.x = result.y = axis.dot(vertices[0]);

        for (int i = 1; i < 8; ++i) {
            double projection = axis.dot(vertices[i]);
            result.x = Math.min(projection, result.x);
            result.y = Math.max(projection, result.y);
        }

        return result;
    }

    /**
     * <a href="https://www.youtube.com/watch?v=EB6NY5sGd08">...</a>
     * SethBling the LEGEND coming in clutch!!!
     * <p>
     * The axis in the result is the axis with the shortest overlap.
     * And the length value is the length of said overlap.
     */
    public record OBBIntersectResult(boolean intersects, @Nullable Vector3d axis, @Nullable Double length) {}
    public record OBBOverlapResult(boolean overlap, double result) {}
}
