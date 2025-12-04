package com.zigythebird.advanced_hitboxes.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.zigythebird.advanced_hitboxes.interfaces.LivingEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(EntityType.class)
public class EntityTypeMixin<T extends Entity> {
    @Inject(method = "create(Lnet/minecraft/server/level/ServerLevel;Ljava/util/function/Consumer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;ZZ)Lnet/minecraft/world/entity/Entity;", at = @At("TAIL"))
    private void create(ServerLevel level, Consumer<T> consumer, BlockPos pos, MobSpawnType spawnType, boolean shouldOffsetY, boolean shouldOffsetYMore, CallbackInfoReturnable<T> cir, @Local T t) {
        if (t instanceof Mob mob) {
            ((LivingEntityAccessor)mob).advanced_Hitboxes$setCommonYBodyRot(mob.getYRot());
            ((LivingEntityAccessor)mob).advanced_Hitboxes$setCommonYBodyRot0(mob.getYRot());
        }
    }
}
