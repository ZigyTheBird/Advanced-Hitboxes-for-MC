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

package com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.zigythebird.advanced_hitboxes.geckolib.util.JsonUtil;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Container class for bone information, only used in deserialization at startup
 */
public record Bone(double[] bindPoseRotation, Cube[] cubes, @Nullable Boolean debug,
				   @Nullable Double inflate, @Nullable Map<String, LocatorValue> locators,
				   @Nullable Boolean mirror, @Nullable String name, @Nullable Boolean neverRender,
				   @Nullable String parent, double[] pivot, @Nullable PolyMesh polyMesh,
				   @Nullable Long renderGroupId, @Nullable Boolean reset, double[] rotation,
				   String hitboxType) {
	public static JsonDeserializer<Bone> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			JsonObject obj = json.getAsJsonObject();
			double[] bindPoseRotation = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "bind_pose_rotation", null));
			Cube[] cubes = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "cubes", new JsonArray(0)), context, Cube.class);
			Boolean debug = JsonUtil.getOptionalBoolean(obj, "debug");
			Double inflate = JsonUtil.getOptionalDouble(obj, "inflate");
			Map<String, LocatorValue> locators = obj.has("locators") ? JsonUtil.jsonObjToMap(GsonHelper.getAsJsonObject(obj, "locators"), context, LocatorValue.class) : null;
			Boolean mirror = JsonUtil.getOptionalBoolean(obj, "mirror");
			String name = GsonHelper.getAsString(obj, "name", null);
			Boolean neverRender = JsonUtil.getOptionalBoolean(obj, "neverRender");
			String parent = GsonHelper.getAsString(obj, "parent", null);
			double[] pivot = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "pivot", new JsonArray(0)));
			PolyMesh polyMesh = GsonHelper.getAsObject(obj, "poly_mesh", null, context, PolyMesh.class);
			Long renderGroupId = JsonUtil.getOptionalLong(obj, "render_group_id");
			Boolean reset = JsonUtil.getOptionalBoolean(obj, "reset");
			double[] rotation = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "rotation", null));
			String hitboxType = GsonHelper.getAsString(obj, "hitbox_type", null);

			return new Bone(bindPoseRotation, cubes, debug, inflate, locators, mirror, name, neverRender, parent, pivot, polyMesh, renderGroupId, reset, rotation, hitboxType);
		};
	}
}
