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

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns one of two stored values dependent on the result of the stored condition value.
 * This returns such that a non-zero result from the condition will return the <b>true</b> stored value, otherwise returning the <b>false</b> stored value
 */
public record Ternary(MathValue condition, MathValue trueValue, MathValue falseValue) implements MathValue {
    @Override
    public double get() {
        return this.condition.get() != 0 ? this.trueValue.get() : this.falseValue.get();
    }

    @Override
    public boolean isMutable() {
        return this.condition.isMutable() || this.trueValue.isMutable() || this.falseValue.isMutable();
    }

    @Override
    public String toString() {
        return this.condition.toString() + " ? " + this.trueValue.toString() + " : " + this.falseValue.toString();
    }
}
