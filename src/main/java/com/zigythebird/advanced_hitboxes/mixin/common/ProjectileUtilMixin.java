package com.zigythebird.advanced_hitboxes.mixin.common;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.zigythebird.advanced_hitboxes.interfaces.IAdvancedHitboxSensitiveProjectile;
import com.zigythebird.advanced_hitboxes.utils.AdvancedRaytraceUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
    @WrapMethod(method = "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;")
    private static EntityHitResult inject(Level level, Entity projectile, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, float inflationAmount, Operation<EntityHitResult> original) {
        // The reason why I inflate the bounding box is that some advanced hitboxes might stick out of the regular hitbox and the entity won't be detected.
        // Though the arrow code does already inflate the hitbox, so this might be overkill, but it doesn't hurt to be safe.
        return projectile instanceof IAdvancedHitboxSensitiveProjectile ?
                AdvancedRaytraceUtils.raytrace(level, startVec, endVec, boundingBox.inflate(1), filter)
                : original.call(level, projectile, startVec, endVec, boundingBox, filter, inflationAmount);
    }
}
