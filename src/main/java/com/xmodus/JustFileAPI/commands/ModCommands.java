package com.xmodus.JustFileAPI.commands;

import com.xmodus.JustFileAPI.JustFileAPI;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModCommands {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {

        LOGGER.debug("[ModCommands:register:event.JustFileAPI.MODID: " + JustFileAPI.MODID);

        /* 2020-01-06 - ck - for some reason we are getting a crash when this mod is
                             made into a jar?
        */
        LiteralCommandNode<CommandSource> cmdPy = dispatcher.register(
                Commands.literal(JustFileAPI.MODID)
                        .then(CommandRunCommands.register(dispatcher))
                        .then(TestForArmor.register(dispatcher))
                        .then(TestForBoots.register(dispatcher))
                        .then(TestForHelmet.register(dispatcher))
                        .then(TestForPants.register(dispatcher))
                        .then(TestForSword.register(dispatcher))
        );

        // currently unused "py" command - this will be the future cmd name (when the above is working!)
        // dispatcher.register(Commands.literal("py").redirect(cmdPy));
    }

}