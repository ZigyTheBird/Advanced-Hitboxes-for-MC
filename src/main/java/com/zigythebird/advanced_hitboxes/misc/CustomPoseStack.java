package com.zigythebird.advanced_hitboxes.misc;

import com.google.common.collect.Queues;
import net.minecraft.Util;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Deque;

public class CustomPoseStack {
    private final Deque ServerSidePoseStack = Util.make(Queues.newArrayDeque(), (p_85848_) -> {
        p_85848_.add(new Pose(new Matrix4f(), new Vector3f(1, 1, 1)));
    });

    public CustomPoseStack() {}

    public void translate(double x, double y, double z) {
        this.translate((float)x, (float)y, (float)z);
    }

    public void translate(float x, float y, float z) {
        CustomPoseStack.Pose ServerSidePoseStack$pose = (CustomPoseStack.Pose)this.ServerSidePoseStack.getLast();
        ServerSidePoseStack$pose.pose.translate(x, y, z);
    }

    public void scale(float x, float y, float z) {
        CustomPoseStack.Pose ServerSidePoseStack$pose = (CustomPoseStack.Pose)this.ServerSidePoseStack.getLast();
        ServerSidePoseStack$pose.pose.scale(x, y, z);
        ServerSidePoseStack$pose.size.mul(x, y, z);
    }

    public void mulPose(Quaternionf quaternion) {
        CustomPoseStack.Pose ServerSidePoseStack$pose = (CustomPoseStack.Pose)this.ServerSidePoseStack.getLast();
        ServerSidePoseStack$pose.pose.rotate(quaternion);
    }

    public void rotateAround(Quaternionf quaternion, float x, float y, float z) {
        CustomPoseStack.Pose ServerSidePoseStack$pose = (CustomPoseStack.Pose)this.ServerSidePoseStack.getLast();
        ServerSidePoseStack$pose.pose.rotateAround(quaternion, x, y, z);
    }

    public void pushPose() {
        this.ServerSidePoseStack.addLast(new CustomPoseStack.Pose((CustomPoseStack.Pose)this.ServerSidePoseStack.getLast()));
    }

    public void popPose() {
        this.ServerSidePoseStack.removeLast();
    }

    public CustomPoseStack.Pose last() {
        return (CustomPoseStack.Pose)this.ServerSidePoseStack.getLast();
    }

    public boolean clear() {
        return this.ServerSidePoseStack.size() == 1;
    }

    public void mulPose(Matrix4f pose) {
        CustomPoseStack.Pose ServerSidePoseStack$pose = (CustomPoseStack.Pose)this.ServerSidePoseStack.getLast();
        ServerSidePoseStack$pose.pose.mul(pose);
    }

    public static final class Pose {
        final Matrix4f pose;
        final Vector3f size;

        Pose(Matrix4f pose, Vector3f size) {
            this.pose = pose;
            this.size = size;
        }

        Pose(CustomPoseStack.Pose pose) {
            this.pose = new Matrix4f(pose.pose);
            this.size = new Vector3f(pose.size);
        }

        public Matrix4f pose() {
            return this.pose;
        }

        public Vector3f size() {
            return this.size;
        }

        public CustomPoseStack.Pose copy() {
            return new CustomPoseStack.Pose(this);
        }
    }
}
