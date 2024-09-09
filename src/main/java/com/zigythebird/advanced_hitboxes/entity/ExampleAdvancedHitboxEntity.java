package com.zigythebird.advanced_hitboxes.entity;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.geckolib.instance.AdvancedHitboxInstanceCache;
import com.zigythebird.advanced_hitboxes.geckolib.model.DefaultedEntityHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.util.HitboxModelUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class ExampleAdvancedHitboxEntity extends Mob implements AdvancedHitboxEntity {

    public ExampleAdvancedHitboxEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    private static final HitboxModel<ExampleAdvancedHitboxEntity> hitboxModel = new DefaultedEntityHitboxModel<>(AdvancedHitboxesMod.id("hitbox_test"));

    @Override
    public HitboxModel<ExampleAdvancedHitboxEntity> getHitboxModel() {
        return hitboxModel;
    }

    private final AdvancedHitboxInstanceCache hitbox_cache = HitboxModelUtil.createInstanceCache(this);

    @Override
    public AdvancedHitboxInstanceCache getAnimatableInstanceCache() {
        return hitbox_cache;
    }
}
