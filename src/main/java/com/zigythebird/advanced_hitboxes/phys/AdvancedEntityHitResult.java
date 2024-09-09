package com.zigythebird.advanced_hitboxes.phys;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;

public class AdvancedEntityHitResult extends EntityHitResult {

    private final String hitboxName;

    public AdvancedEntityHitResult(Entity player, String hitboxName) {
        super(player);
        this.hitboxName = hitboxName;
    }

    public String getHitboxName() {
        return hitboxName;
    }
}
