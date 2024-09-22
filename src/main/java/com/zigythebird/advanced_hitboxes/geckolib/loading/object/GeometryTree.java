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

package com.zigythebird.advanced_hitboxes.geckolib.loading.object;

import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.Bone;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.MinecraftGeometry;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.Model;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.ModelProperties;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

/**
 * Container class for a {@link Bone} structure, used at startup during deserialization
 */
public record GeometryTree(Map<String, BoneStructure> topLevelBones, ModelProperties properties) {
	public static GeometryTree fromModel(Model model) {
		final Map<String, BoneStructure> topLevelBones = new Object2ObjectOpenHashMap<>();
		final MinecraftGeometry geometry = model.minecraftGeometry()[0];
		final Bone[] bones = geometry.bones();
		final Map<String, BoneStructure> lookup = new Object2ObjectOpenHashMap<>(bones.length);

		for (Bone bone : bones) {
			final BoneStructure boneStructure = new BoneStructure(bone);

			lookup.put(bone.name(), boneStructure);

			if (bone.parent() == null)
				topLevelBones.put(bone.name(), boneStructure);
		}

		for (Bone bone : bones) {
			final String parentName = bone.parent();

			if (parentName != null) {
				final String boneName = bone.name();

				if (parentName.equals(boneName))
					throw new IllegalArgumentException("Invalid model definition. Bone has defined itself as its own parent: " + boneName);

				final BoneStructure parentStructure = lookup.get(parentName);

				if (parentStructure == null)
					throw new IllegalArgumentException("Invalid model definition. Found bone with undefined parent (child -> parent): " + boneName + " -> " + parentName);

				parentStructure.children().put(boneName, lookup.get(boneName));
			}
		}

		return new GeometryTree(topLevelBones, geometry.modelProperties());
	}
}
