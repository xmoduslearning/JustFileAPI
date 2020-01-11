package com.xmodus.JustFileAPI.network;

import com.xmodus.JustFileAPI.JustFileAPI;
import com.xmodus.JustFileAPI.network.packets.RunManualCommandPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PacketHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(JustFileAPI.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void register() {
        int disc = 0;
        HANDLER.registerMessage(disc++,
                RunManualCommandPacket.class,
                RunManualCommandPacket::encode,
                RunManualCommandPacket::new,
                RunManualCommandPacket::handle);
    }

    /**
     * Sends a packet to the server.<br>
     * Must be called Client side.
     */
    public static void sendToServer_RunManualCommandPacket(RunManualCommandPacket msg)
    {
        LOGGER.info("[PacketHandler] sending message to server using sendToServer_RunManualCommandPacket");
        HANDLER.sendToServer(msg);
    }
}
