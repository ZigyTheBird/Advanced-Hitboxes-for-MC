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

package com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.random;

import org.jetbrains.annotations.Nullable;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathValue;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.MathFunction;

import java.util.Random;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns a random value based on the input values:
 * <ul>
 *     <li>A single input generates a value between 0 and that input (exclusive)</li>
 *     <li>Two inputs generates a random value between the first (inclusive) and second input (exclusive)</li>
 *     <li>Three inputs generates a random value between the first (inclusive) and second input (exclusive), seeded by the third input</li>
 * </ul>
 */
public final class RandomFunction extends MathFunction {
    private final MathValue valueA;
    @Nullable
    private final MathValue valueB;
    @Nullable
    private final MathValue seed;
    @Nullable
    private final Random random;

    public RandomFunction(MathValue... values) {
        super(values);

        this.valueA = values[0];
        this.valueB = values.length >= 2 ? values[1] : null;
        this.seed = values.length >= 3 ? values[2] : null;
        this.random = this.seed != null ? new Random() : null;
    }

    @Override
    public String getName() {
        return "math.random";
    }

    @Override
    public double compute() {
        double result;
        double valueA = this.valueA.get();

        if (this.random != null) {
            this.random.setSeed((long)this.seed.get());

            result = this.random.nextDouble();
        }
        else {
            result = Math.random();
        }

        if (this.valueB != null) {
            double valueB = this.valueB.get();
            double min = Math.min(valueA, valueB);
            double max = Math.max(valueA, valueB);

            result = min + result * (max - min);
        }
        else {
            result = result * valueA;
        }

        return result;
    }

    @Override
    public boolean isMutable(MathValue... values) {
        if (values.length < 3)
            return true;

        return super.isMutable(values);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public MathValue[] getArgs() {
        if (this.seed != null)
            return new MathValue[] {this.valueA, this.valueB, this.seed};

        if (this.valueB != null)
            return new MathValue[] {this.valueA, this.valueB};

        return new MathValue[] {this.valueA};
    }
}
