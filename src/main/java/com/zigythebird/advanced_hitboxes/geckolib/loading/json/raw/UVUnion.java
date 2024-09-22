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
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.Nullable;
import com.zigythebird.advanced_hitboxes.geckolib.util.JsonUtil;

/**
 * Container class for UV information, only used in deserialization at startup
 */
public record UVUnion(double[] boxUVCoords, @Nullable UVFaces faceUV, boolean isBoxUV) {
	public static JsonDeserializer<UVUnion> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			if (json.isJsonObject()) {
				return new UVUnion(new double[0], context.deserialize(json.getAsJsonObject(), UVFaces.class), false);
			}
			else if (json.isJsonArray()) {
				return new UVUnion(JsonUtil.jsonArrayToDoubleArray(json.getAsJsonArray()), null, true);
			}
			else {
				throw new JsonParseException("Invalid format provided for UVUnion, must be either double array or UVFaces collection");
			}
		};
	}
}
