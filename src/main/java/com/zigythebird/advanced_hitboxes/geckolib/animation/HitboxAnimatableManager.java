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

package com.zigythebird.advanced_hitboxes.geckolib.animation;

import com.zigythebird.advanced_hitboxes.geckolib.animation.state.BoneSnapshot;
import com.zigythebird.advanced_hitboxes.geckolib.constant.dataticket.DataTicket;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The animation data collection for a given animatable instance
 * <p>
 * Generally speaking, a single working-instance of an {@link AdvancedHitboxEntity Animatable}
 * will have a single instance of {@code AnimatableManager} associated with it
 */
public class HitboxAnimatableManager<T extends AdvancedHitboxEntity> {
	private final Map<String, BoneSnapshot> boneSnapshotCollection = new Object2ObjectOpenHashMap<>();
	private final Map<String, HitboxAnimationController<T>> animationControllers;
	private Map<DataTicket<?>, Object> extraData;

	private double lastUpdateTime;
	private boolean isFirstTick = true;
	private double firstTickTime = -1;

	/**
	 * Instantiates a new AnimatableManager for the given animatable, calling {@link AdvancedHitboxEntity#advanced_hitboxes$registerControllers} to define its controllers
	 */
	public HitboxAnimatableManager(AdvancedHitboxEntity animatable) {
		ControllerRegistrar registrar = new ControllerRegistrar(new ObjectArrayList<>(2));

		animatable.advanced_hitboxes$registerControllers(registrar);

		this.animationControllers = registrar.build();
	}

	/**
	 * Add an {@link HitboxAnimationController} to this animatable's manager
	 * <p>
	 * Generally speaking you probably should have added it during {@link AdvancedHitboxEntity#advanced_hitboxes$registerControllers}
	 */
	public void addController(HitboxAnimationController controller) {
		getAnimationControllers().put(controller.getName(), controller);
	}

	/**
	 * Removes an {@link HitboxAnimationController} from this manager by the given name, if present.
	 */
	public void removeController(String name) {
		getAnimationControllers().remove(name);
	}

	public Map<String, HitboxAnimationController<T>> getAnimationControllers() {
		return this.animationControllers;
	}

	public Map<String, BoneSnapshot> getBoneSnapshotCollection() {
		return this.boneSnapshotCollection;
	}

	public void clearSnapshotCache() {
		getBoneSnapshotCollection().clear();
	}

	public double getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void updatedAt(double updateTime) {
		this.lastUpdateTime = updateTime;
	}

	public double getFirstTickTime() {
		return this.firstTickTime;
	}

	public void startedAt(double time) {
		this.firstTickTime = time;
	}

	public boolean isFirstTick() {
		return this.isFirstTick;
	}

	protected void finishFirstTick() {
		this.isFirstTick = false;
	}

	/**
	 * Set a custom data point to be used later
	 *
	 * @param dataTicket The DataTicket for the data point
	 * @param data The piece of data to store
	 */
	public <D> void setData(DataTicket<D> dataTicket, D data) {
		if (this.extraData == null)
			this.extraData = new Object2ObjectOpenHashMap<>();

		this.extraData.put(dataTicket, data);
	}

	/**
	 * Retrieve a custom data point that was stored earlier, or null if it hasn't been stored
	 */
	public <D> D getData(DataTicket<D> dataTicket) {
		return this.extraData != null ? dataTicket.getData(this.extraData) : null;
	}

	/**
	 * Attempt to trigger an animation from a given controller name and registered triggerable animation name
	 * <p>
	 * This pseudo-overloaded method checks each controller in turn until one of them accepts the trigger
	 * <p>
	 * This can be sped up by specifying which controller you intend to receive the trigger in {@link HitboxAnimatableManager#tryTriggerAnimation(String, String)}
	 *
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link HitboxAnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	public void tryTriggerAnimation(String animName) {
		for (HitboxAnimationController<?> controller : getAnimationControllers().values()) {
			if (controller.tryTriggerAnimation(animName))
				return;
		}
	}

	/**
	 * Attempt to trigger an animation from a given controller name and registered triggerable animation name
	 *
	 * @param controllerName The name of the controller name the animation belongs to
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link HitboxAnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	public void tryTriggerAnimation(String controllerName, String animName) {
		HitboxAnimationController<?> controller = getAnimationControllers().get(controllerName);

		if (controller != null)
			controller.tryTriggerAnimation(animName);
	}

	/**
	 * Helper class for the AnimatableManager to cleanly register controllers in one shot at instantiation for efficiency
	 */
	//@ reviewer conversion to record
	public record ControllerRegistrar(List<HitboxAnimationController<? extends AdvancedHitboxEntity>> controllers) {

		/**
		 * Add multiple {@link HitboxAnimationController}s to this registrar
		 */
		public ControllerRegistrar add(HitboxAnimationController<?>... controllers) {
			this.controllers().addAll(Arrays.asList(controllers));

			return this;
		}

		/**
		 * Add an {@link HitboxAnimationController} to this registrar
		 */
		public ControllerRegistrar add(HitboxAnimationController<?> controller) {
			this.controllers().add(controller);
			return this;
		}

		/**
		 * Remove an {@link HitboxAnimationController} from this registrar by name
		 * <p>
		 * This is mostly only useful if you're sub-classing an existing animatable object and want to modify the super list
		 */
		public ControllerRegistrar remove(String name) {
			this.controllers().removeIf(controller -> controller.getName().equals(name));

			return this;
		}

		@ApiStatus.Internal
		private <T extends AdvancedHitboxEntity> Object2ObjectArrayMap<String, HitboxAnimationController<T>> build() {
			Object2ObjectArrayMap<String, HitboxAnimationController<?>> map = new Object2ObjectArrayMap<>(this.controllers().size());

			this.controllers().forEach(controller -> map.put(controller.getName(), controller));

			return (Object2ObjectArrayMap)map;
		}
	}
}
