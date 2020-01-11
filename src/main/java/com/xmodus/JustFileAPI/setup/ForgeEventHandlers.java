package com.xmodus.JustFileAPI.setup;

import com.xmodus.JustFileAPI.commands.ModCommands;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeEventHandlers {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void serverLoad(FMLServerStartingEvent event) {

        // 2020-01-06 - ck - removed this to see if Forge is crashing only here
        LOGGER.debug("[ForgeEventHandlers:serverLoad:event.getCommandDispatcher: " + event.getCommandDispatcher().toString());

        ModCommands.register(event.getCommandDispatcher());
    }

}
