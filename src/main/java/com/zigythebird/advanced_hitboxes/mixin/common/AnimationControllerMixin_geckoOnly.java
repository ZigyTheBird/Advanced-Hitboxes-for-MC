package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;

@Mixin(AnimationController.class)
public class AnimationControllerMixin_geckoOnly<T extends GeoAnimatable> {
    @Shadow @Final protected T animatable;

    @Shadow @Final protected String name;

    @Inject(method = "tryTriggerAnimation", at = @At("TAIL"))
    private void tryTriggerAnimation(String animName, CallbackInfoReturnable<Boolean> cir) {
        if (this.animatable instanceof AdvancedHitboxEntity) {
            ((AdvancedHitboxEntity)this.animatable).getHitboxInstanceCache().getManagerForId(((Entity)this.animatable).getId()).tryTriggerAnimation(this.name, animName);
        }
    }
}
