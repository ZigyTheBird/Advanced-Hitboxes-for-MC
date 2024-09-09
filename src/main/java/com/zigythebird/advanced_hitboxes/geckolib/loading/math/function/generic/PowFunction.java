package com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.generic;

import com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.MathFunction;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathValue;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the input value raised to the power of the second input value
 */
public final class PowFunction extends MathFunction {
    private final MathValue value;
    private final MathValue power;

    public PowFunction(MathValue... values) {
        super(values);

        this.value = values[0];
        this.power = values[1];
    }

    @Override
    public String getName() {
        return "math.pow";
    }

    @Override
    public double compute() {
        return Math.pow(this.value.get(), this.power.get());
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.value, this.power};
    }
}
