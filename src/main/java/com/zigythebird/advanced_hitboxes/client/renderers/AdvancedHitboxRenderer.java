package com.zigythebird.advanced_hitboxes.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zigythebird.advanced_hitboxes.interfaces.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import com.zigythebird.advanced_hitboxes.utils.HitboxUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class AdvancedHitboxRenderer {
    public static final Vec3 axisX = new Vec3(1, 0, 0);
    public static final Vec3 axisY = new Vec3(0, 1, 0);
    public static final Vec3 axisZ = new Vec3(0, 0, 1);

    public static void renderAdvancedHitboxes(VertexConsumer buffer, Entity entity, double camX, double camY, double camZ) {
        if (entity instanceof AdvancedHitboxEntity || entity instanceof Player) {
            if (!((AdvancedHitboxEntity)entity).useAdvancedHitboxesForCollision()) {
                HitboxUtils.tickAndUpdateHitboxesForEntity((AdvancedHitboxEntity) entity);
            }
            PoseStack poseStack = new PoseStack();
            for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity).getHitboxes()) {
                if (hitbox instanceof OBB) {
                    poseStack.pushPose();
                    OBB obb = (OBB) hitbox;
                    poseStack.translate(obb.center.x - camX, obb.center.y - camY, obb.center.z - camZ);
                    renderOBB(new OBB(obb.getName(), Vec3.ZERO, obb.size, obb.rotation), poseStack, buffer);
                    poseStack.popPose();
                }
            }
        }
    }

    public static void renderOBB(OBB obb, PoseStack poseStack, VertexConsumer consumer) {
        Vector3d rotation = obb.rotation;

        poseStack.mulPose(Axis.ZP.rotation((float) rotation.z));
        poseStack.mulPose(Axis.XP.rotation((float) rotation.x));
        poseStack.mulPose(Axis.YP.rotation((float) rotation.y));

        Matrix3f normalisedPoseState = poseStack.last().normal();
        Matrix4f poseState = new Matrix4f(poseStack.last().pose());

        //Todo: Remove this and use the update vertex method.
        Vec3 scaledAxisX = axisX.scale(obb.extent.x);
        Vec3 scaledAxisY = axisY.scale(obb.extent.y);
        Vec3 scaledAxisZ = axisZ.scale(obb.extent.z);
        Vec3 vertex1 = obb.center.subtract(scaledAxisZ).subtract(scaledAxisX).subtract(scaledAxisY); //bottom left back
        Vec3 vertex2 = obb.center.subtract(scaledAxisZ).add(scaledAxisX).subtract(scaledAxisY); //bottom right back
        Vec3 vertex3 = obb.center.subtract(scaledAxisZ).add(scaledAxisX).add(scaledAxisY); //top right back
        Vec3 vertex4 = obb.center.subtract(scaledAxisZ).subtract(scaledAxisX).add(scaledAxisY); //top left back
        Vec3 vertex5 = obb.center.add(scaledAxisZ).subtract(scaledAxisX).subtract(scaledAxisY); //bottom left front
        Vec3 vertex6 = obb.center.add(scaledAxisZ).add(scaledAxisX).subtract(scaledAxisY); //bottom right front
        Vec3 vertex7 = obb.center.add(scaledAxisZ).add(scaledAxisX).add(scaledAxisY); //top right front
        Vec3 vertex8 = obb.center.add(scaledAxisZ).subtract(scaledAxisX).add(scaledAxisY); //top left front
        Vec3[] obbVertices = new Vec3[]{vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7, vertex8};

        for (int i = 0; i < 12; i++) {
            Vec3[] vertices;
            Vector3f normal1;

            switch (i) {
                case 0 -> {
                    normal1 = Direction.WEST.step();
                    vertices = new Vec3[]{obbVertices[2], obbVertices[3], obbVertices[0], obbVertices[1]};
                }
                case 1 -> {
                    normal1 = Direction.EAST.step();
                    vertices = new Vec3[]{obbVertices[7], obbVertices[6], obbVertices[5], obbVertices[4]};
                }
                case 2 -> {
                    normal1 = Direction.NORTH.step();
                    vertices = new Vec3[]{obbVertices[3], obbVertices[7], obbVertices[4], obbVertices[0]};
                }
                case 3 -> {
                    normal1 = Direction.SOUTH.step();
                    vertices = new Vec3[]{obbVertices[6], obbVertices[2], obbVertices[1], obbVertices[5]};
                }
                case 4 -> {
                    normal1 = Direction.UP.step();
                    vertices = new Vec3[]{obbVertices[2], obbVertices[6], obbVertices[7], obbVertices[3]};
                }
                case 5 -> {
                    normal1 = Direction.DOWN.step();
                    vertices = new Vec3[]{obbVertices[0], obbVertices[4], obbVertices[5], obbVertices[1]};
                }
                case 6 -> {
                    normal1 = Direction.WEST.step();
                    vertices = new Vec3[]{obbVertices[0], obbVertices[3], obbVertices[1], obbVertices[2]};
                }
                case 7 -> {
                    normal1 = Direction.EAST.step();
                    vertices = new Vec3[]{obbVertices[5], obbVertices[6], obbVertices[4], obbVertices[7]};
                }
                case 8 -> {
                    normal1 = Direction.NORTH.step();
                    vertices = new Vec3[]{obbVertices[0], obbVertices[3], obbVertices[4], obbVertices[7]};
                }
                case 9 -> {
                    normal1 = Direction.SOUTH.step();
                    vertices = new Vec3[]{obbVertices[1], obbVertices[2], obbVertices[5], obbVertices[6]};
                }
                case 10 -> {
                    normal1 = Direction.UP.step();
                    vertices = new Vec3[]{obbVertices[2], obbVertices[3], obbVertices[6], obbVertices[7]};
                }
                default -> {
                    normal1 = Direction.DOWN.step();
                    vertices = new Vec3[]{obbVertices[0], obbVertices[1], obbVertices[4], obbVertices[5]};
                }
            }

            for (Vec3 vertex : vertices) {
                Vector3f normal = normalisedPoseState.transform(normal1);
                consumer.addVertex(poseState, (float) vertex.x, (float) vertex.y, (float) vertex.z).setColor(1.0F, 0.0F, 0.0f, 1.0f).setNormal(normal.x, normal.y, normal.z);
            }
        }
    }
}
