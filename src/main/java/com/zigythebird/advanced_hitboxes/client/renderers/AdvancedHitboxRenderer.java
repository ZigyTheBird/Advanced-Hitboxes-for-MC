package com.zigythebird.advanced_hitboxes.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

public class AdvancedHitboxRenderer {
    public static void renderAdvancedHitboxes(VertexConsumer buffer, Entity entity, double camX, double camY, double camZ, float red, float green, float blue, float alpha) {
        if (entity instanceof AdvancedHitboxEntity || entity instanceof Player) {
            PoseStack poseStack = new PoseStack();
            for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity).getHitboxes()) {
                if (hitbox instanceof OBB) {
                    poseStack.pushPose();
                    OBB obb = (OBB) hitbox;
                    poseStack.translate(obb.center.x - camX, obb.center.y - camY, obb.center.z - camZ);
                    renderOBB(new OBB(obb.name, Vec3.ZERO, obb.size, new Vector3d(obb.rotation)), poseStack, buffer, red, green, blue, alpha);
                    poseStack.popPose();
                }
            }
        }
    }

    public static void renderOBB(OBB obb, PoseStack poseStack, VertexConsumer consumer, float red, float green, float blue, float alpha) {
        Vector3d rotation = obb.rotation;

        poseStack.mulPose(new Quaternionf().rotationXYZ(
                (float)rotation.x() * Mth.DEG_TO_RAD,
                (float)rotation.y() * Mth.DEG_TO_RAD,
                (float)rotation.z() * Mth.DEG_TO_RAD));

        Matrix3f normalisedPoseState = poseStack.last().normal();
        Matrix4f poseState = new Matrix4f(poseStack.last().pose());

        obb.calculateOrientation();
        obb.updateVertex();

        Vec3[] obbVertices = obb.vertices;

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
                consumer.addVertex(poseState, (float) vertex.x, (float) vertex.y, (float) vertex.z).setColor(red, green, blue, alpha).setNormal(normal.x, normal.y, normal.z);
            }
        }
    }
}
