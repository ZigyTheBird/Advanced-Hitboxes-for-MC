package com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.misc;

import com.zigythebird.advanced_hitboxes.geckolib.loading.math.MathValue;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.function.MathFunction;
import com.zigythebird.advanced_hitboxes.geckolib.loading.math.value.Constant;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns <a href="https://en.wikipedia.org/wiki/Pi">PI</a>
 */
public final class PiFunction extends MathFunction {
    public PiFunction(MathValue... values) {
        super(values);
    }

    @Override
    public String getName() {
        return "math.pi";
    }

    @Override
    public double compute() {
        return Math.PI;
    }

    @Override
    public boolean isMutable(MathValue... values) {
        return false;
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {new Constant(Math.PI)};
    }
}
