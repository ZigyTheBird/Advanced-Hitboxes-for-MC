package com.zigythebird.advanced_hitboxes.utils;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class ClientUtils {
    public static boolean isGamePaused() {
        return Minecraft.getInstance().isPaused();
    }

    public static void updateHitboxIfRendering(AdvancedHitboxEntity entity) {
        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes() && !((Entity)entity).isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo())
            HitboxUtils.updateOrMakeHitboxesForEntity(entity);
    }
}
