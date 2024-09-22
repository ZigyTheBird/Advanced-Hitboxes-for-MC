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
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Container class for UV face information, only used in deserialization at startup
 */
public record UVFaces(@Nullable FaceUV north, @Nullable FaceUV south, @Nullable FaceUV east, @Nullable FaceUV west, @Nullable FaceUV up, @Nullable FaceUV down) {
	public static JsonDeserializer<UVFaces> deserializer() {
		return (json, type, context) -> {
			JsonObject obj = json.getAsJsonObject();
			FaceUV north = GsonHelper.getAsObject(obj, "north", null, context, FaceUV.class);
			FaceUV south = GsonHelper.getAsObject(obj, "south", null, context, FaceUV.class);
			FaceUV east = GsonHelper.getAsObject(obj, "east", null, context, FaceUV.class);
			FaceUV west = GsonHelper.getAsObject(obj, "west", null, context, FaceUV.class);
			FaceUV up = GsonHelper.getAsObject(obj, "up", null, context, FaceUV.class);
			FaceUV down = GsonHelper.getAsObject(obj, "down", null, context, FaceUV.class);

			return new UVFaces(north, south, east, west, up, down);
		};
	}

	public FaceUV fromDirection(Direction direction) {
		return switch(direction) {
			case NORTH -> north;
			case SOUTH -> south;
			case EAST -> east;
			case WEST -> west;
			case UP -> up;
			case DOWN -> down;
		};
	}
}
