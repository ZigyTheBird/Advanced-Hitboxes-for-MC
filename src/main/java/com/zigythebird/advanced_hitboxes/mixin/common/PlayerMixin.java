package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.*;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxGeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.instance.AdvancedHitboxInstanceCache;
import com.zigythebird.advanced_hitboxes.geckolib.model.DefaultedEntityHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.util.HitboxModelUtil;
import com.zigythebird.advanced_hitboxes.utils.PlayerHitboxProcessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements AdvancedHitboxEntity {
    @Unique
    private static final HitboxModel advanced_Hitboxes$hitboxModel = new DefaultedEntityHitboxModel<>(AdvancedHitboxesMod.id("player_hitbox"));

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public HitboxModel getHitboxModel() {
        return advanced_Hitboxes$hitboxModel;
    }

    @Unique
    private final AdvancedHitboxInstanceCache advanced_Hitboxes$hitbox_cache = HitboxModelUtil.createInstanceCache(this);

    @Override
    public AdvancedHitboxInstanceCache getHitboxInstanceCache() {
        return advanced_Hitboxes$hitbox_cache;
    }

    @Override
    public void registerHitboxControllers(HitboxAnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new HitboxAnimationController<>(this, 0, state -> PlayState.STOP)
                .setAnimationSpeedHandler((player) -> {
                    RawAnimation animation = this.getHitboxInstanceCache().getManagerForId(this.getId()).getAnimationControllers().get("base_controller").getCurrentRawAnimation();
                    if (animation instanceof PlayerRawAnimation) {
                        return ((PlayerRawAnimation)animation).getSpeed();
                    }
                    else {
                        return 1D;
                    }
                }));
    }

    @Override
    public void applyTransformationsToBone(HitboxGeoBone bone) {
        PlayerHitboxProcessor.tickAnimation((Player)(Object)this, bone);
    }
}
