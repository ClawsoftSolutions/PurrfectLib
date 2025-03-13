package com.github.clawsoftsolutions.purrfectlib.impl.content.command;

import com.github.clawsoftsolutions.purrfectlib.PurrfectLib;
import com.github.clawsoftsolutions.purrfectlib.prefab.command.PrefabCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.ItemStack;

public class BaseCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("purrfectlib")
                                .then(Commands.literal("debug_hammer")
                                        .executes(BaseCommands::debug_hammer)
                                )
        );
    }

    private static int debug_hammer(CommandContext<CommandSourceStack> source) {
        CommandSourceStack commandSource = source.getSource();
        if (commandSource.getPlayer() != null) {
            commandSource.getPlayer().addItem(new ItemStack(PurrfectLib.DEBUG_HAMMER.get()));
        }
        return 1;
    }

}
