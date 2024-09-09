package com.zigythebird.advanced_hitboxes.misc;

import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;

import java.util.List;

public interface EntityInterface {
    List<AdvancedHitbox> advanced_Hitboxes$getHitboxes();
    float advanced_Hitboxes$commonYBodyRot();
    void advanced_Hitboxes$setCommonYBodyRot(float rot);
}
