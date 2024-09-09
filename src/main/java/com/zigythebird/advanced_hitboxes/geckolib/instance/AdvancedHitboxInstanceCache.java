package com.zigythebird.advanced_hitboxes.geckolib.instance;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.AnimatableManager;
import com.zigythebird.advanced_hitboxes.geckolib.constant.dataticket.DataTicket;

/**
 * The base cache class responsible for returning the {@link AnimatableManager} for a given instanceof of a {@link AdvancedHitboxEntity}
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
	 * pass their position or int ID. They typically only have one {@link AnimatableManager} per cache anyway
	 *
	 * @param uniqueId A unique ID. For every ID the same animation manager
	 *                 will be returned.
	 */
	public abstract <T extends AdvancedHitboxEntity> AnimatableManager<T> getManagerForId(long uniqueId);

	/**
	 * Helper method to set a data point in the {@link AnimatableManager#setData manager} for this animatable
	 *
	 * @param uniqueId The unique identifier for this animatable instance
	 * @param dataTicket The DataTicket for the data
	 * @param data The data to store
	 */
	public <D> void addDataPoint(long uniqueId, DataTicket<D> dataTicket, D data) {
		getManagerForId(uniqueId).setData(dataTicket, data);
	}

	/**
	 * Helper method to get a data point from the {@link AnimatableManager#getData data collection} for this animatable
	 *
	 * @param uniqueId The unique identifier for this animatable instance
	 * @param dataTicket The DataTicket for the data
	 */
	public <D> D getDataPoint(long uniqueId, DataTicket<D> dataTicket) {
		return getManagerForId(uniqueId).getData(dataTicket);
	}
}
