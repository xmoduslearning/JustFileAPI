package com.xmodus.JustFileAPI;

import com.xmodus.JustFileAPI.network.PacketHandler;
import com.xmodus.JustFileAPI.setup.ModSetup;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(JustFileAPI.MODID)
public class JustFileAPI
{
    public static final String MODID = "justfileapi";

    public static ModSetup modSetup = new ModSetup();

    public static MinecraftServer mcServer;

    // API server thread
    private static Thread _apiServerThread = null;
    private static APIServer _apiServer = null;
    
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public JustFileAPI() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // load our config
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("justfileapi-common.toml"));

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info(JustFileAPI.MODID + " setup");

        PacketHandler.register();
        modSetup.init();
    }

    public void Test() {
        LOGGER.info("JustFileAPI - Test method thread's name: " + Thread.currentThread().toString());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starting
        LOGGER.info(JustFileAPI.MODID + " onServerStarting");

        mcServer = event.getServer();

        // start API server thread
        try {

            this._apiServer = new APIServer(event.getServer(), this);
            this._apiServerThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LOGGER.info("APIServer listening...");
                            _apiServer.listen();
                        }
                        catch (Exception ex) {
                            LOGGER.error("APIServer error " + ex.toString());
                        }
                        finally {
                            if (_apiServer != null) {
                                LOGGER.info("APIServer closing...");
                                _apiServer.close();
                            }
                        }
                    }
                }
            );

            this._apiServerThread.start();
            // giving our thread a name so we can identify it amongst the sea of threads
            this._apiServerThread.setName("JustFileAPI thread");
        }
        catch (IOException ex) {
            LOGGER.error("ERROR: Error starting network server " + ex.toString());
        }
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {

        // do something when the server stopping
        LOGGER.info(JustFileAPI.MODID + " onServerStopping");

        if (this._apiServer != null) {
            this._apiServer.close();
        }
    }
}
