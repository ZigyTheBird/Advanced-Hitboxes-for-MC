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

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import com.zigythebird.advanced_hitboxes.geckolib.util.JsonUtil;

/**
 * Container class for poly mesh information, only used in deserialization at startup
 */
public record PolyMesh(@Nullable Boolean normalizedUVs, double[] normals, @Nullable PolysUnion polysUnion, double[] positions, double[] uvs) {
	public static JsonDeserializer<PolyMesh> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			JsonObject obj = json.getAsJsonObject();
			Boolean normalizedUVs = JsonUtil.getOptionalBoolean(obj, "normalized_uvs");
			double[] normals = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "normals", null));
			PolysUnion polysUnion = GsonHelper.getAsObject(obj, "polys", null, context, PolysUnion.class);
			double[] positions = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "positions", null));
			double[] uvs = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "uvs", null));

			return new PolyMesh(normalizedUVs, normals, polysUnion, positions, uvs);
		};
	}
}
