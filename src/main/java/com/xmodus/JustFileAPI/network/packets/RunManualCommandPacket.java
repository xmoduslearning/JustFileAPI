package com.xmodus.JustFileAPI.network.packets;

import com.xmodus.JustFileAPI.APIServer;
import com.xmodus.JustFileAPI.JustFileAPI;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

// source: https://www.minecraftforge.net/forum/topic/71901-1142-solved-how-do-i-send-packets-to-the-client/

public class RunManualCommandPacket {

    private static final Logger LOGGER = LogManager.getLogger();

    // packet data 'fields'
    private String commands_filename;
    private String results_filename;

    public RunManualCommandPacket(PacketBuffer buf) {
        LOGGER.info("@@RunManualCommandPacket@@::constructor(PacketBuffer) called");
        LOGGER.info("thread's name: " + Thread.currentThread().toString());
        commands_filename = buf.readString();
        results_filename = buf.readString();
    }

    public RunManualCommandPacket(String new_commands_filename, String new_results_filename) {
        LOGGER.info("@@RunManualCommandPacket@@::constructor(new_data) called");
        LOGGER.info("thread's name: " + Thread.currentThread().toString());
        commands_filename = new_commands_filename;
        results_filename = new_results_filename;
    }

    public void encode(PacketBuffer buf) {
        LOGGER.info("@@RunManualCommandPacket@@::encode called");
        LOGGER.info("thread's name: " + Thread.currentThread().toString());
        buf.writeString(commands_filename);
        buf.writeString(results_filename);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        LOGGER.info("@@RunManualCommandPacket@@::handle called");
        LOGGER.info("@@RunManualCommandPacket@@::commands_filename: " + commands_filename);
        LOGGER.info("@@RunManualCommandPacket@@::results_filename: " + results_filename);
        LOGGER.info("thread's name: " + Thread.currentThread().toString());

        File command_file = new File(commands_filename);

        if (command_file.exists()) {
            try {
                World world = JustFileAPI.mcServer.getWorld(DimensionType.OVERWORLD);
                APIServer apiServer = new APIServer(JustFileAPI.mcServer, null);    // send null or the packet will be sent again!
                apiServer.processCommandFile(world, commands_filename, results_filename);
            } catch (IOException e) {
                LOGGER.error(e.toString());
            } catch (Exception ex) {
                LOGGER.error(ex.toString());
            }
        } else {
            LOGGER.warn("No command file found to process, filename provided: " + commands_filename);
        }
    }
}
