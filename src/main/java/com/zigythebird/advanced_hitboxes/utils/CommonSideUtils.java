package com.zigythebird.advanced_hitboxes.utils;

import com.zigythebird.advanced_hitboxes.interfaces.LivingEntityAccessor;
import com.zigythebird.advanced_hitboxes.client.utils.ClientUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class CommonSideUtils {
    public static float getTickDelta(Level level) {
        if (level.isClientSide) {
            return ClientUtils.getTickDelta();
        }
        return 0;
    }

    public static float getBodyYRot(LivingEntity entity) {
        return ((LivingEntityAccessor)entity).advanced_Hitboxes$commonYBodyRot();
    }

    public static float getBodyYRot0(LivingEntity entity) {
        return ((LivingEntityAccessor)entity).advanced_Hitboxes$commonYBodyRot0();
    }

    public static void setBodyYRot(LivingEntity entity, float rot) {
        ((LivingEntityAccessor)entity).advanced_Hitboxes$setCommonYBodyRot(rot);
    }

    public static void setBodyYRot0(LivingEntity entity, float rot) {
        ((LivingEntityAccessor)entity).advanced_Hitboxes$setCommonYBodyRot0(rot);
    }
}
