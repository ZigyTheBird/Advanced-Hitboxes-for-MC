package com.zigythebird.advanced_hitboxes.geckolib.cache;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.geckolib.animation.Animation;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.loading.FileLoader;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.Model;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.BakedAnimations;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.BakedModelFactory;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.GeometryTree;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.util.CompoundException;
import com.zigythebird.advanced_hitboxes.utils.CommonResourceManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Cache class for holding loaded {@link Animation Animations}
 * and {@link HitboxModel Models}
 */
public class HitboxCache {
    private static final Set<String> EXCLUDED_NAMESPACES = ObjectOpenHashSet.of("moreplayermodels", "customnpcs", "gunsrpg");

    private static Map<ResourceLocation, BakedAnimations> ANIMATIONS = Collections.emptyMap();
    private static Map<ResourceLocation, BakedHitboxModel> MODELS = Collections.emptyMap();

    public static Map<ResourceLocation, BakedAnimations> getBakedAnimations() {
        return ANIMATIONS;
    }

    public static Map<ResourceLocation, BakedHitboxModel> getBakedModels() {
        return MODELS;
    }

    public static CompletableFuture<Void> reload() {
        Map<ResourceLocation, BakedAnimations> animations = new Object2ObjectOpenHashMap<>();
        Map<ResourceLocation, BakedHitboxModel> models = new Object2ObjectOpenHashMap<>();

        return CompletableFuture.allOf(
                        loadAnimations(animations::put),
                        loadModels(models::put))
                .thenAcceptAsync(empty -> {
                    HitboxCache.ANIMATIONS = animations;
                    HitboxCache.MODELS = models;
                });
    }

    private static CompletableFuture<Void> loadAnimations(BiConsumer<ResourceLocation, BakedAnimations> elementConsumer) {
        return loadResources("animations", resource -> {
            try {
                return FileLoader.loadAnimationsFile(resource);
            }
            catch (CompoundException ex) {
                ex.withMessage(resource.toString() + ": Error loading animation file").printStackTrace();

                return new BakedAnimations(new Object2ObjectOpenHashMap<>());
            }
            catch (Exception ex) {
                throw AdvancedHitboxesMod.exception(resource, "Error loading animation file", ex);
            }
        }, elementConsumer);
    }

    private static CompletableFuture<Void> loadModels(BiConsumer<ResourceLocation, BakedHitboxModel> elementConsumer) {
        return loadResources("geo", resource -> {
            try {
                Model model = FileLoader.loadModelFile(resource);

                switch (model.formatVersion()) {
                    case V_1_12_0 -> {}
                    case V_1_14_0 -> throw new IllegalArgumentException("Unsupported geometry json version: 1.14.0. Supported versions: 1.12.0");
                    case V_1_21_0 -> throw new IllegalArgumentException("Unsupported geometry json version: 1.21.0. Supported versions: 1.12.0. Remove any rotated face UVs and re-export the model to fix");
                    case null, default -> throw new IllegalArgumentException("Unsupported geometry json version. Supported versions: 1.12.0");
                }

                return BakedModelFactory.getForNamespace(resource.getNamespace()).constructGeoModel(GeometryTree.fromModel(model));
            }
            catch (Exception ex) {
                throw AdvancedHitboxesMod.exception(resource, "Error loading model file", ex);
            }
        }, elementConsumer);
    }

    private static <T> CompletableFuture<Void> loadResources(String type, Function<ResourceLocation, T> loader, BiConsumer<ResourceLocation, T> map) {
        return CompletableFuture.supplyAsync(
                        () -> CommonResourceManager.listResources(type, fileName -> fileName.toString().endsWith(".json")))
                .thenApplyAsync(resources -> {
                    Map<ResourceLocation, CompletableFuture<T>> tasks = new Object2ObjectOpenHashMap<>();

                    for (ResourceLocation resource : resources) {
                        tasks.put(resource, CompletableFuture.supplyAsync(() -> loader.apply(resource)));
                    }

                    return tasks;
                })
                .thenAcceptAsync(tasks -> {
                    for (Entry<ResourceLocation, CompletableFuture<T>> entry : tasks.entrySet()) {
                        // Skip known namespaces that use an "animation" or "geo" folder as well
                        if (!EXCLUDED_NAMESPACES.contains(entry.getKey().getNamespace().toLowerCase(Locale.ROOT)))
                            map.accept(entry.getKey(), entry.getValue().join());
                    }
                });
    }

    /**
     * Register a new namespace to be excluded from GeckoLib's resource loader
     *
     * @param namespace The namespace to exclude
     */
    public static synchronized void registerNamespaceExclusion(String namespace) {
        EXCLUDED_NAMESPACES.add(namespace);
    }
}
