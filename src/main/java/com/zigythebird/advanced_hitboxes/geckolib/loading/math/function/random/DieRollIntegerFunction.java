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

import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathValue;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.MathFunction;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns a random integer value based on the input values:
 * <ul>
 *     <li>Three inputs generates the sum of <i>n</i> (first input) random values between the second (inclusive) and third input (inclusive)</li>
 *     <li>Four inputs generates the sum of <i>n</i> (first input) random values between the second (inclusive) and third input (inclusive), seeded by the fourth input</li>
 * </ul>
 */
public final class DieRollIntegerFunction extends MathFunction {
    private final MathValue rolls;
    private final MathValue min;
    private final MathValue max;
    @Nullable
    private final MathValue seed;
    @Nullable
    private final Random random;

    public DieRollIntegerFunction(MathValue... values) {
        super(values);

        this.rolls = values[0];
        this.min = values[1];
        this.max = values[2];
        this.seed = values.length >= 4 ? values[3] : null;
        this.random = this.seed != null ? new Random() : null;
    }

    @Override
    public String getName() {
        return "math.die_roll";
    }

    @Override
    public double compute() {
        final int rolls = (int)(Math.floor(this.rolls.get()));
        final int min = Mth.floor(this.min.get());
        final int max = Mth.ceil(this.max.get());
        int sum = 0;
        Random random;

        if (this.random != null) {
            random = this.random;
            random.setSeed((long)this.seed.get());
        }
        else {
            random = ThreadLocalRandom.current();
        }

        for (int i = 0; i < rolls; i++) {
            sum += min + random.nextInt(max + 1 - min);
        }

        return sum;
    }

    @Override
    public boolean isMutable(MathValue... values) {
        if (values.length < 4)
            return true;

        return super.isMutable(values);
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public MathValue[] getArgs() {
        if (this.seed != null)
            return new MathValue[] {this.rolls, this.min, this.max, this.seed};

        return new MathValue[] {this.rolls, this.min, this.max};
    }
}
