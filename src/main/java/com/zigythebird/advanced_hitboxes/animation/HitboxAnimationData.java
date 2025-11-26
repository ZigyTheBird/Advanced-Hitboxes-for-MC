package com.zigythebird.advanced_hitboxes.animation;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.playeranimcore.animation.AnimationData;

public class HitboxAnimationData extends AnimationData {
    private final AdvancedHitboxEntity entity;

    public HitboxAnimationData(AdvancedHitboxEntity entity, float velocity, float partialTick) {
        super(velocity, partialTick);
        this.entity = entity;
    }

    /**
     * Gets the current entity being animated
     */
    public AdvancedHitboxEntity getHitboxEntity() {
        return this.entity;
    }

    /**
     * Gets the current hitbox animation manager
     */
    public HitboxAnimManager getHitboxAnimManager(AdvancedHitboxEntity entity) {
        return getHitboxEntity().getHitboxModel().getManager(entity);
    }

    @Override
    public HitboxAnimationData copy() {
        return new HitboxAnimationData(getHitboxEntity(), getVelocity(), getPartialTick());
    }
}
