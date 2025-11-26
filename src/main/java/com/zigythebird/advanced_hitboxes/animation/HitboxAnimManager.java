package com.zigythebird.advanced_hitboxes.animation;

import com.zigythebird.playeranimcore.animation.layered.AnimationStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * The animation data collection for a given advanced hitbox entity instance
 * <p>
 * Generally speaking, a single working-instance of an advanced hitbox entity will have a single instance of {@code HitboxAnimManager} associated with it
 */
public class HitboxAnimManager extends AnimationStack {
    private float lastUpdateTime;
    private boolean isFirstTick = true;
    private float tickDelta;

    public HitboxAnimManager() {}

    public float getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void updatedAt(float updateTime) {
        this.lastUpdateTime = updateTime;
    }

    public boolean isFirstTick() {
        return this.isFirstTick;
    }

    protected void finishFirstTick() {
        this.isFirstTick = false;
    }

    public float getTickDelta() {
        return this.tickDelta;
    }

    /**
     * If you touch this, you're a horrible person.
     */
    @ApiStatus.Internal
    public void setTickDelta(float tickDelta) {
        this.tickDelta = tickDelta;
    }
}
