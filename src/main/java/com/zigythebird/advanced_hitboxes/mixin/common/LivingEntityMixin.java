package com.zigythebird.advanced_hitboxes.mixin.common;

import com.zigythebird.advanced_hitboxes.interfaces.LivingEntityInterface;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityInterface {
    @Shadow public float yBodyRot;

    @Shadow public float yBodyRotO;
    @Unique
    private float advanced_Hitboxes$commonYBodyRot;
    @Unique
    private float advanced_Hitboxes$commonYBodyRot0;

    @Override
    public float advanced_Hitboxes$commonYBodyRot() {
        return advanced_Hitboxes$commonYBodyRot;
    }

    @Override
    public void advanced_Hitboxes$setCommonYBodyRot(float rot) {
        advanced_Hitboxes$commonYBodyRot = rot;
    }

    @Override
    public float advanced_Hitboxes$commonYBodyRot0() {
        return advanced_Hitboxes$commonYBodyRot0;
    }

    @Override
    public void advanced_Hitboxes$setCommonYBodyRot0(float rot) {
        advanced_Hitboxes$commonYBodyRot0 = rot;
    }
}
