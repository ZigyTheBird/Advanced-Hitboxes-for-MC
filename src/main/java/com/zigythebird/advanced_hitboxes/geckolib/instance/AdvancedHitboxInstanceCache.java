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

package com.zigythebird.advanced_hitboxes.geckolib.instance;

import com.zigythebird.advanced_hitboxes.geckolib.animation.HitboxAnimatableManager;
import com.zigythebird.advanced_hitboxes.geckolib.constant.dataticket.DataTicket;
import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;

/**
 * The base cache class responsible for returning the {@link HitboxAnimatableManager} for a given instanceof of a {@link AdvancedHitboxEntity}
 */
public abstract class AdvancedHitboxInstanceCache {
	protected final AdvancedHitboxEntity animatable;

	public AdvancedHitboxInstanceCache(AdvancedHitboxEntity animatable) {
		this.animatable = animatable;
	}

	/**
	 * This creates or gets the cached animatable manager for any unique ID
	 * <p>
	 * For itemstacks, this is typically a reserved ID provided by GeckoLib. {@code Entities} and {@code BlockEntities}
	 * pass their position or int ID. They typically only have one {@link HitboxAnimatableManager} per cache anyway
	 *
	 * @param uniqueId A unique ID. For every ID the same animation manager
	 *                 will be returned.
	 */
	public abstract <T extends AdvancedHitboxEntity> HitboxAnimatableManager<T> getManagerForId(long uniqueId);

	/**
	 * Helper method to set a data point in the {@link HitboxAnimatableManager#setData manager} for this animatable
	 *
	 * @param uniqueId The unique identifier for this animatable instance
	 * @param dataTicket The DataTicket for the data
	 * @param data The data to store
	 */
	public <D> void addDataPoint(long uniqueId, DataTicket<D> dataTicket, D data) {
		getManagerForId(uniqueId).setData(dataTicket, data);
	}

	/**
	 * Helper method to get a data point from the {@link HitboxAnimatableManager#getData data collection} for this animatable
	 *
	 * @param uniqueId The unique identifier for this animatable instance
	 * @param dataTicket The DataTicket for the data
	 */
	public <D> D getDataPoint(long uniqueId, DataTicket<D> dataTicket) {
		return getManagerForId(uniqueId).getData(dataTicket);
	}
}
