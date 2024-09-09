package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.phys.AdvancedEntityHitResult;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {

    @Inject(method = "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;", at = @At("HEAD"), cancellable = true)
    private static void inject(Level level, Entity projectile, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, float inflationAmount, CallbackInfoReturnable<EntityHitResult> cir) {
        double d = Double.MAX_VALUE;
        Entity entity = null;
        String name = null;
        for (Entity entity2 : level.getEntities(projectile, boundingBox, filter)) {
            if (entity2 instanceof AdvancedHitboxEntity || entity2 instanceof Player) {
                for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity2).getHitboxes()) {
                    double e;
                    Optional<Vec3> optional = hitbox.Linetest(startVec, endVec);
                    if (optional.isEmpty() || !((e = startVec.distanceToSqr(optional.get())) < d)) continue;
                    entity = entity2;
                    d = e;
                    name = hitbox.name;
                }
            }
            else {
                double e;
                AABB aABB = entity2.getBoundingBox().inflate(inflationAmount);
                Optional<Vec3> optional = aABB.clip(startVec, endVec);
                if (optional.isEmpty() || !((e = startVec.distanceToSqr(optional.get())) < d)) continue;
                entity = entity2;
                d = e;
            }
        }

        if (entity == null) {
            cir.setReturnValue(null);
            return;
        }
        if (entity instanceof AdvancedHitboxEntity || entity instanceof Player) {
            cir.setReturnValue(new AdvancedEntityHitResult(entity, name));
        }
        else {
            cir.setReturnValue(new EntityHitResult(entity));
        }
    }
}
