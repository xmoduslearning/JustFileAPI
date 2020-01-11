package com.xmodus.JustFileAPI.setup;

// source: https://github.com/McJty/YouTubeModding14/blob/master/src/main/java/com/mcjty/mytutorial/setup/ModSetup.java

import net.minecraftforge.common.MinecraftForge;

public class ModSetup {

    public void init() {
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());

    }

}
