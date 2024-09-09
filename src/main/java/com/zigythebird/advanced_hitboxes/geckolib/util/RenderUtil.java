package com.zigythebird.advanced_hitboxes.geckolib.util;

import com.mojang.blaze3d.Blaze3D;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RenderUtil {
    /**
     * Returns the current time (in ticks) that the {@link org.lwjgl.glfw.GLFW GLFW} instance has been running
     * <p>
     * This is effectively a permanent timer that counts up since the game was launched.
     */
    public static double getCurrentTick() {
        return Blaze3D.getTime() * 20d;
    }

    /**
     * Special helper function for lerping yaw.
     * <p>
     * This exists because yaw in Minecraft handles its yaw a bit strangely, and can cause incorrect results if lerped without accounting for special-cases
     */
    public static double lerpYaw(double delta, double start, double end) {
        start = Mth.wrapDegrees(start);
        end = Mth.wrapDegrees(end);
        double diff = start - end;
        end = diff > 180 || diff < -180 ? start + Math.copySign(360 - Math.abs(diff), diff) : end;

        return Mth.lerp(delta, start, end);
    }

    /**
     * Converts a given double array to its {@link Vec3} equivalent
     */
    public static Vec3 arrayToVec(double[] array) {
        return new Vec3(array[0], array[1], array[2]);
    }
}
