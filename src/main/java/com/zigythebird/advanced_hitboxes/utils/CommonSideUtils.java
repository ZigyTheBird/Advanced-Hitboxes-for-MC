package com.zigythebird.advanced_hitboxes.utils;

import com.zigythebird.advanced_hitboxes.client.utils.ClientUtils;
import com.zigythebird.advanced_hitboxes.interfaces.TimerInterface;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class CommonSideUtils {
    public static float getTickDelta(Level level) {
        if (level.isClientSide) {
            return ClientUtils.getTickDelta();
        }
        return ((TimerInterface) ServerLifecycleHooks.getCurrentServer()).advanced_hitboxes$timer().getGameTimeDeltaPartialTick(true);
    }
}
