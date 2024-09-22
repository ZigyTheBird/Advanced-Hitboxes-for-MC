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
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import com.zigythebird.advanced_hitboxes.geckolib.util.JsonUtil;

/**
 * Container class for poly union information, only used in deserialization at startup
 */
public record PolysUnion(double[][][] union, @Nullable Type type) {
	public static JsonDeserializer<PolysUnion> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
				return new PolysUnion(new double[0][0][0], context.deserialize(json.getAsJsonPrimitive(), Type.class));
			}
			else if (json.isJsonArray()) {
				JsonArray array = json.getAsJsonArray();
				double[][][] matrix = makeSizedMatrix(array);

				for (int x = 0; x < array.size(); x++) {
					JsonArray xArray = array.get(x).getAsJsonArray();

					for (int y = 0; y < xArray.size(); y++) {
						JsonArray yArray = xArray.get(y).getAsJsonArray();

						matrix[x][y] = JsonUtil.jsonArrayToDoubleArray(yArray);
					}
				}

				return new PolysUnion(matrix, null);
			}
			else {
				throw new JsonParseException("Invalid format for PolysUnion, must be either string or array");
			}
		};
	}

	private static double[][][] makeSizedMatrix(JsonArray array) {
		JsonArray subArray = array.size() > 0 ? array.get(0).getAsJsonArray() : null;
		JsonArray subSubArray = subArray != null && subArray.size() > 0 ? subArray.get(0).getAsJsonArray() : null;
		int ySize = subArray != null ? subArray.size() : 0;
		int zSize = subSubArray != null ? subSubArray.size() : 0;

		return new double[array.size()][ySize][zSize];
	}

	public enum Type {
		@SerializedName(value = "quad_list") QUAD,
		@SerializedName(value = "tri_list") TRI;
	}
}
