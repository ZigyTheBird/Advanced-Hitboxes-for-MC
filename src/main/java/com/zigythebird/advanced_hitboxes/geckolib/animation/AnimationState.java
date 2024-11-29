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

import com.zigythebird.advanced_hitboxes.interfaces.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.constant.dataticket.DataTicket;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.Objects;

/**
 * Animation state handler for end-users
 * <p>
 * This is where users would set their selected animation to play,
 * stop the controller, or any number of other animation-related actions.
 */
public class AnimationState<T extends AdvancedHitboxEntity> {
	private final T animatable;
	private final float limbSwing;
	private final float limbSwingAmount;
	private final float partialTick;
	private final boolean isMoving;
	private final Map<DataTicket<?>, Object> extraData = new Object2ObjectOpenHashMap<>();

	protected HitboxAnimationController<T> controller;
	public double animationTick;

	public AnimationState(T animatable, float limbSwing, float limbSwingAmount, float partialTick, boolean isMoving) {
		this.animatable = animatable;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.partialTick = partialTick;
		this.isMoving = isMoving;
	}

	/**
	 * Gets the amount of ticks that have passed in either the current transition or
	 * animation, depending on the controller's AnimationState.
	 */
	public double getAnimationTick() {
		return this.animationTick;
	}

	/**
	 * Gets the current {@link AdvancedHitboxEntity} being rendered
	 */
	public T getAnimatable() {
		return this.animatable;
	}

	public float getLimbSwing() {
		return this.limbSwing;
	}

	public float getLimbSwingAmount() {
		return this.limbSwingAmount;
	}

	/**
	 * Gets the fractional value of the current game tick that has passed in rendering
	 */
	public float getPartialTick() {
		return this.partialTick;
	}

	/**
	 * Gets whether the current {@link AdvancedHitboxEntity} is considered to be moving for animation purposes
	 * <p>
	 * Note that this is a best-case approximation of movement, and your needs may vary.
	 */
	public boolean isMoving() {
		return this.isMoving;
	}

	/**
	 * Gets the current {@link HitboxAnimationController} responsible for the current animation
	 */
	public HitboxAnimationController<T> getController() {
		return this.controller;
	}

	/**
	 * Sets the {@code AnimationState}'s current {@link HitboxAnimationController}
	 */
	public AnimationState<T> withController(HitboxAnimationController<T> controller) {
		this.controller = controller;

		return this;
	}

	/**
	 * Gets the optional additional data map for the state
	 *
	 * @see DataTicket
	 */
	public Map<DataTicket<?>, ?> getExtraData() {
		return this.extraData;
	}

	/**
	 * Get a data value saved to this animation state by the ticket for that data
	 *
	 * @see DataTicket
	 * @param dataTicket The {@link DataTicket} for the data to retrieve
	 * @return The cached data for the given {@code DataTicket}, or null if not saved
	 */
	public <D> D getData(DataTicket<D> dataTicket) {
		return dataTicket.getData(this.extraData);
	}

	/**
	 * Save a data value for the given {@link DataTicket} in the additional data map
	 *
	 * @param dataTicket The {@code DataTicket} for the data value
	 * @param data The data value
	 */
	public <D> void setData(DataTicket<D> dataTicket, D data) {
		this.extraData.put(dataTicket, data);
	}

	/**
	 * Sets the animation for the controller to start/continue playing
	 * <p>
	 * Basically just a shortcut for <pre>{@code getController().setAnimation()}</pre>
	 *
	 * @param animation The animation to play
	 */
	public void setAnimation(RawAnimation animation) {
		getController().setAnimation(animation);
	}

	/**
	 * Helper method to set an animation to start/continue playing, and return {@link PlayState#CONTINUE}
	 */
	public PlayState setAndContinue(RawAnimation animation) {
		getController().setAnimation(animation);

		return PlayState.CONTINUE;
	}

	/**
	 * Checks whether the current {@link HitboxAnimationController}'s last animation was the one provided
	 * <p>
	 * This allows for multi-stage animation shifting where the next animation to play may depend on the previous one
	 *
	 * @param animation The animation to check
	 * @return Whether the controller's last animation is the one provided
	 */
	public boolean isCurrentAnimation(RawAnimation animation) {
		return Objects.equals(getController().currentRawAnimation, animation);
	}

	/**
	 * Similar to {@link AnimationState#isCurrentAnimation}, but additionally checks the current stage of the animation by name
	 * <p>
	 * This can be used to check if a multi-stage animation has reached a given stage (if it is running at all)
	 * <p>
	 * Note that this will still return true even if the animation has finished, matching with the last animation stage in the {@link RawAnimation} last provided
	 *
	 * @param name The name of the animation stage to check (I.E. "move.walk")
	 * @return Whether the controller's current stage is the one provided
	 */
	public boolean isCurrentAnimationStage(String name) {
		return getController().getCurrentAnimation() != null && getController().getCurrentAnimation().animation().name().equals(name);
	}

	/**
	 * Helper method for {@link HitboxAnimationController#forceAnimationReset()}
	 * <p>
	 * This should be used in controllers when stopping a non-looping animation, so that it is reset to the start for the next time it starts
	 */
	public void resetCurrentAnimation() {
		getController().forceAnimationReset();
	}

	/**
	 * Helper method for {@link HitboxAnimationController#setAnimationSpeed}
	 *
	 * @param speed The speed modifier for the controller (2 = twice as fast, 0.5 = half as fast, etc)
	 */
	public void setControllerSpeed(float speed) {
		getController().setAnimationSpeed(speed);
	}
}
