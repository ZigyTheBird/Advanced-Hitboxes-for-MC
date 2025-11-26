/*
 * MIT License
 *
 * Copyright (c) 2024 GeckoThePecko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.zigythebird.advanced_hitboxes.geckolib.cache;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.loading.FileLoader;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.Model;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.BakedModelFactory;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.GeometryTree;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.utils.CommonResourceManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Cache class for holding loaded {@link HitboxModel Models}
 */
public class HitboxCache {
    private static final Set<String> EXCLUDED_NAMESPACES = ObjectOpenHashSet.of("moreplayermodels", "customnpcs", "gunsrpg");

    private static Map<ResourceLocation, BakedHitboxModel> MODELS = Collections.emptyMap();

    public static Map<ResourceLocation, BakedHitboxModel> getBakedModels() {
        return MODELS;
    }

    public static CompletableFuture<Void> reload() {
        Map<ResourceLocation, BakedHitboxModel> models = new Object2ObjectOpenHashMap<>();

        return CompletableFuture.allOf(
                        loadModels(models::put))
                .thenAcceptAsync(empty -> HitboxCache.MODELS = models);
    }

    private static CompletableFuture<Void> loadModels(BiConsumer<ResourceLocation, BakedHitboxModel> elementConsumer) {
        return loadResources(resource -> {
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

    private static <T> CompletableFuture<Void> loadResources(Function<ResourceLocation, T> loader, BiConsumer<ResourceLocation, T> map) {
        return CompletableFuture.supplyAsync(
                        () -> CommonResourceManager.listResources("geo", fileName -> fileName.toString().endsWith(".json")))
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
