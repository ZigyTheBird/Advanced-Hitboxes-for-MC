package com.zigythebird.advanced_hitboxes;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import com.zigythebird.advanced_hitboxes.client.AdvancedHitboxesModClient;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.entity.ExampleAdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.cache.HitboxCache;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(AdvancedHitboxesMod.MOD_ID)
public class AdvancedHitboxesMod
{
    public static final String MOD_ID = "advanced_hitboxes";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson gson = new Gson();

    private static boolean isDoneLoading = false;
    private static final List<Entity> loadingQueue = new ArrayList<>();

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
            BuiltInRegistries.ENTITY_TYPE,
            MOD_ID
    );

    public static final DeferredHolder<EntityType<?>, EntityType<ExampleAdvancedHitboxEntity>> EXAMPLE_ENTITY = ENTITIES.register(
            "example_entity",
            () -> EntityType.Builder.of(ExampleAdvancedHitboxEntity::new, MobCategory.MISC).sized(1, 1).build("example_entity")
    );

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

    public AdvancedHitboxesMod(IEventBus modEventBus, ModContainer modContainer)
    {
        ENTITIES.register(modEventBus);
        modEventBus.addListener(AdvancedHitboxesMod::ClientInit);
        HitboxCache.reload();
        isDoneLoading = true;
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
