package com.zigythebird.advanced_hitboxes.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.zigythebird.advanced_hitboxes.entity.TransformerEntity;
import com.zigythebird.advanced_hitboxes.registry.ModEntities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class TransformCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("optimusprime").executes((command) -> execute(command)));
    }
    private static int execute(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player){
            Player player = (Player) command.getSource().getEntity();
            TransformerEntity entity = new TransformerEntity(ModEntities.TRANSFORMER.get(), player.level());
            entity.setPos(player.position());
            player.level().addFreshEntity(entity);
            player.startRiding(entity);
        }
        return Command.SINGLE_SUCCESS;
    }
}
