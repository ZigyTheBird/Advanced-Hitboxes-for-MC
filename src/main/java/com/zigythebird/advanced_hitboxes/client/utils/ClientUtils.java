package com.zigythebird.advanced_hitboxes.client.utils;

import net.minecraft.client.Minecraft;

public class ClientUtils {
    public static boolean isGamePaused() {
        return Minecraft.getInstance().isPaused();
    }

    public static boolean hasSinglePlayerServer() {
        return Minecraft.getInstance().hasSingleplayerServer();
    }

    public static float getTickDelta() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
    }
}
