package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.instance.AdvancedHitboxInstanceCache;
import com.zigythebird.advanced_hitboxes.geckolib.model.DefaultedEntityHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.util.HitboxModelUtil;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerMixin implements AdvancedHitboxEntity {
    @Unique
    private static final HitboxModel advanced_Hitboxes$hitboxModel = new DefaultedEntityHitboxModel<>(AdvancedHitboxesMod.id("player_hitbox"));

    @Override
    public HitboxModel getHitboxModel() {
        return advanced_Hitboxes$hitboxModel;
    }

    @Unique
    private final AdvancedHitboxInstanceCache advanced_Hitboxes$hitbox_cache = HitboxModelUtil.createInstanceCache(this);

    @Override
    public AdvancedHitboxInstanceCache getAnimatableInstanceCache() {
        return advanced_Hitboxes$hitbox_cache;
    }
}
