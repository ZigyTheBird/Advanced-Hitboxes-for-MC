package com.zigythebird.advanced_hitboxes.item;

import com.zigythebird.advanced_hitboxes.phys.AdvancedEntityHitResult;
import com.zigythebird.advanced_hitboxes.utils.AdvancedRaytraceUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class DebugBowItem extends Item {
    public DebugBowItem(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        EntityHitResult entityHitResult = AdvancedRaytraceUtils.raytrace(player, 60);
        StringBuilder message = new StringBuilder();
        if (level.isClientSide) {
            message.append("CLIENT-SIDE RESULT:");
        }
        else {
            message.append("SERVER-SIDE RESULT:");
        }

        if (entityHitResult.getEntity() == null) {
            message.append("\nRaycast did not hit an entity within a 60 block distance.");
        }
        else {
            Entity entity = entityHitResult.getEntity();
            message.append("\nHit entity's type: " + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
            if (entity instanceof LivingEntity && entity.hasCustomName()) {
                message.append("\nHit entity's custom name: " + entity.getCustomName().getString());
            }
            if (entityHitResult instanceof AdvancedEntityHitResult advancedEntityHitResult) {
                Vec3 hitPosition = advancedEntityHitResult.getHitPosition();
                double d = advancedEntityHitResult.getDistance();
                String name = advancedEntityHitResult.getHitboxName();

                message.append("\nDistance hit from: " + Math.round(Math.sqrt(d) * 1000) / 1000F + " (Rounded to three decimal places)");
                message.append("\nPosition hit at: (" + Math.round(hitPosition.x*1000)/1000F + ", " + Math.round(hitPosition.y*1000)/1000F + ", " + Math.round(hitPosition.z*1000)/1000F + ")");
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
