package com.zigythebird.advanced_hitboxes.utils;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

public interface ServerDeltaTracker {
    ServerDeltaTracker ZERO = new DefaultValue(0.0F);
    ServerDeltaTracker ONE = new DefaultValue(1.0F);

    float getGameTimeDeltaTicks();

    float getGameTimeDeltaPartialTick(boolean runsNormally);

    float getRealtimeDeltaTicks();

    public static class DefaultValue implements ServerDeltaTracker {
        private final float value;

        DefaultValue(float value) {
            this.value = value;
        }

        @Override
        public float getGameTimeDeltaTicks() {
            return this.value;
        }

        @Override
        public float getGameTimeDeltaPartialTick(boolean runsNormally) {
            return this.value;
        }

        @Override
        public float getRealtimeDeltaTicks() {
            return this.value;
        }
    }

    public static class Timer implements ServerDeltaTracker {
        private float deltaTicks;
        private float deltaTickResidual;
        private float realtimeDeltaTicks;
        private float pausedDeltaTickResidual;
        private long lastMs;
        private long lastUiMs;
        private final float msPerTick;
        private final FloatUnaryOperator targetMsptProvider;
        private boolean paused;
        private boolean frozen;

        public Timer(float ticksPerSecond, long time, FloatUnaryOperator targetMsptProvider) {
            this.msPerTick = 1000.0F / ticksPerSecond;
            this.lastUiMs = this.lastMs = time;
            this.targetMsptProvider = targetMsptProvider;
        }

        public int advanceTime(long time, boolean advanceGameTime) {
            this.advanceRealTime(time);
            return advanceGameTime ? this.advanceGameTime(time) : 0;
        }

        private int advanceGameTime(long time) {
            this.deltaTicks = (float)(time - this.lastMs) / this.targetMsptProvider.apply(this.msPerTick);
            this.lastMs = time;
            this.deltaTickResidual = this.deltaTickResidual + this.deltaTicks;
            int i = (int)this.deltaTickResidual;
            this.deltaTickResidual -= (float)i;
            return i;
        }

        private void advanceRealTime(long time) {
            this.realtimeDeltaTicks = (float)(time - this.lastUiMs) / this.msPerTick;
            this.lastUiMs = time;
        }

        public void updatePauseState(boolean paused) {
            if (paused) {
                this.pause();
            } else {
                this.unPause();
            }
        }

        private void pause() {
            if (!this.paused) {
                this.pausedDeltaTickResidual = this.deltaTickResidual;
            }

            this.paused = true;
        }

        private void unPause() {
            if (this.paused) {
                this.deltaTickResidual = this.pausedDeltaTickResidual;
            }

            this.paused = false;
        }

        public void updateFrozenState(boolean frozen) {
            this.frozen = frozen;
        }

        @Override
        public float getGameTimeDeltaTicks() {
            return this.deltaTicks;
        }

        @Override
        public float getGameTimeDeltaPartialTick(boolean runsNormally) {
            if (!runsNormally && this.frozen) {
                return 1.0F;
            } else {
                return this.paused ? this.pausedDeltaTickResidual : this.deltaTickResidual;
            }
        }

        @Override
        public float getRealtimeDeltaTicks() {
            return this.realtimeDeltaTicks > 7.0F ? 0.5F : this.realtimeDeltaTicks;
        }
    }
}
