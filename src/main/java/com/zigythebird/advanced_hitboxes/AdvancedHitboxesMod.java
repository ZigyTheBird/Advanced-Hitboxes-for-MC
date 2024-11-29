package com.zigythebird.advanced_hitboxes;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import com.zigythebird.advanced_hitboxes.client.AdvancedHitboxesModClient;
import com.zigythebird.advanced_hitboxes.interfaces.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.cache.HitboxCache;
import com.zigythebird.advanced_hitboxes.registry.ModEntities;
import com.zigythebird.advanced_hitboxes.registry.ModItems;
import com.zigythebird.old_fabric_networking.network.ModCodecs;
import com.zigythebird.old_fabric_networking.network.NetworkManager;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(AdvancedHitboxesMod.MOD_ID)
public class AdvancedHitboxesMod
{
    public static final String MOD_ID = "advanced_hitboxes";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson gson = new Gson();

    public static final ResourceLocation PLAYER_ANIMATION_RESOURCES_SYNC_PACKET = id("player_animation_resources_sync");

    private static boolean isDoneLoading = false;
    private static final List<Entity> loadingQueue = new ArrayList<>();

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

    public AdvancedHitboxesMod(IEventBus modEventBus)
    {
        ModEntities.ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(AdvancedHitboxesMod::ClientInit);
        HitboxCache.reload();
        isDoneLoading = true;

        if (ModList.get().isLoaded("playeranimatorapi")) {
            NetworkManager.registerReceiver(PacketFlow.SERVERBOUND, PLAYER_ANIMATION_RESOURCES_SYNC_PACKET, (buf, context) -> {
                List<ResourceLocation> resources = buf.readList(ModCodecs.RESOURCELOCATION);
                for (ResourceLocation resource : resources) {
                    HitboxCache.loadPlayerAnim(resource);
                }
            });
        }
    }

    private static void ClientInit(final FMLClientSetupEvent event) {
        AdvancedHitboxesModClient.init();
    }

    public static boolean isIsDoneLoading() {
        return isDoneLoading;
    }

    public static <T extends Entity & AdvancedHitboxEntity> void addEntityToLoadingQueue(AdvancedHitboxEntity entity) {
        if (entity instanceof Entity) {
            loadingQueue.add((T)entity);
        }
    }
}
