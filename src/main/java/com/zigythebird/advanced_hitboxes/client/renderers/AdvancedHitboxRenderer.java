package com.zigythebird.advanced_hitboxes.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class AdvancedHitboxRenderer {
    public static void renderAdvancedHitboxes(PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha) {
        if (entity instanceof AdvancedHitboxEntity || entity instanceof Player) {
            poseStack.pushPose();
            double d0 = Mth.lerp(red, entity.xOld, entity.getX());
            double d1 = Mth.lerp(red, entity.yOld, entity.getY());
            double d2 = Mth.lerp(red, entity.zOld, entity.getZ());
            poseStack.translate(-d0, -d1, -d2);
            for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity).getHitboxes()) {
                if (hitbox instanceof OBB) {
                    poseStack.pushPose();
                    OBB obb = (OBB) hitbox;
                    poseStack.translate(obb.center.x, obb.center.y, obb.center.z);
                    renderOBB(new OBB(obb.name, Vec3.ZERO, null, obb.size, new Vector3d(0, 0, 0), false), poseStack, buffer, red, green, blue, alpha);
                    poseStack.popPose();
                }
            }
            poseStack.popPose();
        }
    }

    public static void renderOBB(OBB obb, PoseStack poseStack, VertexConsumer consumer, float red, float green, float blue, float alpha) {
        PoseStack.Pose posestack$pose = poseStack.last();

        Vec3 scaledAxisX = obb.axisX.scale(obb.extent.x);
        Vec3 scaledAxisY = obb.axisY.scale(obb.extent.y);
        Vec3 scaledAxisZ = obb.axisZ.scale(obb.extent.z);
        Vec3 vertex1 = obb.center.subtract(scaledAxisZ).subtract(scaledAxisX).subtract(scaledAxisY);
        Vec3 vertex2 = obb.center.add(scaledAxisZ).add(scaledAxisX).add(scaledAxisY);

        float f = (float) vertex1.x;
        float f1 = (float) vertex1.y;
        float f2 = (float) vertex1.z;
        float f3 = (float) vertex2.x;
        float f4 = (float) vertex2.y;
        float f5 = (float) vertex2.z;

        consumer.addVertex(posestack$pose, f, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        consumer.addVertex(posestack$pose, f, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        consumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, -1.0F, 0.0F, 0.0F);
        consumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, -1.0F, 0.0F, 0.0F);
        consumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, -1.0F);
        consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, -1.0F);
        consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
    }
}
