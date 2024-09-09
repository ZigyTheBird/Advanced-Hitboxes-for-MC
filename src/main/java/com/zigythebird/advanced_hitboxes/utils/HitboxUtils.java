package com.zigythebird.advanced_hitboxes.utils;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.GeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.GeoCube;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.misc.EntityInterface;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class HitboxUtils {
    public static void updateOrMakeHitboxesForEntity(AdvancedHitboxEntity animatable) {
        Entity entity = (Entity) animatable;
        List<AdvancedHitbox> hitboxes = animatable.getHitboxes();
        HitboxModel hitboxModel = animatable.getHitboxModel();

        BakedHitboxModel bakedModel = hitboxModel.getBakedModel(hitboxModel.getModelResource(animatable));
        List<GeoBone> hitboxBones = new ArrayList<>();
        for (GeoBone bone : bakedModel.topLevelBones()) {
            if (bone.getName().endsWith("_hitbox")) {
                hitboxBones.add(bone);
            }
            findHitboxBones(bone, hitboxBones);
        }

        for (GeoBone bone : hitboxBones) {
            OBB hitbox = null;

            for (AdvancedHitbox entry : animatable.getHitboxes()) {
                if (entry instanceof OBB && entry.name.equals(bone.getName())) {
                    hitbox = (OBB) entry;
                    break;
                }
            }

            GeoCube cube = bone.getCubes().get(0);
            Vector3d position = new Vector3d(cube.origin().x, cube.origin().y, cube.origin().z);
            Vector3d size = bone.getScaleVector().mul(cube.size().x, cube.size().y, cube.size().z);
            Vector3d rotation = new Vector3d(cube.rotation().x, cube.rotation().y, cube.rotation().z);
            Vector3d boneRotation = bone.getRotationVector();
            ModMath.rotateAroundPivot(rotation, position, (float) cube.pivot().x, (float) cube.pivot().y, (float) cube.pivot().z);
            position.add(bone.getPositionVector());
            ModMath.rotateAroundPivot(boneRotation, position, bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
            rotation.add(boneRotation);
            applyParentBoneTransforms(bone, position, size);
            GeoBone parent = bone.getParent();
            while (parent != null) {
                rotation.add(parent.getRotationVector());
                parent = parent.getParent();
            }
            rotation = new Vector3d(Mth.wrapDegrees(rotation.x), Mth.wrapDegrees(rotation.y), Mth.wrapDegrees(rotation.z));
            position.add(size.x/32, size.y/32, size.z/32);
            Vec3 finalPosition = ModMath.moveInLocalSpace(new Vec3(-position.x, position.y, -position.z), 0, ((EntityInterface)entity).advanced_Hitboxes$commonYBodyRot());
            if (hitbox == null) {
                hitboxes.add(new OBB(bone.getName(), entity.position().add(finalPosition), null, ModMath.sizeInPixelsToMetres(size), rotation, false));
            }
            else {
                hitbox.update(entity.position().add(finalPosition), null, ModMath.sizeInPixelsToMetres(size), rotation, false);
            }
        }
    }

    public static void findHitboxBones(GeoBone bone, List<GeoBone> list) {
        for (GeoBone child : bone.getChildBones()) {
            if (child.getName().endsWith("_hitbox")) {
                list.add(child);
            }
            findHitboxBones(child, list);
        }
    }

    public static void applyParentBoneTransforms(GeoBone bone, Vector3d position, Vector3d size) {
        GeoBone parent = bone.getParent();
        if (parent != null) {
            ModMath.rotateAroundPivot(parent.getRotationVector(), position, parent.getPivotX(), parent.getPivotY(), parent.getPivotZ());
            position.add(parent.getPositionVector());
            size.mul(parent.getScaleVector());
            applyParentBoneTransforms(parent, position, size);
        }
    }
}
