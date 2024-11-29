package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.interfaces.TimerInterface;
import com.zigythebird.advanced_hitboxes.utils.ServerDeltaTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.TickRateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements TimerInterface {

    @Shadow public abstract ServerTickRateManager tickRateManager();

    @Unique
    private final ServerDeltaTracker.Timer advanced_hitboxes$timer = new ServerDeltaTracker.Timer(20.0F, 0L, this::advanced_hitboxes$getTickTargetMillis);

    @Unique
    private float advanced_hitboxes$getTickTargetMillis(float defaultValue) {
        TickRateManager tickratemanager = this.tickRateManager();
        if (tickratemanager.runsNormally()) {
            return Math.max(defaultValue, tickratemanager.millisecondsPerTick());
        }

        return defaultValue;
    }

    @Override
    public ServerDeltaTracker.Timer advanced_hitboxes$timer() {
        return advanced_hitboxes$timer;
    }
}
