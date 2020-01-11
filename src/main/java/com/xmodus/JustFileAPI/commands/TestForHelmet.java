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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestForHelmet implements Command<CommandSource> {

    private static final TestForHelmet CMD = new TestForHelmet();

    public static final Logger LOGGER = LogManager.getLogger();

    // TODO: modify so we can run a named command w/ a named results file
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("TestForHelmet")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {

        // test for "Co-op Helmet" then TP to x, y, z
        String item_name = Config.IG_HAS_HELMET_ITEM.get();
        // source position
        int sx = Config.IG_HAS_HELMET_SX.get();
        int sy = Config.IG_HAS_HELMET_SY.get();
        int sz = Config.IG_HAS_HELMET_SZ.get();
        // dest position
        int dx = Config.IG_HAS_HELMET_DX.get();
        int dy = Config.IG_HAS_HELMET_DY.get();
        int dz = Config.IG_HAS_HELMET_DZ.get();

        LOGGER.info("[TestForHelmet] testing for "
                + item_name
                + " at:"
                + " " + sx
                + " " + sy
                + " " + sz);

        MinecraftServer minecraftServer = context.getSource().getServer();

        PlayerList players = minecraftServer.getPlayerList();

        for (ServerPlayerEntity p : players.getPlayers()) {

            // find all players at source_pos
            int pos_diff = 0;

            pos_diff += Math.abs((int)p.getPositionVec().x - sx);
            pos_diff += Math.abs((int)p.getPositionVec().y - sy);
            pos_diff += Math.abs((int)p.getPositionVec().z - sz);

            if (pos_diff < 2) {

                LOGGER.info("player " + p.getScoreboardName() + " at source position");

                // then open their inventory and look for an item named "item_name" (ignore case)
                PlayerInventory pi = p.inventory;
                boolean has_item = false;
                for (int i = 0; i < pi.mainInventory.size(); i++) {
                    NonNullList<ItemStack> items = pi.mainInventory;
                    ItemStack item = items.get(i);

                    // get NBT/TileEntity info
                    CompoundNBT cnbt = item.getTag();
                    String nbt_str = "";

                    if (cnbt != null) {
                        nbt_str = cnbt.toString();
                    }

                    String item_str = item.toString() + " " + nbt_str;

                    if (item_str.toLowerCase().contains(item_name.toLowerCase())) {
                        // then move all matching players to dest_pos
                        LOGGER.info("player has item " + item_name + " in " + item_str);
                        has_item = true;
                        p.setPositionAndUpdate(dx, dy, dz);
                    }
                }

                if (!has_item) {
                    LOGGER.info("player " + p.getScoreboardName() + " does not have item named " + item_name);
                }

            } else {
                LOGGER.info("player " + p.getScoreboardName() + " not at source position");
            }

        }

        return 0;
    }
}