package com.zigythebird.advanced_hitboxes.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import com.zigythebird.advanced_hitboxes.utils.HitboxUtils;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class AdvancedHitboxRenderer {
    public static void renderAdvancedHitboxes(PoseStack poseStack, VertexConsumer buffer, Entity entity, double camX, double camY, double camZ) {
        if (entity instanceof AdvancedHitboxEntity || entity instanceof Player) {
            HitboxUtils.tickAndUpdateHitboxesForEntity((AdvancedHitboxEntity) entity);

            PoseStack poseStack1 = new PoseStack();
            for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity).getHitboxes()) {
                if (hitbox instanceof OBB) {
                    poseStack1.pushPose();
                    OBB obb = (OBB) hitbox;
                    renderOBB(obb, poseStack1, buffer, camX, camY, camZ);
                    poseStack1.popPose();
                }
                else if (hitbox instanceof AABB aabb) {
                    LevelRenderer.renderLineBox(poseStack, buffer, aabb.move(-entity.getX(), -entity.getY(), -entity.getZ()), 1.0F, 0.0F, 0.0F, 1.0F);
                }
            }
        }
    }

    public static void renderOBB(OBB obb, PoseStack poseStack, VertexConsumer consumer, double camX, double camY, double camZ) {
        List<Vec3> vertices1 = new ArrayList<>();

        poseStack.mulPose(new Matrix4f(obb.orientation));
        Matrix3f normalisedPoseState = poseStack.last().normal();

        for (Vec3 vec3 : obb.vertices) {
            vertices1.add(vec3.subtract(camX, camY, camZ));
        }

        Vec3[] obbVertices = vertices1.toArray(new Vec3[8]);

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
                consumer.addVertex((float) vertex.x, (float) vertex.y, (float) vertex.z).setColor(1.0F, 0.0F, 0.0f, 1.0f).setNormal(normal.x, normal.y, normal.z);
            }
        }
    }
}
