package com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.generic;

import com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.MathFunction;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathValue;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the absolute (non-negative) equivalent of the input value
 */
public final class AbsFunction extends MathFunction {
    private final MathValue value;

    public AbsFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.abs";
    }

    @Override
    public double compute() {
        return Math.abs(this.value.get());
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.value};
    }
}
