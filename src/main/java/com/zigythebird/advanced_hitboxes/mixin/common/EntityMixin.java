package com.zigythebird.advanced_hitboxes.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.zigythebird.advanced_hitboxes.accessor.EntityAccessor;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import com.zigythebird.advanced_hitboxes.utils.HitboxUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor {

    @Shadow
    private static Vec3 collideWithShapes(Vec3 deltaMovement, AABB entityBB, List<VoxelShape> shapes) {
        return null;
    }

    @Shadow
    public static Vec3 collideBoundingBox(@Nullable Entity entity, Vec3 vec, AABB collisionBox, Level level, List<VoxelShape> potentialHits) {
        return null;
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void move(CallbackInfo ci) {
        Entity entity = ((Entity)(Object)this);
        if (entity instanceof AdvancedHitboxEntity advanced && advanced.useAdvancedHitboxesForCollision()) {
            HitboxUtils.tickAndUpdateHitboxesForEntity(advanced);
        }
    }

    @Inject(method = "isColliding", at = @At("HEAD"), cancellable = true)
    private void isColliding(BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = ((Entity)(Object)this);
        if (entity instanceof AdvancedHitboxEntity && ((AdvancedHitboxEntity) entity).useAdvancedHitboxesForCollision()) {
            VoxelShape voxelshape = state.getCollisionShape(entity.level(), pos, CollisionContext.of(entity));
            VoxelShape voxelshape1 = voxelshape.move(pos.getX(), pos.getY(), pos.getZ());
            for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity).getHitboxes()) {
                if (((AdvancedHitboxEntity) entity).useHitboxForCollision(hitbox.getName())) {
                    for (AABB aabb : voxelshape1.toAabbs()) {
                        if (hitbox.intersects(aabb)) {
                            cir.setReturnValue(true);
                            return;
                        }
                    }
                }
            }
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "collide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;collideWithShapes(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 collide(Vec3 d0, AABB d1, List<VoxelShape> d2, @Local(ordinal = 1) Vec3 vec3, @Local(ordinal = 3) boolean flag3) {
        //Todo: Make this actually work and make sure it works.
        Entity entity = ((Entity)(Object)this);
        if (entity instanceof AdvancedHitboxEntity && ((AdvancedHitboxEntity) entity).useAdvancedHitboxesForCollision()) {
            Vec3 result = d0;
            for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity).getHitboxes()) {
                if (((AdvancedHitboxEntity) entity).useHitboxForCollision(hitbox.getName())) {
                    if (hitbox instanceof OBB obb) {
                        if (flag3) {
                            obb = new OBB(obb).offset(0, vec3.y, 0);
                        }
                        result = HitboxUtils.collideWithShapes(result, obb, d2);
                    }
                    else if (hitbox instanceof AABB aabb) {
                        result = collideWithShapes(d0, aabb, d2);
                    }
                }
            }
            return result;
        }
        else {
            return collideWithShapes(d0, d1, d2);
        }
    }

    @Redirect(method = "collide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 collide2(Entity entity, Vec3 vec, AABB collisionBox, Level level, List<VoxelShape> potentialHits) {
        if (entity instanceof AdvancedHitboxEntity && ((AdvancedHitboxEntity) entity).useAdvancedHitboxesForCollision()) {
            List<AABB> aabbs = new ArrayList<>();
            for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity).getHitboxes()) {
                if (((AdvancedHitboxEntity) entity).useHitboxForCollision(hitbox.getName())) {
                    if (hitbox instanceof OBB obb) {
                        //Todo: Seems to work just fine but it doesn't hurt to look into this later.
                        aabbs.add(obb.toAABB().expandTowards(vec));
                    }
                    else if (hitbox instanceof AABB aabb) {
                        aabbs.add(aabb.expandTowards(vec));
                    }
                }
            }

            //Todo: See whether this or the new method is more efficient.
            //List<VoxelShape> list = new ArrayList<>();
            //for (AABB aabb : aabbs) {
                //Todo: Try and avoid duplicates in the list.
                //list.addAll(com.zigythebird.advanced_hitboxes.mixin.accessors.EntityAccessor.callCollectColliders(entity, level, potentialHits, aabb));
            //}

            AABB mainAABB = null;
            for (AABB aabb : aabbs) {
                if (mainAABB == null) mainAABB = aabb;
                else mainAABB = aabb.minmax(mainAABB);
            }
            List<VoxelShape> list = mainAABB == null ? new ArrayList<>() : com.zigythebird.advanced_hitboxes.mixin.accessors.EntityAccessor.callCollectColliders(entity, level, potentialHits, mainAABB);
            Vec3 result = vec;
            boolean checkForZero = true;
            if (vec.equals(Vec3.ZERO)) checkForZero = false;
            for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity).getHitboxes()) {
                if (((AdvancedHitboxEntity) entity).useHitboxForCollision(hitbox.getName())) {
                    if (hitbox instanceof OBB obb) {
                        result = HitboxUtils.collideWithShapes(result, obb, list);
                    }
                    else if (hitbox instanceof AABB aabb) {
                        result = collideWithShapes(result, aabb, list);
                    }

                    if (checkForZero && result.x == 0 && result.y == 0 && result.z == 0) {
                        break;
                    }
                }
            }
            return result;
        }
        else {
            return collideBoundingBox(entity, vec, collisionBox, level, potentialHits);
        }
    }

    @Unique
    private final List<AdvancedHitbox> advanced_hitboxes$hitboxes = new ArrayList<>();

    @Override
    public List<AdvancedHitbox> advanced_Hitboxes$getHitboxes() {
        return advanced_hitboxes$hitboxes;
    }
}
