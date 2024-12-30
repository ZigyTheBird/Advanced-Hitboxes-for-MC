package com.zigythebird.advanced_hitboxes.item;

import com.zigythebird.advanced_hitboxes.entity.AdvancedHitboxEntity;
import com.zigythebird.advanced_hitboxes.mixin.accessors.LevelAccessor;
import com.zigythebird.advanced_hitboxes.phys.AdvancedHitbox;
import com.zigythebird.advanced_hitboxes.utils.HitboxUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DebugBowItem extends Item {
    public DebugBowItem(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        double d = Double.MAX_VALUE;
        Entity entity = null;
        String name = null;
        Vec3 hitPosition = null;

        Vec3 startVec = player.getEyePosition();
        Vec3 endVec = player.getEyePosition().add(Vec3.directionFromRotation(player.getXRot(), player.getYRot()).scale(60));
        final List<Entity> entities = new ArrayList<>();

        ((LevelAccessor)level).callGetEntities().get(EntityTypeTest.forClass(Entity.class), AbortableIterationConsumer.forConsumer(entities::add));
        for (Entity entity2 : entities) {
            if (entity2 == player)
                continue;
            if (entity2 instanceof AdvancedHitboxEntity || entity2 instanceof Player) {
                HitboxUtils.tickAndUpdateHitboxesForEntity((AdvancedHitboxEntity) entity2);
                for (AdvancedHitbox hitbox : ((AdvancedHitboxEntity) entity2).getHitboxes()) {
                    double e;
                    Optional<Vec3> optional = hitbox.linetest(startVec, endVec);
                    if (optional.isEmpty() || !((e = startVec.distanceToSqr(optional.get())) < d)) continue;
                    entity = entity2;
                    d = e;
                    name = hitbox.getName();
                    hitPosition = optional.get();
                }
            }
            else {
                double e;
                AABB aABB = entity2.getBoundingBox().inflate(0.4);
                Optional<Vec3> optional = aABB.clip(startVec, endVec);
                if (optional.isEmpty() || !((e = startVec.distanceToSqr(optional.get())) < d)) continue;
                entity = entity2;
                d = e;
                hitPosition = optional.get();
            }
        }

        StringBuilder message = new StringBuilder();
        if (level.isClientSide) {
            message.append("CLIENT-SIDE RESULT:");
        }
        else {
            message.append("SERVER-SIDE RESULT:");
        }

        if (entity == null) {
            message.append("\nRaycast did not hit an entity within a 60 block distance.");
        }
        else {
            message.append("\nHit entity's type: " + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
            if (entity instanceof LivingEntity && entity.hasCustomName()) {
                message.append("\nHit entity's custom name: " + entity.getCustomName().getString());
            }
            message.append("\nDistance hit from: " + Math.round(Math.sqrt(d) * 1000) / 1000F + " (Rounded to three decimal places)");
            message.append("\nPosition hit at: (" + Math.round(hitPosition.x*1000)/1000F + ", " + Math.round(hitPosition.y*1000)/1000F + ", " + Math.round(hitPosition.z*1000)/1000F + ")");
            if (entity instanceof AdvancedHitboxEntity) {
                message.append("\nAdvanced hitbox name: " + name);
            }
            else {
                message.append("\nEntity hit was not an advanced hitbox entity.");
            }
        }

        player.sendSystemMessage(Component.literal(message.toString()).withStyle(ChatFormatting.GREEN));
        return InteractionResultHolder.success(itemStack);
    }
}
