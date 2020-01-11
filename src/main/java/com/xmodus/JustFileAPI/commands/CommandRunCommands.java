package com.xmodus.JustFileAPI.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xmodus.JustFileAPI.APIServer;
import com.xmodus.JustFileAPI.Config;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class CommandRunCommands implements Command<CommandSource> {

    private static final CommandRunCommands CMD = new CommandRunCommands();

    public static final Logger LOGGER = LogManager.getLogger();

    // TODO: modify so we can run a named command w/ a named results file
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("runcommands")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {

        LOGGER.info("[CommandRunCommands] running commands file, if it exists");

        // NOTE: we prepend the word "manual_" to these file names
        //       because if we don't and we drop a commands.txt file
        //       the API thread will find it first and run it, instead of use :(
        String commands_filename = Config.COMMANDS_FILENAME.get();
        commands_filename = "manual_" + commands_filename;

        String results_filename = Config.RESULTS_FILENAME.get();
        results_filename = "manual_" + results_filename;

        File command_file = new File(commands_filename);

        if (command_file.exists()) {
            try {
                ServerPlayerEntity player = context.getSource().asPlayer();
                World world = player.getServerWorld();
                APIServer apiServer = new APIServer(player.getServer(), null);
                apiServer.processCommandFile(world, commands_filename, results_filename);
            } catch (IOException e) {
                LOGGER.error(e.toString());
            } catch (Exception ex) {
                LOGGER.error(ex.toString());
            }
        } else {
            LOGGER.warn("No command file found to process.");
        }

        return 0;
    }
}