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

import com.zigythebird.advanced_hitboxes.geckolib.cache.object.BakedHitboxModel;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.GeoCube;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxGeoBone;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.Bone;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.Cube;
import com.zigythebird.advanced_hitboxes.geckolib.loading.json.raw.ModelProperties;
import com.zigythebird.advanced_hitboxes.geckolib.util.RenderUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;
import java.util.Map;

/**
 * Base interface for a factory of {@link BakedHitboxModel} objects
 * <p>
 * Handled by default by GeckoLib, but custom implementations may be added by other mods for special needs
 */
public interface BakedModelFactory {
	Map<String, BakedModelFactory> FACTORIES = new Object2ObjectOpenHashMap<>(1);
	BakedModelFactory DEFAULT_FACTORY = new Builtin();

	/**
	 * Construct the output model from the given {@link GeometryTree}.<br>
	 */
	BakedHitboxModel constructGeoModel(GeometryTree geometryTree);

	/**
	 * Construct a {@link HitboxGeoBone} from the relevant raw input data
	 *
	 * @param boneStructure The {@code BoneStructure} comprising the structure of the bone and its children
	 * @param properties The loaded properties for the model
	 * @param parent The parent bone for this bone, or null if a top-level bone
	 */
	HitboxGeoBone constructBone(BoneStructure boneStructure, ModelProperties properties, @Nullable HitboxGeoBone parent);

	/**
	 * Construct a {@link GeoCube} from the relevant raw input data
	 *
	 * @param cube The raw {@code Cube} comprising the structure and properties of the cube
	 * @param properties The loaded properties for the model
	 * @param bone The bone this cube belongs to
	 */
	GeoCube constructCube(Cube cube, ModelProperties properties, HitboxGeoBone bone);

	static BakedModelFactory getForNamespace(String namespace) {
		return FACTORIES.getOrDefault(namespace, DEFAULT_FACTORY);
	}

	/**
	 * Register a custom {@link BakedModelFactory} to handle loading models in a custom way
	 * <p>
	 * <b><u>MUST be called during mod construct</u></b>
	 * <p>
	 * It is recommended you don't call this directly, and instead call it via {@link com.zigythebird.advanced_hitboxes.geckolib.util.HitboxModelUtil#addCustomBakedModelFactory}
	 *
	 * @param namespace The namespace (modid) to register the factory for
	 * @param factory The factory responsible for model loading under the given namespace
	 */
	static void register(String namespace, BakedModelFactory factory) {
		FACTORIES.put(namespace, factory);
	}

	final class Builtin implements BakedModelFactory {
		@Override
		public BakedHitboxModel constructGeoModel(GeometryTree geometryTree) {
			List<HitboxGeoBone> bones = new ObjectArrayList<>();

			for (BoneStructure boneStructure : geometryTree.topLevelBones().values()) {
				bones.add(constructBone(boneStructure, geometryTree.properties(), null));
			}

			return new BakedHitboxModel(bones, geometryTree.properties());
		}

		@Override
		public HitboxGeoBone constructBone(BoneStructure boneStructure, ModelProperties properties, HitboxGeoBone parent) {
			Bone bone = boneStructure.self();
			HitboxGeoBone newBone = new HitboxGeoBone(parent, bone.name(), bone.inflate());
			Vec3 rotation = RenderUtil.arrayToVec(bone.rotation());
			Vec3 pivot = RenderUtil.arrayToVec(bone.pivot());

			newBone.updateRotation((float)Math.toRadians(-rotation.x), (float)Math.toRadians(-rotation.y), (float)Math.toRadians(rotation.z));
			newBone.updatePivot((float)-pivot.x, (float)pivot.y, (float)pivot.z);

			for (Cube cube : bone.cubes()) {
				newBone.getCubes().add(constructCube(cube, properties, newBone));
			}

			for (BoneStructure child : boneStructure.children().values()) {
				newBone.getChildBones().add(constructBone(child, properties, newBone));
			}

			newBone.hitboxType = bone.hitboxType();

			return newBone;
		}

		@Override
		public GeoCube constructCube(Cube cube, ModelProperties properties, HitboxGeoBone bone) {
			boolean mirror = cube.mirror() == Boolean.TRUE;
			double inflate = cube.inflate() != null ? cube.inflate() / 16f : (bone.getInflate() == null ? 0 : bone.getInflate() / 16f);
			Vec3 size = software.bernie.geckolib.util.RenderUtil.arrayToVec(cube.size());
			Vec3 origin = software.bernie.geckolib.util.RenderUtil.arrayToVec(cube.origin());
			Vec3 rotation = software.bernie.geckolib.util.RenderUtil.arrayToVec(cube.rotation());
			Vec3 pivot = software.bernie.geckolib.util.RenderUtil.arrayToVec(cube.pivot());
			origin = new Vec3(-(origin.x + size.x) / 16d, origin.y / 16d, origin.z / 16d);
			Vec3 vertexSize = size.multiply(1 / 16d, 1 / 16d, 1 / 16d);

			pivot = pivot.multiply(-1, 1, 1);
			rotation = new Vec3(Math.toRadians(-rotation.x), Math.toRadians(-rotation.y), Math.toRadians(rotation.z));

			return new GeoCube(new VertexSet(origin, vertexSize, inflate), origin, pivot, rotation, size, inflate, mirror);
		}
	}

	/**
	 * Holder class to make it easier to store and refer to vertices for a given cube
	 */
	record VertexSet(Vector3d bottomLeftBack, Vector3d bottomRightBack, Vector3d topLeftBack, Vector3d topRightBack,
					 Vector3d topLeftFront, Vector3d topRightFront, Vector3d bottomLeftFront, Vector3d bottomRightFront) {
		public VertexSet(Vec3 origin, Vec3 vertexSize, double inflation) {
			this(
					new Vector3d(origin.x - inflation, origin.y - inflation, origin.z - inflation),
					new Vector3d(origin.x - inflation, origin.y - inflation, origin.z + vertexSize.z + inflation),
					new Vector3d(origin.x - inflation, origin.y + vertexSize.y + inflation, origin.z - inflation),
					new Vector3d(origin.x - inflation, origin.y + vertexSize.y + inflation, origin.z + vertexSize.z + inflation),
					new Vector3d(origin.x + vertexSize.x + inflation, origin.y + vertexSize.y + inflation, origin.z - inflation),
					new Vector3d(origin.x + vertexSize.x + inflation, origin.y + vertexSize.y + inflation, origin.z + vertexSize.z + inflation),
					new Vector3d(origin.x + vertexSize.x + inflation, origin.y - inflation, origin.z - inflation),
					new Vector3d(origin.x + vertexSize.x + inflation, origin.y - inflation, origin.z + vertexSize.z + inflation));
		}

		/**
		 * Returns the normal vertex array for a west-facing quad
		 */
		public Vector3d[] quadWest() {
			return new Vector3d[] {this.topRightBack, this.topLeftBack, this.bottomLeftBack, this.bottomRightBack};
		}

		/**
		 * Returns the normal vertex array for an east-facing quad
		 */
		public Vector3d[] quadEast() {
			return new Vector3d[] {this.topLeftFront, this.topRightFront, this.bottomRightFront, this.bottomLeftFront};
		}

		/**
		 * Returns the normal vertex array for a north-facing quad
		 */
		public Vector3d[] quadNorth() {
			return new Vector3d[] {this.topLeftBack, this.topLeftFront, this.bottomLeftFront, this.bottomLeftBack};
		}

		/**
		 * Returns the normal vertex array for a south-facing quad
		 */
		public Vector3d[] quadSouth() {
			return new Vector3d[] {this.topRightFront, this.topRightBack, this.bottomRightBack, this.bottomRightFront};
		}

		/**
		 * Returns the normal vertex array for a top-facing quad
		 */
		public Vector3d[] quadUp() {
			return new Vector3d[] {this.topRightBack, this.topRightFront, this.topLeftFront, this.topLeftBack};
		}

		/**
		 * Returns the normal vertex array for a bottom-facing quad
		 */
		public Vector3d[] quadDown() {
			return new Vector3d[] {this.bottomLeftBack, this.bottomLeftFront, this.bottomRightFront, this.bottomRightBack};
		}

		public Vector3d[] allVertices() {
			return new Vector3d[] {this.bottomLeftBack, this.bottomRightBack, this.topRightBack, topLeftBack,
			this.bottomLeftFront, this.bottomRightFront, this.topRightFront, this.topLeftFront};
		}

		/**
		 * Return the vertex array relevant to the quad being built, taking into account mirroring and quad type
		 */
		public Vector3d[] verticesForQuad(Direction direction, boolean boxUv, boolean mirror) {
			return switch (direction) {
				case WEST -> mirror ? quadEast() : quadWest();
				case EAST -> mirror ? quadWest() : quadEast();
				case NORTH -> quadNorth();
				case SOUTH -> quadSouth();
				case UP -> mirror && !boxUv ? quadDown() : quadUp();
				case DOWN -> mirror && !boxUv ? quadUp() : quadDown();
			};
		}
	}
}
