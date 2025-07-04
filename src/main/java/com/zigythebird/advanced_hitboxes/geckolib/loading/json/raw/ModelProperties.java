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
import com.zigythebird.advanced_hitboxes.geckolib.util.JsonUtil;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Container class for model property information, only used in deserialization at startup
 */
public record ModelProperties(@Nullable Boolean animationArmsDown, @Nullable Boolean animationArmsOutFront,
							  @Nullable Boolean animationDontShowArmor, @Nullable Boolean animationInvertedCrouch,
							  @Nullable Boolean animationNoHeadBob, @Nullable Boolean animationSingleArmAnimation,
							  @Nullable Boolean animationSingleLegAnimation, @Nullable Boolean animationStationaryLegs,
							  @Nullable Boolean animationStatueOfLibertyArms, @Nullable Boolean animationUpsideDown,
							  @Nullable String identifier, @Nullable Boolean preserveModelPose,
							  double textureHeight, double textureWidth,
							  @Nullable Double visibleBoundsHeight, double[] visibleBoundsOffset,
							  @Nullable Double visibleBoundsWidth) {
	public static JsonDeserializer<ModelProperties> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			JsonObject obj = json.getAsJsonObject();
			Boolean animationArmsDown = JsonUtil.getOptionalBoolean(obj, "animationArmsDown");
			Boolean animationArmsOutFront = JsonUtil.getOptionalBoolean(obj, "animationArmsOutFront");
			Boolean animationDontShowArmor = JsonUtil.getOptionalBoolean(obj, "animationDontShowArmor");
			Boolean animationInvertedCrouch = JsonUtil.getOptionalBoolean(obj, "animationInvertedCrouch");
			Boolean animationNoHeadBob = JsonUtil.getOptionalBoolean(obj, "animationNoHeadBob");
			Boolean animationSingleArmAnimation = JsonUtil.getOptionalBoolean(obj, "animationSingleArmAnimation");
			Boolean animationSingleLegAnimation = JsonUtil.getOptionalBoolean(obj, "animationSingleLegAnimation");
			Boolean animationStationaryLegs = JsonUtil.getOptionalBoolean(obj, "animationStationaryLegs");
			Boolean animationStatueOfLibertyArms = JsonUtil.getOptionalBoolean(obj, "animationStatueOfLibertyArms");
			Boolean animationUpsideDown = JsonUtil.getOptionalBoolean(obj, "animationUpsideDown");
			String identifier = GsonHelper.getAsString(obj, "identifier", null);
			Boolean preserveModelPose = JsonUtil.getOptionalBoolean(obj, "preserve_model_pose");
			double textureHeight = GsonHelper.getAsDouble(obj, "texture_height");
			double textureWidth = GsonHelper.getAsDouble(obj, "texture_width");
			Double visibleBoundsHeight = JsonUtil.getOptionalDouble(obj, "visible_bounds_height");
			double[] visibleBoundsOffset = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "visible_bounds_offset", null));
			Double visibleBoundsWidth = JsonUtil.getOptionalDouble(obj, "visible_bounds_width");

			return new ModelProperties(animationArmsDown, animationArmsOutFront, animationDontShowArmor, animationInvertedCrouch,
					animationNoHeadBob, animationSingleArmAnimation, animationSingleLegAnimation, animationStationaryLegs,
					animationStatueOfLibertyArms, animationUpsideDown, identifier, preserveModelPose, textureHeight,
					textureWidth, visibleBoundsHeight, visibleBoundsOffset, visibleBoundsWidth);
		};
	}
}
