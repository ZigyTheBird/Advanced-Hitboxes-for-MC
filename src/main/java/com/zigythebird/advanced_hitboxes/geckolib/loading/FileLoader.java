package com.zigythebird.advanced_hitboxes.geckolib.loading;

import com.google.gson.JsonObject;
import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.geckolib.animation.Animation;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.Model;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.typeadapter.GeoAdapter;
import com.zigythebird.advanced_hitboxes.geckolib.loading.object.BakedAnimations;
import com.zigythebird.advanced_hitboxes.utils.CommonResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Extracts raw information from given files, and other similar functions
 */
public final class FileLoader {
    /**
     * Load up and deserialize an animation json file to its respective {@link Animation} components
     *
     * @param location The resource path of the animations file
     */
    public static BakedAnimations loadAnimationsFile(ResourceLocation location) {
        if (location.getPath().endsWith(".geo.json"))
            throw new IllegalArgumentException("Geo model file found in animations folder!");

        if (!location.getPath().endsWith(".animation.json"))
            AdvancedHitboxesMod.LOGGER.warn("Found animation file with improper file name format; animation files should end in .animation.json: '" + location + "'");

        return GeoAdapter.GEO_GSON.fromJson(GsonHelper.getAsJsonObject(loadFile(location), "animations"), BakedAnimations.class);
    }

    /**
     * Load up and deserialize a geo model json file to its respective {@link BakedHitboxModel} format
     *
     * @param location The resource path of the model file
     */
    public static Model loadModelFile(ResourceLocation location) {
        if (location.getPath().endsWith(".animation.json"))
            throw new IllegalArgumentException("Animation file found in geo models folder!");

        if (!location.getPath().endsWith(".geo.json"))
            AdvancedHitboxesMod.LOGGER.warn("Found geo model file with improper file name format; geo model files should end in .geo.json: '" + location + "'");

        return GeoAdapter.GEO_GSON.fromJson(loadFile(location), Model.class);
    }

    /**
     * Load a given json file into memory
     *
     * @param location The resource path of the json file
     */
    public static JsonObject loadFile(ResourceLocation location) {
        return GsonHelper.fromJson(GeoAdapter.GEO_GSON, getFileContents(location), JsonObject.class);
    }

    /**
     * Read a text-based file into memory in the form of a single string
     *
     * @param location The resource path of the file
     */
    public static String getFileContents(ResourceLocation location) {
        try (InputStream inputStream = CommonResourceManager.getResourceOrThrow(location)) {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        }
        catch (Exception e) {
            AdvancedHitboxesMod.LOGGER.error("Couldn't load " + location, e);

            throw new RuntimeException(new FileNotFoundException(location.toString()));
        }
    }
}
