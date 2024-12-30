package com.zigythebird.advanced_hitboxes.client.models;

import com.zigythebird.advanced_hitboxes.AdvancedHitboxesMod;
import com.zigythebird.advanced_hitboxes.entity.TransformerEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TransformerModel extends DefaultedEntityGeoModel<TransformerEntity> {
    public TransformerModel() {
        super(AdvancedHitboxesMod.id("transformer"));
    }
}
