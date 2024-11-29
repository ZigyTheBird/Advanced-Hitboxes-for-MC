package com.zigythebird.advanced_hitboxes.phys;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface AdvancedHitbox {
    @NotNull String getName();
    boolean contains(Vec3 point);
    RaycastResult raycast(Vec3 origin, Vec3 direction);
    Optional<Vec3> linetest(Vec3 start, Vec3 end);
    boolean intersects(AABB boundingBox);
    boolean intersects(OBB obb);

    record RaycastResult(Optional<Vec3> location, float result) {}
}
