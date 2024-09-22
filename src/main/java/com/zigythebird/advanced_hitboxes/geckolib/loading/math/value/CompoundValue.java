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

package com.zigythebird.advanced_hitboxes.geckolib.loading.math.value;

import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathValue;

import java.util.StringJoiner;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Contains a collection of sub-expressions that evaluate before returning the last expression, or 0 if no return is defined.
 * Sub-expressions have no bearing on the final return with exception for where they may be setting variable values
 */
public record CompoundValue(MathValue[] subValues) implements MathValue {
    @Override
    public double get() {
        for (int i = 0; i < this.subValues.length - 1; i++) {
            this.subValues[i].get();
        }

        return this.subValues[this.subValues.length - 1].get();
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner("; ");

        for (MathValue subValue : this.subValues) {
            joiner.add(subValue.toString());
        }

        return joiner.toString();
    }
}
