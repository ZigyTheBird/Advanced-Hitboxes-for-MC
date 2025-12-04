package com.zigythebird.advanced_hitboxes.utils;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.mixin.accessors.LevelAccessor;
import com.zigythebird.advanced_hitboxes.phys.AdvancedEntityHitResult;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

//TODO Probably add more methods than this
public class AdvancedRaytraceUtils {
    public static EntityHitResult raytrace(Entity shooter, float length) {
        Vec3 startVec = shooter.getEyePosition();
        Vec3 endVec = shooter.getEyePosition().add(Vec3.directionFromRotation(shooter.getXRot(), shooter.getYRot()).scale(length));
        AABB boundingBox = new AABB(startVec.x-1, startVec.y-1, startVec.z-1, startVec.x+1, startVec.y+1, startVec.z+1).inflate(length);
        return raytrace(shooter.level(), startVec, endVec, boundingBox, (entity) -> entity != shooter);
    }

    public static EntityHitResult raytrace(Level level, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> predicate) {
        double d = Double.MAX_VALUE;
        Entity entity = null;
        String name = null;
        Vec3 hitPosition = null;

        for (Entity entity2 : level.getEntities((Entity) null, boundingBox, predicate)) {
            if (entity2 instanceof AdvancedHitboxEntity || entity2 instanceof Player) {
                HitboxUtils.updateOrMakeHitboxesForEntity((AdvancedHitboxEntity) entity2);
                for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity2).getHitboxes()) {
                    double e;
                    Optional<Vec3> optional = hitbox.linetest(startVec, endVec);
                    if (optional.isEmpty() || !((e = startVec.distanceToSqr(optional.get())) < d)) continue;
                    entity = entity2;
                    d = e;
                    name = hitbox.getName();
                    hitPosition = optional.get();
                }
            }
            else {
                double e;
                AABB aABB = entity2.getBoundingBox().inflate(0.4);
                Optional<Vec3> optional = aABB.clip(startVec, endVec);
                if (optional.isEmpty() || !((e = startVec.distanceToSqr(optional.get())) < d)) continue;
                entity = entity2;
                d = e;
                hitPosition = optional.get();
            }
        }

        if (entity == null) {
            return null;
        }
        if (entity instanceof AdvancedHitboxEntity || entity instanceof Player) {
            return new AdvancedEntityHitResult(entity, name, d, hitPosition);
        }
        else {
            return new EntityHitResult(entity);
        }
    }
}
