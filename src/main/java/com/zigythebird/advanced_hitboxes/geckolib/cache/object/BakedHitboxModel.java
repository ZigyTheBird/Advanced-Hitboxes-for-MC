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

package com.zigythebird.advanced_hitboxes.geckolib.cache.object;

import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.ModelProperties;

import java.util.List;
import java.util.Optional;

/**
 * Baked model object for Geckolib models
 */
public record BakedHitboxModel(List<GeoBone> topLevelBones, ModelProperties properties) {
	/**
	 * Gets a bone from this model by name
	 * <p>
	 * Generally not a very efficient method, should be avoided where possible
	 *
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link GeoBone} if one matches, otherwise an empty Optional
	 */
	public Optional<GeoBone> getBone(String name) {
		for (GeoBone bone : this.topLevelBones) {
			GeoBone childBone = searchForChildBone(bone, name);

			if (childBone != null)
				return Optional.of(childBone);
		}

		return Optional.empty();
	}

	/**
	 * Search a given {@link GeoBone}'s child bones and see if any of them match the given name, then return it
	 *
	 * @param parent The parent bone to search the children of
	 * @param name The name of the child bone to find
	 * @return The {@code GeoBone} found in the parent's children list, or null if not found
	 */
	public GeoBone searchForChildBone(GeoBone parent, String name) {
		if (parent.getName().equals(name))
			return parent;

		for (GeoBone bone : parent.getChildBones()) {
			if (bone.getName().equals(name))
				return bone;

			GeoBone subChildBone = searchForChildBone(bone, name);

			if (subChildBone != null)
				return subChildBone;
		}

		return null;
	}
}
