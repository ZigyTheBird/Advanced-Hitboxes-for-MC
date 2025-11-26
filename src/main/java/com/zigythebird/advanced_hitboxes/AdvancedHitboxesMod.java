package com.zigythebird.advanced_hitboxes;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import com.zigythebird.advanced_hitboxes.client.AdvancedHitboxesModClient;
import com.zigythebird.advanced_hitboxes.geckolib.cache.HitboxCache;
import com.zigythebird.advanced_hitboxes.registry.ModEntities;
import com.zigythebird.advanced_hitboxes.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod(AdvancedHitboxesMod.MOD_ID)
public class AdvancedHitboxesMod {
    public static final String MOD_ID = "advanced_hitboxes";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson gson = new Gson();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Throw an exception pertaining to a specific resource
     * <p>
     * This mostly serves as a helper for consistent formatting of exceptions
     *
     * @param resource The location or id of the resource the error pertains to
     * @param message The error message to display
     */
    public static RuntimeException exception(ResourceLocation resource, String message) {
        return new RuntimeException(resource + ": " + message);
    }

    /**
     * Throw an exception pertaining to a specific resource
     * <p>
     * This mostly serves as a helper for consistent formatting of exceptions
     *
     * @param resource The location or id of the resource the error pertains to
     * @param message The error message to display
     * @param exception The exception to throw
     */
    public static RuntimeException exception(ResourceLocation resource, String message, Throwable exception) {
        return new RuntimeException(resource + ": " + message, exception);
    }

    public AdvancedHitboxesMod(IEventBus modEventBus) {
        ModEntities.ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(AdvancedHitboxesMod::ClientInit);
        HitboxCache.reload();
    }

    private static void ClientInit(final FMLClientSetupEvent event) {
        AdvancedHitboxesModClient.init();
    }
}
