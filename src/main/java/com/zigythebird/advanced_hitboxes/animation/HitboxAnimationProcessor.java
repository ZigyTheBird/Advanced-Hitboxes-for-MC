package com.zigythebird.advanced_hitboxes.animation;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.AnimationProcessor;
import com.zigythebird.playeranimcore.animation.layered.AnimationStack;

public class HitboxAnimationProcessor extends AnimationProcessor {
    /**
     * Each AnimationProcessor must be bound to a player
     */
    public HitboxAnimationProcessor() {
        super();
    }

    @Override
    public void handleAnimations(float v, boolean b) {}

    @Override
    public void tickAnimation(AnimationStack stack, AnimationData state) {
        super.tickAnimation(stack, state);

        if (stack instanceof HitboxAnimManager hitboxAnimManager) {
            hitboxAnimManager.finishFirstTick();
        }
    }
}
