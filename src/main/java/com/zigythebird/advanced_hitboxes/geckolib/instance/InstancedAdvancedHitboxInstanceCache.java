package com.zigythebird.advanced_hitboxes.geckolib.instance;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.geckolib.animation.AnimatableManager;
import com.zigythebird.advanced_hitboxes.geckolib.util.HitboxModelUtil;

/**
 * AnimatableInstanceCache implementation for instantiated objects such as Entities or BlockEntities. Returns a single {@link AnimatableManager} instance per cache
 * <p>
 * You should <b><u>NOT</u></b> be instantiating this directly unless you know what you are doing.
 * Use {@link HitboxModelUtil#createInstanceCache GeckoLibUtil.createInstanceCache} instead
 */
public class InstancedAdvancedHitboxInstanceCache extends AdvancedHitboxInstanceCache {
	protected AnimatableManager<?> manager;

	public InstancedAdvancedHitboxInstanceCache(AdvancedHitboxEntity animatable) {
		super(animatable);
	}

	/**
	 * Gets the {@link AnimatableManager} instance from this cache
	 * <p>
	 * Because this cache subclass expects a 1:1 relationship of cache to animatable, only one {@code AnimatableManager} instance is used
	 */
	@Override
	public AnimatableManager<?> getManagerForId(long uniqueId) {
		if (this.manager == null)
			this.manager = new AnimatableManager<>(this.animatable);

		return this.manager;
	}
}