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

package com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.round;

import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathValue;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.MathFunction;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the <a href="https://en.wikipedia.org/wiki/Hermite_polynomials">Hermite</a>> basis <code>3t^2 - 2t^3</code> curve interpolation value based on the input value
 */
public final class HermiteBlendFunction extends MathFunction {
    private final MathValue valueA;

    public HermiteBlendFunction(MathValue... values) {
        super(values);

        this.valueA = values[0];
    }

    @Override
    public String getName() {
        return "math.hermite_blend";
    }

    @Override
    public double compute() {
        final double value = this.valueA.get();

        return (3 * value * value) - (2 * value * value * value);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.valueA};
    }
}
