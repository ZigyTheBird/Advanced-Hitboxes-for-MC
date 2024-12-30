package com.zigythebird.advanced_hitboxes.utils;

import com.zigythebird.advanced_hitboxes.client.utils.ClientUtils;
import com.zigythebird.advanced_hitboxes.geckolib.animation.AnimationState;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.GeoCube;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxGeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.constant.DataTickets;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.model.data.EntityModelData;
import com.zigythebird.advanced_hitboxes.interfaces.AABBInterface;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.interfaces.LivingEntityInterface;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class HitboxUtils {
    public static <T extends Entity & AdvancedHitboxEntity> void tick(Entity animatable) {
        if ((animatable instanceof AdvancedHitboxEntity || animatable instanceof Player) &&
                (!animatable.level().isClientSide || !ClientUtils.hasSinglePlayerServer() || animatable instanceof Player)) {
            LivingEntity livingEntity = animatable instanceof LivingEntity entity ? entity : null;
            boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null);
            float tickDelta = CommonSideUtils.getTickDelta(animatable.level());
            float lerpBodyRot = livingEntity == null ? 0 : Mth.rotLerp(tickDelta, ((LivingEntityInterface) livingEntity).advanced_Hitboxes$commonYBodyRot0(), ((LivingEntityInterface) livingEntity).advanced_Hitboxes$commonYBodyRot());
            float lerpHeadRot = livingEntity == null ? 0 : Mth.rotLerp(tickDelta, livingEntity.yHeadRotO, livingEntity.yHeadRot);
            float netHeadYaw = lerpHeadRot - lerpBodyRot;

            float limbSwingAmount = 0;
            float limbSwing = 0;

            float headPitch = animatable.getXRot();
            float motionThreshold = 0.015f;
            Vec3 velocity = animatable.getDeltaMovement();
            float avgVelocity = (float) (Math.abs(velocity.x) + Math.abs(velocity.z) / 2f);
            AnimationState<T> animationState = new AnimationState<>(((T) animatable), limbSwing, limbSwingAmount, tickDelta, avgVelocity >= motionThreshold && limbSwingAmount != 0);
            long instanceId = animatable.getId();
            HitboxModel<T> currentModel = ((AdvancedHitboxEntity) animatable).getHitboxModel();

            animationState.setData(DataTickets.TICK, ((AdvancedHitboxEntity) animatable).advanced_hitboxes$getTick(animatable));
            animationState.setData(DataTickets.ENTITY, animatable);
            animationState.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(shouldSit, animatable instanceof LivingEntity && ((LivingEntity) animatable).isBaby(), -netHeadYaw, -headPitch));
            currentModel.addAdditionalStateData(((T) animatable), instanceId, animationState::setData);
            currentModel.handleAnimations(((T) animatable), instanceId, animationState, tickDelta);
            if (((AdvancedHitboxEntity) animatable).useAdvancedHitboxesForCollision()) HitboxUtils.updateOrMakeHitboxesForEntity((AdvancedHitboxEntity) animatable);
        }
    }

    public static void tickAndUpdateHitboxesForEntity(AdvancedHitboxEntity animatable) {
        tick((Entity) animatable);
        if (!animatable.useAdvancedHitboxesForCollision()) updateOrMakeHitboxesForEntity(animatable);
    }

    public static void updateOrMakeHitboxesForEntity(AdvancedHitboxEntity animatable) {
        Entity entity = (Entity) animatable;
        List<AdvancedHitbox> hitboxes = animatable.getHitboxes();
        HitboxModel hitboxModel = animatable.getHitboxModel();

        BakedHitboxModel bakedModel = hitboxModel.getBakedModel(hitboxModel.getModelResource(animatable));
        List<HitboxGeoBone> hitboxBones = new ArrayList<>();
        for (HitboxGeoBone bone : bakedModel.topLevelBones()) {
            if (bone.getName().endsWith("_hitbox")) {
                hitboxBones.add(bone);
            }
            findHitboxBones(bone, hitboxBones);
        }

        for (HitboxGeoBone bone : hitboxBones) {
            AdvancedHitbox hitbox = null;

            for (AdvancedHitbox entry : animatable.getHitboxes()) {
                if (entry.getName().equals(bone.getName())) {
                    hitbox = entry;
                    break;
                }
            }

            GeoCube cube = bone.getCubes().get(0);
            Vector3d position = new Vector3d(cube.origin().x, cube.origin().y, cube.origin().z);
            Vector3d size = bone.getScaleVector().mul(cube.size().x, cube.size().y, cube.size().z);
            Vector3d rotation = new Vector3d();
            Vector3d boneRotation = bone.getRotationVector();
            getSizeFromBoneParents(bone, size);
            position.add(size.x / 32, size.y / 32, size.z / 32);
            ModMath.rotateAroundPivot(rotation, new Vector3d(cube.rotation().x, cube.rotation().y, cube.rotation().z), position, (float) cube.pivot().x / 16, (float) cube.pivot().y / 16, (float) cube.pivot().z / 16);
            position.add(bone.getPositionVector());
            ModMath.rotateAroundPivot(rotation, boneRotation, position, bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
            applyParentBoneTransforms(bone, position, rotation);
            rotation = rotation.set(ModMath.clampToRadian(rotation.x), ModMath.clampToRadian(rotation.y), ModMath.clampToRadian(rotation.z));
            Level level = ((Entity) animatable).level();
            float tickDelta = CommonSideUtils.getTickDelta(level);
            float lerpedBodyRot = entity instanceof LivingEntity ? Mth.rotLerp(tickDelta, ((LivingEntityInterface) entity).advanced_Hitboxes$commonYBodyRot0(), ((LivingEntityInterface) entity).advanced_Hitboxes$commonYBodyRot()) : 0;
            Vec3 finalPosition = ModMath.moveInLocalSpace(new Vec3(position.x, position.y, position.z), 0, lerpedBodyRot);
            double d0 = Mth.lerp(tickDelta, entity.xOld, entity.getX());
            double d1 = Mth.lerp(tickDelta, entity.yOld, entity.getY());
            double d2 = Mth.lerp(tickDelta, entity.zOld, entity.getZ());
            finalPosition = finalPosition.add(d0, d1, d2);

            if (bone.hitboxType == null) {
                if (hitbox == null) {
                    hitboxes.add(new OBB(bone.getName(), finalPosition, ModMath.vector3dToVec3(size.div(16)), rotation));
                }
                else if (hitbox instanceof OBB obb) {
                    obb.update(finalPosition, ModMath.vector3dToVec3(size.div(16)), rotation);
                }
                else {
                    hitboxes.remove(hitbox);
                    hitboxes.add(new OBB(bone.getName(), finalPosition, ModMath.vector3dToVec3(size.div(16)), rotation));
                }
            }
            else if (bone.hitboxType.equalsIgnoreCase("aabb")) {
                finalPosition = finalPosition.add(d0, d1, d2);
                Vec3 size1 = ModMath.vector3dToVec3(size.div(32));
                AABB aabb = new AABB(finalPosition.subtract(size1), finalPosition.add(size1));
                ((AABBInterface)aabb).setName(bone.getName());

                if (hitbox != null) {
                    hitboxes.remove(hitbox);
                }
                hitboxes.add((AdvancedHitbox) aabb);
            }
        }
    }

    public static void findHitboxBones(HitboxGeoBone bone, List<HitboxGeoBone> list) {
        for (HitboxGeoBone child : bone.getChildBones()) {
            if (child.getName().endsWith("_hitbox")) {
                list.add(child);
            }
            findHitboxBones(child, list);
        }
    }

    public static void getSizeFromBoneParents(HitboxGeoBone bone, Vector3d size) {
        HitboxGeoBone parent = bone.getParent();
        if (parent != null) {
            size.mul(parent.getScaleVector());
            getSizeFromBoneParents(parent, size);
        }
    }

    public static void applyParentBoneTransforms(HitboxGeoBone bone, Vector3d position, Vector3d rotation) {
        HitboxGeoBone parent = bone.getParent();
        if (parent != null) {
            ModMath.rotateAroundPivot(rotation, parent.getRotationVector(), position, parent.getPivotX() / 16, parent.getPivotY() / 16, parent.getPivotZ() / 16);
            position.add(parent.getPositionVector());
            applyParentBoneTransforms(parent, position, rotation);
        }
    }

    public static Vec3 collideWithShapes(Vec3 deltaMovement, OBB entityOBB, List<VoxelShape> shapes) {
        if (shapes.isEmpty()) {
            return deltaMovement;
        } else {
            double d0 = Math.abs(deltaMovement.x) > 1.0E-7 ? deltaMovement.x : 0;
            double d1 = Math.abs(deltaMovement.y) > 1.0E-7 ? deltaMovement.y : 0;
            double d2 = Math.abs(deltaMovement.z) > 1.0E-7 ? deltaMovement.z : 0;

            if (d0 == 0 && d1 == 0 && d2 == 0) {
                return Vec3.ZERO;
            }

            entityOBB = new OBB(entityOBB);
            entityOBB.inflate(-1.0E-7);

            entityOBB.offset(d0, d1, d2);
            for (VoxelShape shape : shapes) {
                for (AABB aabb : shape.toAabbs()) {
                    OBB.OBBIntersectResult result = OBB.intersects(entityOBB, new OBB(null, aabb));
                    if (result.intersects()) {
                        Vector3d offset = result.axis().mul(result.length());
                        Vec3 aabbCentre = aabb.getCenter();
                        Vec3 pos1 = new Vec3(entityOBB.center.x + offset.x, entityOBB.center.y + offset.y, entityOBB.center.z + offset.z);
                        Vec3 pos2 = new Vec3(entityOBB.center.x - offset.x, entityOBB.center.y - offset.y, entityOBB.center.z - offset.z);
                        if (pos2.distanceTo(aabbCentre) > pos1.distanceTo(aabbCentre)) {
                            offset.mul(-1);
                        }
                        entityOBB.offset(offset.x, offset.y, offset.z);
                        d0 += offset.x();
                        d1 += offset.y();
                        d2 += offset.z();
                    }

                    if (Math.abs(d0) < 1.0E-7) d0 = 0;
                    if (Math.abs(d1) < 1.0E-7) d1 = 0;
                    if (Math.abs(d2) < 1.0E-7) d2 = 0;

                    if (d0 == 0 && d1 == 0 && d2 == 0) break;
                }
                if (d0 == 0 && d1 == 0 && d2 == 0) break;
            }

            return new Vec3(d0, d1, d2);
        }
    }
}
