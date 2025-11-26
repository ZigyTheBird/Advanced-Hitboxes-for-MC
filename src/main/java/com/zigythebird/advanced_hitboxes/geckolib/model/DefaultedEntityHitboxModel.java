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

package com.zigythebird.advanced_hitboxes.geckolib.model;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.cache.object.HitboxBone;
import com.zigythebird.advanced_hitboxes.geckolib.model.data.EntityModelData;
import com.zigythebird.playeranimcore.animation.AnimationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Optional;

/**
 * {@link DefaultedHitboxModel} specific to {@link net.minecraft.world.entity.Entity Entities}
 * <p>
 * Using this class pre-sorts provided asset paths into the "entity" subdirectory
 * <p>
 * Additionally it can automatically handle head-turning if the entity has a "head" bone
 */
public class DefaultedEntityHitboxModel<T extends AdvancedHitboxEntity> extends DefaultedHitboxModel<T> {
	protected final boolean turnsHead;

	/**
	 * Create a new instance of this model class
	 * <p>
	 * The asset path should be the truncated relative path from the base folder
	 * <p>
	 * E.G.
	 * <pre>{@code
	 * 	new ResourceLocation("myMod", "animals/red_fish")
	 * }</pre>
	 */
	public DefaultedEntityHitboxModel(ResourceLocation assetSubpath) {
		this(assetSubpath, false);
	}

	public DefaultedEntityHitboxModel(ResourceLocation assetSubpath, boolean turnsHead) {
		super(assetSubpath);

		this.turnsHead = turnsHead;
	}

	/**
	 * Returns the subtype string for this type of model
	 * <p>
	 * This allows for sorting of asset files into neat subdirectories for clean management.
	 */
	@Override
	protected String subtype() {
		return "entity";
	}

	//TODO implement
	//Don't forget to override this in the player model in order to disable it
//	@Override
//	public void setCustomAnimations(T animatable, AnimationData data) {
//		if (!this.turnsHead)
//			return;
//
//		Optional<HitboxBone> head = this.getBone("head");
//
//		if (head.isPresent()) {
//			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
//
//			head.get().setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
//			head.get().setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
//		}
//	}

	/**
	 * Changes the constructor-defined model path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	 */
	@Override
	public DefaultedEntityHitboxModel<T> withAltModel(ResourceLocation altPath) {
		return (DefaultedEntityHitboxModel<T>)super.withAltModel(altPath);
	}

	/**
	 * Changes the constructor-defined animations path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	 */
	@Override
	public DefaultedEntityHitboxModel<T> withAltAnimations(ResourceLocation altPath) {
		return (DefaultedEntityHitboxModel<T>)super.withAltAnimations(altPath);
	}
}
