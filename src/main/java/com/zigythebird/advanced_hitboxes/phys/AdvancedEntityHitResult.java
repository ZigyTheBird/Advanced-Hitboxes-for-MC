package com.zigythebird.advanced_hitboxes.phys;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class AdvancedEntityHitResult extends EntityHitResult {

    private final String hitboxName;
    private final double distance;
    private final Vec3 hitPosition;

    public AdvancedEntityHitResult(Entity entity, String hitboxName, double distance, Vec3 hitPosition) {
        super(entity);
        this.hitboxName = hitboxName;
        this.hitPosition = hitPosition;
        this.distance = distance;
    }

    public double getDistance() {return distance;}

    public String getHitboxName() {
        return hitboxName;
    }

    public Vec3 getHitPosition() {
        return hitPosition;
    }
}
