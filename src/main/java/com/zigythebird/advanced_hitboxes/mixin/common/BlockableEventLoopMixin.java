package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.interfaces.TimerInterface;
import com.zigythebird.advanced_hitboxes.utils.ServerDeltaTracker;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(BlockableEventLoop.class)
public class BlockableEventLoopMixin {
    @Inject(method = "managedBlock", at = @At("HEAD"))
    private void managedBlock(BooleanSupplier isDone, CallbackInfo ci) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        ServerDeltaTracker.Timer timer = ((TimerInterface)server).advanced_hitboxes$timer();
        timer.advanceTime(Util.getMillis(), true);
        timer.updatePauseState(server.isPaused());
        timer.updateFrozenState(!server.tickRateManager().runsNormally());
    }
}
