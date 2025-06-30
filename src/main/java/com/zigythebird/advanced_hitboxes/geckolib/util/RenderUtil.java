/*
 * MIT License
 *
 * Copyright (c) 2024 GeckoThePecko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.zigythebird.advanced_hitboxes.geckolib.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RenderUtil {
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
