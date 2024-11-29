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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.geckolib.animation.Animation;
import com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.BoneAnimation;
import com.zigythebird.advanced_hitboxes.geckolib.animation.keyframe.KeyframeStack;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.loading.FileLoader;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.Model;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.typeadapter.BakedAnimationsAdapter;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.typeadapter.GeoAdapter;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.BakedAnimations;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.BakedModelFactory;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.GeometryTree;
import com.zigythebird.advanced_hitboxes.geckolib.model.HitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.util.CompoundException;
import com.zigythebird.advanced_hitboxes.utils.CommonResourceManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Cache class for holding loaded {@link Animation Animations}
 * and {@link HitboxModel Models}
 */
public class HitboxCache {
    public static final ResourceLocation PLAYER_ANIMATION_RESOURCE = AdvancedHitboxesMod.id("animations/entity/player_hitbox.animation.json");

    private static final Set<String> EXCLUDED_NAMESPACES = ObjectOpenHashSet.of("moreplayermodels", "customnpcs", "gunsrpg");
    private static final List<ResourceLocation> loadedPlayerAnimationFiles = new ArrayList<>();

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

    public static void loadPlayerAnim(ResourceLocation id) {
        BakedAnimations animations = ANIMATIONS.get(PLAYER_ANIMATION_RESOURCE);
        if (!loadedPlayerAnimationFiles.contains(id)) {
            try {
                InputStream resource = CommonResourceManager.getResourceOrThrow(id);
                JsonObject json = AdvancedHitboxesMod.gson.fromJson(new InputStreamReader(resource), JsonObject.class);
                if (json.has("animations")) {
                    json = json.get("animations").getAsJsonObject();
                    JsonObject modifiedJson = new JsonObject();
                    for (Map.Entry<String, JsonElement> entry : json.asMap().entrySet()) {
                        JsonObject modifiedBones = new JsonObject();
                        for (Map.Entry<String, JsonElement> entry1 : entry.getValue().getAsJsonObject().get("bones").getAsJsonObject().asMap().entrySet()) {
                            modifiedBones.add(getCorrectPlayerBoneName(entry1.getKey()), entry1.getValue());
                        }
                        JsonObject entryJson = entry.getValue().getAsJsonObject();
                        entryJson.add("bones", modifiedBones);
                        modifiedJson.add(id.getNamespace() + ":" + entry.getKey(), entryJson);
                    }
                    BakedAnimations anim = GeoAdapter.GEO_GSON.fromJson(modifiedJson, BakedAnimations.class);
                    animations.animations().putAll(anim.animations());
                }
                else {
                    json.addProperty("name", id.getNamespace() + ":" + json.get("name"));
                    Animation animation = loadPlayerAnim(json);
                    animations.animations().put(animation.name(), animation);
                }
            }
            catch (Exception ignore) {}
            loadedPlayerAnimationFiles.add(id);
        }
    }

    public static Animation loadPlayerAnim(JsonElement json) {
        JsonObject obj = json.getAsJsonObject();
        List<BoneAnimation> boneAnims = new ArrayList<>();
        for (JsonElement jsonElement : obj.get("moves").getAsJsonArray()) {
            if (json.isJsonObject()) {
                JsonObject move = (JsonObject) jsonElement;
                int currentTick = move.get("tick").getAsInt();
                for (Map.Entry<String, JsonElement> entry : move.asMap().entrySet()) {
                    List<Pair<Integer, Vec3>> transforms = new ArrayList<>();
                    List<Pair<Integer, Vec3>> rotations = new ArrayList<>();
                    //Not gonna bother supporting scaling for now
                    //List<Pair<Integer, Vec3>> scales = new ArrayList<>();
                    JsonObject jsonObject = (JsonObject) entry.getValue();
                    double x = jsonObject.has("x") ? jsonObject.get("x").getAsDouble() : 0;
                    double y = jsonObject.has("y") ? jsonObject.get("y").getAsDouble() : 0;
                    double z = jsonObject.has("z") ? jsonObject.get("z").getAsDouble() : 0;
                    double pitch = jsonObject.has("pitch") ? jsonObject.get("pitch").getAsDouble() : 0;
                    double yaw = jsonObject.has("yaw") ? jsonObject.get("yaw").getAsDouble() : 0;
                    double roll = jsonObject.has("roll") ? jsonObject.get("roll").getAsDouble() : 0;
                    transforms.add(new Pair<>(currentTick, new Vec3(x, y, z)));
                    rotations.add(new Pair<>(currentTick, new Vec3(pitch, yaw, roll)));
                    boneAnims.add(new BoneAnimation(getCorrectPlayerBoneName(entry.getKey()), BakedAnimationsAdapter.buildKeyframeStackFromPlayerAnim(rotations), BakedAnimationsAdapter.buildKeyframeStackFromPlayerAnim(rotations), new KeyframeStack<>()));
                }
            }
        }
        BoneAnimation[] boneAnimations = boneAnims.toArray(new BoneAnimation[]{});
        String name = obj.get("name").getAsString();
        return new Animation(name, BakedAnimationsAdapter.calculateAnimationLength(boneAnimations),
                obj.get("emote").getAsJsonObject().get("isLoop").getAsBoolean() ? Animation.LoopType.LOOP : Animation.LoopType.PLAY_ONCE, boneAnimations);
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

    public static String getCorrectPlayerBoneName(String name) {
        StringBuilder sb = new StringBuilder(name.length());
        boolean uc = false;  // Flag to know whether to uppercase the char.
        for (int i = 0; i < name.length(); ++i) {
            int c = name.codePointAt(i);
            if (c == '_') {
                // Don't append the codepoint, but flag to uppercase the next codepoint
                // that isn't a '_'.
                uc = true;
            } else {
                if (uc) {
                    c = Character.toUpperCase(c);
                    uc = false;
                }
                sb.appendCodePoint(c);
            }
        }
        return sb + "_hitbox";
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
