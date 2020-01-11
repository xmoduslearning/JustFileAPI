package com.xmodus.JustFileAPI;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {

    // config elements
    public static ForgeConfigSpec.ConfigValue<String> COMMANDS_FILENAME;
    public static ForgeConfigSpec.ConfigValue<String> RESULTS_FILENAME;

    //
    // Ice Giant variables
    //

    // co-op armor
    public static ForgeConfigSpec.ConfigValue<String> IG_HAS_ARMOR_ITEM;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_ARMOR_SX;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_ARMOR_SY;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_ARMOR_SZ;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_ARMOR_DX;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_ARMOR_DY;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_ARMOR_DZ;

    // co-op boots
    public static ForgeConfigSpec.ConfigValue<String> IG_HAS_BOOTS_ITEM;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_BOOTS_SX;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_BOOTS_SY;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_BOOTS_SZ;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_BOOTS_DX;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_BOOTS_DY;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_BOOTS_DZ;

    // co-op helmet
    public static ForgeConfigSpec.ConfigValue<String> IG_HAS_HELMET_ITEM;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_HELMET_SX;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_HELMET_SY;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_HELMET_SZ;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_HELMET_DX;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_HELMET_DY;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_HELMET_DZ;

    // co-op pants
    public static ForgeConfigSpec.ConfigValue<String> IG_HAS_PANTS_ITEM;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_PANTS_SX;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_PANTS_SY;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_PANTS_SZ;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_PANTS_DX;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_PANTS_DY;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_PANTS_DZ;

    // co-op sword
    public static ForgeConfigSpec.ConfigValue<String> IG_HAS_SWORD_ITEM;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_SWORD_SX;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_SWORD_SY;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_SWORD_SZ;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_SWORD_DX;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_SWORD_DY;
    public static ForgeConfigSpec.ConfigValue<Integer> IG_HAS_SWORD_DZ;

    public static final Logger LOGGER = LogManager.getLogger();

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec COMMON_CONFIG;

    static {

        COMMANDS_FILENAME = COMMON_BUILDER.comment("filename for file containing API command(s)")
                .define("COMMANDS_FILENAME", "commands.txt");

        RESULTS_FILENAME = COMMON_BUILDER.comment("filename for results of commands")
                .define("RESULTS_FILENAME", "results.txt");

        // hacked in item names and locations for Ice Giant world

        // armor
        IG_HAS_ARMOR_ITEM = COMMON_BUILDER.comment("name of Ice Giant armor")
                .define("IG_HAS_ARMOR_ITEM", "Co-op Armor");
        IG_HAS_ARMOR_SX = COMMON_BUILDER.comment("source x-position to test for armor")
                .define("IG_HAS_ARMOR_SX", 65);
        IG_HAS_ARMOR_SY = COMMON_BUILDER.comment("source y-position to test for armor")
                .define("IG_HAS_ARMOR_SY", 70);
        IG_HAS_ARMOR_SZ = COMMON_BUILDER.comment("source z-position to test for armor")
                .define("IG_HAS_ARMOR_SZ", 243);
        IG_HAS_ARMOR_DX = COMMON_BUILDER.comment("dest. x-position to test for armor")
                .define("IG_HAS_ARMOR_DX", 67);
        IG_HAS_ARMOR_DY = COMMON_BUILDER.comment("dest. y-position to test for armor")
                .define("IG_HAS_ARMOR_DY", 70);
        IG_HAS_ARMOR_DZ = COMMON_BUILDER.comment("dest. z-position to test for armor")
                .define("IG_HAS_ARMOR_DZ", 239);

        // boots
        IG_HAS_BOOTS_ITEM = COMMON_BUILDER.comment("name of Ice Giant boots")
                .define("IG_HAS_BOOTS_ITEM", "Co-op Boots");
        IG_HAS_BOOTS_SX = COMMON_BUILDER.comment("source x-position to test for boots")
                .define("IG_HAS_BOOTS_SX", 66);
        IG_HAS_BOOTS_SY = COMMON_BUILDER.comment("source y-position to test for boots")
                .define("IG_HAS_BOOTS_SY", 70);
        IG_HAS_BOOTS_SZ = COMMON_BUILDER.comment("source z-position to test for boots")
                .define("IG_HAS_BOOTS_SZ", 243);
        IG_HAS_BOOTS_DX = COMMON_BUILDER.comment("dest. x-position to test for boots")
                .define("IG_HAS_BOOTS_DX", 68);
        IG_HAS_BOOTS_DY = COMMON_BUILDER.comment("dest. y-position to test for boots")
                .define("IG_HAS_BOOTS_DY", 70);
        IG_HAS_BOOTS_DZ = COMMON_BUILDER.comment("dest. z-position to test for boots")
                .define("IG_HAS_BOOTS_DZ", 239);

        // helmet
        IG_HAS_HELMET_ITEM = COMMON_BUILDER.comment("name of Ice Giant helmet")
                .define("IG_HAS_HELMET_ITEM", "Co-op Helmet");
        IG_HAS_HELMET_SX = COMMON_BUILDER.comment("source x-position to test for helmet")
                .define("IG_HAS_HELMET_SX", 67);
        IG_HAS_HELMET_SY = COMMON_BUILDER.comment("source y-position to test for helmet")
                .define("IG_HAS_HELMET_SY", 70);
        IG_HAS_HELMET_SZ = COMMON_BUILDER.comment("source z-position to test for helmet")
                .define("IG_HAS_HELMET_SZ", 243);
        IG_HAS_HELMET_DX = COMMON_BUILDER.comment("dest. x-position to test for helmet")
                .define("IG_HAS_HELMET_DX", 69);
        IG_HAS_HELMET_DY = COMMON_BUILDER.comment("dest. y-position to test for helmet")
                .define("IG_HAS_HELMET_DY", 70);
        IG_HAS_HELMET_DZ = COMMON_BUILDER.comment("dest. z-position to test for helmet")
                .define("IG_HAS_HELMET_DZ", 239);

        // pants
        IG_HAS_PANTS_ITEM = COMMON_BUILDER.comment("name of Ice Giant pants")
                .define("IG_HAS_PANTS_ITEM", "Co-op Pants");
        IG_HAS_PANTS_SX = COMMON_BUILDER.comment("source x-position to test for pants")
                .define("IG_HAS_PANTS_SX", 69);
        IG_HAS_PANTS_SY = COMMON_BUILDER.comment("source y-position to test for pants")
                .define("IG_HAS_PANTS_SY", 70);
        IG_HAS_PANTS_SZ = COMMON_BUILDER.comment("source z-position to test for pants")
                .define("IG_HAS_PANTS_SZ", 243);
        IG_HAS_PANTS_DX = COMMON_BUILDER.comment("dest. x-position to test for pants")
                .define("IG_HAS_PANTS_DX", 71);
        IG_HAS_PANTS_DY = COMMON_BUILDER.comment("dest. y-position to test for pants")
                .define("IG_HAS_PANTS_DY", 70);
        IG_HAS_PANTS_DZ = COMMON_BUILDER.comment("dest. z-position to test for pants")
                .define("IG_HAS_PANTS_DZ", 239);

        // sword
        IG_HAS_SWORD_ITEM = COMMON_BUILDER.comment("name of Ice Giant sword")
                .define("IG_HAS_SWORD_ITEM", "Co-op Sword");
        IG_HAS_SWORD_SX = COMMON_BUILDER.comment("source x-position to test for sword")
                .define("IG_HAS_SWORD_SX", 70);
        IG_HAS_SWORD_SY = COMMON_BUILDER.comment("source y-position to test for sword")
                .define("IG_HAS_SWORD_SY", 70);
        IG_HAS_SWORD_SZ = COMMON_BUILDER.comment("source z-position to test for sword")
                .define("IG_HAS_SWORD_SZ", 243);
        IG_HAS_SWORD_DX = COMMON_BUILDER.comment("dest. x-position to test for sword")
                .define("IG_HAS_SWORD_DX", 72);
        IG_HAS_SWORD_DY = COMMON_BUILDER.comment("dest. y-position to test for sword")
                .define("IG_HAS_SWORD_DY", 70);
        IG_HAS_SWORD_DZ = COMMON_BUILDER.comment("dest. z-position to test for sword")
                .define("IG_HAS_SWORD_DZ", 239);

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        LOGGER.info("[loadConfig] loading mod configuration, from: "  + path.toString());

        // TODO: review, do we want this to auto-save - first-time, yes, otherwise, no...
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        LOGGER.info("[onLoad] loading mod configuration");
        LOGGER.info("COMMANDS_FILENAME: " + COMMANDS_FILENAME.get());
        LOGGER.info("RESULTS_FILENAME: " + RESULTS_FILENAME.get());
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.ConfigReloading configEvent) {
    }
}
