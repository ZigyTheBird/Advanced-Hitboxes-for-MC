package com.zigythebird.advanced_hitboxes.misc;

import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.fml.common.asm.enumextension.ReservedConstructor;

public enum CommonArmPose implements IExtensibleEnum {
    EMPTY(false),
    ITEM(false),
    BLOCK(false),
    BOW_AND_ARROW(true),
    THROW_SPEAR(false),
    CROSSBOW_CHARGE(true),
    CROSSBOW_HOLD(true),
    SPYGLASS(false),
    TOOT_HORN(false),
    BRUSH(false);

    private final boolean twoHanded;

    @ReservedConstructor
    CommonArmPose(boolean twoHanded) {
        this.twoHanded = twoHanded;
    }

    public boolean isTwoHanded() {
        return this.twoHanded;
    }
}
