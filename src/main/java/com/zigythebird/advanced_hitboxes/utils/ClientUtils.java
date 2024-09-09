package com.zigythebird.advanced_hitboxes.utils;

import net.minecraft.client.Minecraft;

public class ClientUtils {
    public static boolean isGamePaused() {
        return Minecraft.getInstance().isPaused();
    }
}
