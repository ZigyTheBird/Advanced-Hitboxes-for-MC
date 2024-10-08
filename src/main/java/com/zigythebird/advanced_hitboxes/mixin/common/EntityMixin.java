package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.interfaces.EntityInterface;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.utils.HitboxUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public class EntityMixin implements EntityInterface {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(EntityType entityType, Level level, CallbackInfo ci) {
        if (this instanceof AdvancedHitboxEntity || ((Entity)(Object)this) instanceof Player) {
            if (AdvancedHitboxesMod.isIsDoneLoading()) {
                HitboxUtils.updateOrMakeHitboxesForEntity(((AdvancedHitboxEntity) this));
            }
            else {
                AdvancedHitboxesMod.addEntityToLoadingQueue(((AdvancedHitboxEntity) this));
            }
        }
    }

    @Unique
    private final List<AdvancedHitbox> advanced_hitboxes$hitboxes = new ArrayList<>();

    @Override
    public List<AdvancedHitbox> advanced_Hitboxes$getHitboxes() {
        return advanced_hitboxes$hitboxes;
    }
}
