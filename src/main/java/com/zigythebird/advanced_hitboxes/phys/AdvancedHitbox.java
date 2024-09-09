package com.zigythebird.advanced_hitboxes.phys;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public abstract class AdvancedHitbox {
    public final String name;

    public AdvancedHitbox(String name) {
        this.name = name;
    }

    public abstract boolean contains(Vec3 point);
    public abstract RaycastResult raycast(Vec3 origin, Vec3 direction);
    public abstract Optional<Vec3> Linetest(Vec3 start, Vec3 end);
    public abstract boolean intersects(AABB boundingBox);
    public abstract boolean intersects(OBB obb);

    public record RaycastResult(Optional<Vec3> location, float result) {}
}
