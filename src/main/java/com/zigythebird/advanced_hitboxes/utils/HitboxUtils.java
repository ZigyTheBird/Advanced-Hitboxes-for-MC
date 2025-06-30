package com.zigythebird.advanced_hitboxes.utils;

import com.mojang.math.Axis;
import com.zigythebird.advanced_hitboxes.accessor.AABBAccessor;
import com.zigythebird.advanced_hitboxes.client.utils.ClientUtils;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.AnimationState;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.GeoCube;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxGeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.constant.DataTickets;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.model.data.EntityModelData;
import com.zigythebird.advanced_hitboxes.misc.CustomPoseStack;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import com.zigythebird.playeranimatorapi.ModInit;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class HitboxUtils {
    public static <T extends Entity & AdvancedHitboxEntity> void tick(Entity animatable) {
        if ((animatable instanceof AdvancedHitboxEntity || animatable instanceof Player) &&
                (!animatable.level().isClientSide || !ClientUtils.hasSinglePlayerServer() || animatable instanceof Player)) {
            LivingEntity livingEntity = animatable instanceof LivingEntity entity ? entity : null;
            boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null);
            float tickDelta = CommonSideUtils.getTickDelta(animatable.level());
            float lerpBodyRot = livingEntity == null ? 0 : Mth.rotLerp(tickDelta, CommonSideUtils.getBodyYRot0(livingEntity), CommonSideUtils.getBodyYRot(livingEntity));
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

            BakedHitboxModel bakedModel = currentModel.getBakedModel(currentModel.getModelResource((T)animatable));
            for (HitboxGeoBone bone: bakedModel.topLevelBones()) {
                applyTransformationsToBone((AdvancedHitboxEntity) animatable, bone);
            }
        }
    }

    public static void applyTransformationsToBone(AdvancedHitboxEntity entity, HitboxGeoBone bone) {
        entity.applyTransformationsToBone(bone);
        for (HitboxGeoBone child : bone.getChildBones()) {
            applyTransformationsToBone(entity, child);
        }
    }

    public static void tickAndUpdateHitboxesForEntity(AdvancedHitboxEntity animatable) {
        tick((Entity) animatable);
        updateOrMakeHitboxesForEntity(animatable);
    }

    public static void updateOrMakeHitboxesForEntity(AdvancedHitboxEntity animatable) {
        Entity entity = (Entity) animatable;
        List<AdvancedHitbox> hitboxes = animatable.getHitboxes();
        HitboxModel hitboxModel = animatable.getHitboxModel();
        BakedHitboxModel bakedModel = hitboxModel.getBakedModel(hitboxModel.getModelResource(animatable));
        CustomPoseStack poseStack = new CustomPoseStack();
        float tickDelta = CommonSideUtils.getTickDelta(entity.level());
        double d0 = Mth.lerp(tickDelta, entity.xOld, entity.getX());
        double d1 = Mth.lerp(tickDelta, entity.yOld, entity.getY());
        double d2 = Mth.lerp(tickDelta, entity.zOld, entity.getZ());
        poseStack.translate(d0, d1, d2);

        poseStack.mulPose(Axis.YN.rotationDegrees(180));
        if (animatable instanceof Player)
            poseStack.scale(0.9375F, 0.9375F, 0.9375F);

        float lerpedBodyRot = entity instanceof LivingEntity livingEntity ? Mth.rotLerp(tickDelta, CommonSideUtils.getBodyYRot0(livingEntity), CommonSideUtils.getBodyYRot(livingEntity)) : 0;
        if (lerpedBodyRot != 0) poseStack.mulPose(Axis.YN.rotationDegrees(lerpedBodyRot));

        for (HitboxGeoBone bone : bakedModel.topLevelBones()) {
            processBoneRecursively(entity, hitboxes, poseStack, bone);
        }
        //For debugging purposes
        //if (!poseStack.clear()) throw new RuntimeException("You made an oopsie messing with the posestack handling in the HitboxUtils class!");
    }

    public static void processBoneRecursively(Entity entity, List<AdvancedHitbox> hitboxes,
                                              CustomPoseStack poseStack, HitboxGeoBone bone) {
        poseStack.pushPose();
        ModMath.prepMatrixForBone(poseStack, bone);
        if (bone.getName().endsWith("_hitbox")) {
            if (bone.hitboxType == null) processHitboxBoneOBB(entity, hitboxes, poseStack, bone);
            else if ("aabb".equalsIgnoreCase(bone.hitboxType)) processHitboxBoneAABB(entity, hitboxes, poseStack, bone);
        }
        for (HitboxGeoBone child : bone.getChildBones()) {
            processBoneRecursively(entity, hitboxes, poseStack, child);
        }
        poseStack.popPose();
    }

    public static void processHitboxBoneOBB(Entity entity, List<AdvancedHitbox> hitboxes,
                                            CustomPoseStack poseStack, HitboxGeoBone bone) {
        if (bone.getCubes().isEmpty()) {
            ModInit.LOGGER.debug("Hitbox bone called {} doesn't have any cubes", bone.getName());
            return;
        }
        poseStack.pushPose();
        AdvancedHitbox hitbox = null;

        for (AdvancedHitbox entry : ((AdvancedHitboxEntity)entity).getHitboxes()) {
            if (entry.getName().equals(bone.getName())) {
                hitbox = entry;
                break;
            }
        }

        GeoCube cube = bone.getCubes().get(0);
        ModMath.translateToPivotPoint(poseStack, cube);
        ModMath.rotateMatrixAroundCube(poseStack, cube);
        ModMath.translateAwayFromPivotPoint(poseStack, cube);

        Matrix4f pose = poseStack.last().pose();
        Matrix3f orientation = new Matrix3f();
        pose.get3x3(orientation);
        Vector3f size = poseStack.last().size().mul((float) cube.size().x(), (float) cube.size().y(), (float) cube.size().z()).div(32);
        List<Vec3> vertices = new ArrayList<>();

        for (Vector3d vertex : cube.vertexSet().allVertices()) {
            Vector4f vector4f = pose.transform(new Vector4f((float) vertex.x, (float) vertex.y, (float) vertex.z, 1.0F));
            vertices.add(new Vec3(vector4f.x, vector4f.y, vector4f.z));
        }

        Vec3[] verticesArray = vertices.toArray(new Vec3[8]);

        if (hitbox == null) {
            hitboxes.add(new OBB(bone.getName(), verticesArray, ModMath.vector3fToVec3(size), orientation));
        }
        else if (hitbox instanceof OBB obb) {
            obb.update(verticesArray, ModMath.vector3fToVec3(size), orientation);
        }
        else {
            hitboxes.remove(hitbox);
            hitboxes.add(new OBB(bone.getName(), verticesArray, ModMath.vector3fToVec3(size), orientation));
        }
        poseStack.popPose();
    }

    public static void processHitboxBoneAABB(Entity entity, List<AdvancedHitbox> hitboxes,
                                            CustomPoseStack poseStack, HitboxGeoBone bone) {
        AdvancedHitbox hitbox = null;
        for (AdvancedHitbox entry : ((AdvancedHitboxEntity)entity).getHitboxes()) {
            if (entry.getName().equals(bone.getName())) {
                hitbox = entry;
                break;
            }
        }
        ((AdvancedHitboxEntity)entity).getHitboxes().remove(hitbox);


        GeoCube cube = bone.getCubes().get(0);
        Matrix4f pose = poseStack.last().pose();
        Vec3 position = cube.origin().add(pose.m30(), pose.m31(), pose.m32());
        Vec3 size = cube.size().multiply(ModMath.vector3fToVec3(poseStack.last().size().div(32)));
        AABB aabb = new AABB(-size.x(), -size.y(), -size.z(), size.x(), size.y(), size.z()).move(position);
        ((AABBAccessor)aabb).setName(bone.getName());
        hitboxes.add(((AdvancedHitbox) aabb));
    }

    public static Vec3 collideWithShapes(Vec3 deltaMovement, OBB entityOBB, List<VoxelShape> shapes) {
        if (shapes.isEmpty()) {
            return deltaMovement;
        } else {
            double d0 = Math.abs(deltaMovement.x) > 1.0E-99 ? deltaMovement.x : 0;
            double d1 = Math.abs(deltaMovement.y) > 1.0E-99 ? deltaMovement.y : 0;
            double d2 = Math.abs(deltaMovement.z) > 1.0E-99 ? deltaMovement.z : 0;

            if (d0 == 0 && d1 == 0 && d2 == 0) {
                return Vec3.ZERO;
            }

            entityOBB = new OBB(entityOBB);
            entityOBB.inflate(-1.0E-7);

            entityOBB.offset(d0, d1, d2);
            for (VoxelShape shape : shapes) {
                for (AABB aabb : shape.toAabbs()) {
                    OBB.OBBIntersectResult result = OBB.intersects(entityOBB, new OBB(aabb));
                    if (result.intersects()) {
                        Vector3d offset = result.axis().mul(result.length() <= 1 ? result.length() : 1);
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

//                    if (Math.abs(d0) < 1.0E-7) d0 = 0;
//                    if (Math.abs(d1) < 1.0E-7) d1 = 0;
//                    if (Math.abs(d2) < 1.0E-7) d2 = 0;

                    if (d0 == 0 && d1 == 0 && d2 == 0) break;
                }
                if (d0 == 0 && d1 == 0 && d2 == 0) break;
            }

            return new Vec3(d0, d1, d2);
        }
    }
}
