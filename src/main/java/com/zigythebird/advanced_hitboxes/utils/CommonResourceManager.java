package com.zigythebird.advanced_hitboxes.utils;

import com.google.gson.JsonArray;
import com.mojang.serialization.JsonOps;
import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

public class CommonResourceManager {

    public static InputStream getResourceOrThrow(ResourceLocation location) throws FileNotFoundException {
        try {
            for (IModInfo modInfo : ModList.get().getMods()) {
                if (modInfo.getModId().equals(location.getNamespace())) {
                    return AdvancedHitboxesMod.class.getClassLoader().getResourceAsStream("assets/" + location.getNamespace() + "/" + location.getPath());
                }
            }
        }
        catch (Exception e) {
            throw new FileNotFoundException(e.toString());
        }
        throw new FileNotFoundException(location.toString());
    }

    public static List<ResourceLocation> listResources(String path, Predicate<ResourceLocation> filter) {
        List<ResourceLocation> resources = new ArrayList<>();
        for (IModInfo modInfo : ModList.get().getMods()) {
            try {
                resources.addAll(ResourceLocation.CODEC.listOf().parse(JsonOps.INSTANCE, AdvancedHitboxesMod.gson.fromJson(new InputStreamReader(AdvancedHitboxesMod.class.getClassLoader().getResourceAsStream("assets/" + modInfo.getModId() + "/hitbox_model_index.json"), StandardCharsets.UTF_8), JsonArray.class))
                        .getOrThrow().stream().filter(resource -> resource.getPath().split("/").length >= 1 && resource.getPath().split("/")[0].equals(path) && filter.test(resource)).toList());
            }
            catch (Exception ignore) {}
        }
        return resources;
    }
}
