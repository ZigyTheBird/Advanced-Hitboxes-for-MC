package com.zigythebird.advanced_hitboxes.registry;

import com.zigythebird.advanced_hitboxes.item.DebugBowItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod.MOD_ID;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            BuiltInRegistries.ITEM,
            MOD_ID
    );

    public static final DeferredHolder<Item, DebugBowItem> DEBUG_BOW = ITEMS.register(
            "debug_bow",
            () -> new DebugBowItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true))
    );
}
