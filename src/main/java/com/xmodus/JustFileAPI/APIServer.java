package com.xmodus.JustFileAPI;

import com.xmodus.JustFileAPI.network.PacketHandler;
import com.xmodus.JustFileAPI.network.packets.RunManualCommandPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class APIServer {

    private static final Logger LOGGER = LogManager.getLogger();

    private boolean listening;
    private MinecraftServer minecraftServer;

    // this is meant to allow
    public JustFileAPI mc_server_thread_handle = null;

    /*
     * creates a new APIServer
     *
     * @param mcServer  the Minecraft server that holds the world data
     */
    public APIServer(MinecraftServer mcServer, JustFileAPI parent_thread) throws IOException {

        // keep a link to the Minecraft server
        this.minecraftServer = mcServer;

        this.mc_server_thread_handle = parent_thread;

        // signal that we are listening
        this.listening = true;
    }

    public void close() {
        this.listening = false;
    }

    /*
     * are we still listening for connection and thus active?
     */
    public boolean isListening() {
        return this.listening;
    }

    /*
     * watch for a commands file
     */
    public void listen() {

        LOGGER.info("[APIServer] 'listening' for commands file");

        String commands_filename = Config.COMMANDS_FILENAME.get();
        String results_filename = Config.RESULTS_FILENAME.get();

        File command_file = new File(commands_filename);

        while (this.listening) {

            if (command_file.exists()) {
                try {
                    World world = this.minecraftServer.getWorld(DimensionType.OVERWORLD);
                    processCommandFile(world, commands_filename, results_filename);
                } catch (Exception ex) {
                    throw (ex);
                }
            } else {
                // sleep to avoid thrashing the OS?
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                    // ignore: if we can't sleep, we can't sleep...
                }
            }

        }
    }

    /*
     * attempts to parse the provided line and returns "" or error string
     *
     * @param line - line to parse
     */
    private String parseAndExecuteLine(World world, String line) {

        // skip blank lines or 'comment' lines
        if (line.length() == 0 || line.startsWith("#"))
            return "";

        // simple parsing, for now, just split by space
        String[] fields = line.split(" ");

        // we ignore case
        String command = fields[0].toLowerCase();

        String result = "";

        int x = 0;
        int y = 0;
        int z = 0;

        //
        // world.getBlock
        //
        if (command.equals("world.getblock")) {

            if (fields.length < 4) {
                result = "ERROR: world.getBlock requires three parameters: x, y, z; only received: " + Integer.toString(fields.length);
            } else {

                LOGGER.info("getting block at (" + fields[1] + "," + fields[2] + "," + fields[3] + ")");

                x = Integer.parseInt(fields[1]);
                y = Integer.parseInt(fields[2]);
                z = Integer.parseInt(fields[3]);

                BlockPos pos = new BlockPos(x, y, z);

                // TODO: get the WORLD (overworld, nether, etc)... for now we'll leave this in the overworld, need an override or param
                BlockState blockState = world.getBlockState(pos);
                Block block = blockState.getBlock();
                ResourceLocation blockResLoc = block.getRegistryName(); // TODO: handle possible NULL

                // should return the name of the block and any 'NBT' info
                String block_text = "Name:" + blockResLoc.toString();
                block_text += "|BlockState:" + blockState.toString();

                if (!block.hasTileEntity(blockState)) {
                    block_text += "|TileEntity:none";
                } else {
                    TileEntity te = world.getTileEntity(pos);

                    if (te == null) {
                        block_text += "|TileEntity:null";
                    }
                    else {
                        block_text += "|TileEntity:" + te.toString();

                        if (te instanceof ChestTileEntity) {
                            ChestTileEntity cte = (ChestTileEntity) te;
                            block_text += "|Chest:isEmpty=" + Boolean.toString(cte.isEmpty());

                            if (!cte.isEmpty()) {
                                block_text += "|SizeInventory=" + Integer.toString(cte.getSizeInventory());

                                // list the inventory items, where not empty
                                for (int i = 0; i < cte.getSizeInventory(); i++) {
                                    ItemStack item = cte.getStackInSlot(i);

                                    if (item != null) {
                                        if (item.isEmpty()) {
                                            block_text += "[empty]";
                                        } else {
                                            // block_text += "[" + item.getItem().get + ":" + Integer.toString(item.getCount()) + "]";
                                            // block_text += "[" + item.toString() + ":" + Integer.toString(item.getCount()) + "]";
                                            block_text += "[" + item.toString() + "]";
                                        }
                                    }
                                }
                            }

                        } else if (te instanceof CommandBlockTileEntity) {
                            CommandBlockTileEntity cbe = (CommandBlockTileEntity) te;
                            block_text += "|CommandBlock:command=" + cbe.toString();
                            block_text += "|CommandBlock:mode=" + cbe.getMode().toString();
                            block_text += "|CommandBlock:type=" + cbe.getType().toString();

                            CommandBlockLogic cbl = cbe.getCommandBlockLogic();
                            block_text += "|CommandBlock:command=" + cbl.getCommand();
                        } else {
                            block_text += "|UnknownTileEntity";
                        }

                        CompoundNBT nbt = te.getTileData();
                        if (nbt != null) {
                            int nbt_size = nbt.size();
                            block_text += "|NBT:" + nbt.toString();
                            block_text +="(" + Integer.toString(nbt_size) + ")";
                        }
                        else {
                            block_text += "|NBT:null";
                        }

                        BlockPos bp = te.getPos();
                        if (bp != null) {
                            block_text += "|BlockPos:" + bp.toString();
                        } else {
                            block_text += "|BlockPos:null";
                        }
                    }
                }

                return block_text;
            }
        }

        //
        // world.setBlock <x> <y> <z> <name> <tags>
        //
        else if (command.equals("world.setblock")) {

            if (fields.length < 5) {
                result = "ERROR: world.setBlock requires three parameters: x, y, z, name, [tags]; only received: " + Integer.toString(fields.length);
            } else {

                x = Integer.parseInt(fields[1]);
                y = Integer.parseInt(fields[2]);
                z = Integer.parseInt(fields[3]);

                String name = fields[4];
                String tags = "";

                if (fields.length > 5) {
                    // join the rest of the fields to handle spaces
                    for (int i = 5; i < fields.length; i++) {
                        tags += fields[i] + " ";
                    }
                    tags = tags.trim();
                }

                LOGGER.info("placing block " + name + " at (" + fields[1] + "," + fields[2] + "," + fields[3] + "), tags:" + tags);

                Block new_block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));

                if (new_block == null) {
                    result = "ERROR: unknown block name: " + name;
                    // throw new IllegalStateException("ERROR: [APIServer] Attempt to create a block with an unknown id '" + name + "'!");
                    return result;
                }

                BlockPos new_block_pos = new BlockPos(x, y, z);
                BlockState new_block_state = new_block.getDefaultState();
                LOGGER.info("default block state: " + new_block_state.toString());

                // place the new block into the world!
                boolean set_block_result = world.setBlockState(new_block_pos, new_block_state);
                LOGGER.info("setBlockState, result: " + set_block_result);

                // custom tags
                if (name.toLowerCase().equals("minecraft:chest")) {

                    // place items into the newly placed block

                    LOGGER.info("TODO: custom chest handler");

                    ChestTileEntity cte = (ChestTileEntity)world.getTileEntity(new_block_pos);

                    if (cte == null) {
                        LOGGER.error("ERROR: placed new chest block, but block reports no TileEntity?");
                    } else {

                        // TODO: parse the tags for the slot details
                        // TODO: fix this broken 'property' issue... I can't just place 'blocks' into chests...

                        // testing by manually placing items into this new chest
                        Item.Properties new_item_properties = new Item.Properties();
                        Item new_item = new Item(new_item_properties);
                        String item_name_str = "minecraft:dirt";
                        new_item.setRegistryName(new ResourceLocation(item_name_str));
                        LOGGER.info("adding " + item_name_str + " got " + new_item.getName().toString());

                        // ItemStack new_item_stack = new ItemStack(new_item, 1);
                        Block test_block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(item_name_str));
                        ItemStack new_item_stack = new ItemStack(test_block.asItem(), 1);
                        cte.setInventorySlotContents(0, new_item_stack);

                        // ItemStack new_item_stack = new ItemStack(new_item, 1);
                        // HACK: testing if adding a 'block' as an item works... as the above does not?!
                        LOGGER.info("adding " + item_name_str + " got " + new_block.asItem().toString());
                        new_item_stack = new ItemStack(new_block.asItem(), 1);

                        cte.setInventorySlotContents(1, new_item_stack);

                        // DEBUG: verify the inventory...
                        // list the inventory items, where not empty
                        for (int i = 0; i < cte.getSizeInventory(); i++) {
                            ItemStack itemStack = cte.getStackInSlot(i);
                            if (!itemStack.isEmpty())
                                LOGGER.info("Chest item: " + Integer.toString(i) + " => " + itemStack.toString());
                        }
                    }
                }
                else if (name.toLowerCase().equals("minecraft:command_block")) {

                    LOGGER.info("TODO: custom command block handler");

                    CommandBlockTileEntity cbte = (CommandBlockTileEntity)world.getTileEntity(new_block_pos);

                    if (cbte == null) {
                        LOGGER.error("ERROR: placed new command block, but block reports no TileEntity?");
                    } else {
                    /*
                        // copying an in-game block for deciphering the NBT
                        BlockPos test_block_pos = new BlockPos(-63, 69, 241);
                        BlockState test_block_state = world.getBlockState(test_block_pos);
                        Block test_block = test_block_state.getBlock();

                        CompoundNBT test_nbt = new CompoundNBT();
                        test_nbt = cbte.write(test_nbt);
                        LOGGER.info("### test_nbt ###");
                        LOGGER.info(test_nbt.toString());
                    */

                    /*
                    // resulting NBT:
                    {
                        x auto:0b,
                        * ForgeData:{},
                        x powered:0b,
                        * LastExecution:29444L,
                        x SuccessCount:1,
                        x UpdateLastExecution:1b,
                        x conditionMet:1b,
                        x CustomName:'{"text":"@"}',
                        x Command:"/say hello from command block",
                        d x:-63,
                        d y:69,
                        d z:241,
                        x id:"minecraft:command_block",
                        x TrackOutput:1b
                    }

                    {
                        x conditionMet:0b,
                        x auto:0b,
                        x CustomName:'{"text":"@"}',
                        x powered:0b,
                        x Command:"/say hey, this modding stuff is fun!",
                        d x:0,
                        d y:0,
                        d z:0,
                        x id:"minecraft:command_block",
                        x SuccessCount:0,
                        x TrackOutput:1b,
                        x UpdateLastExecution:1b
                    }
                     */
                        // create the NBT
                        CompoundNBT test_nbt = new CompoundNBT();
                        String cb_command = tags;
                        LOGGER.info("command block custom command: " + cb_command);

                        // NOTE: these appear to be the minimum set of parameters for the command block to work
                        test_nbt.putByte("auto", (byte)0);
                        test_nbt.putInt("x", x);
                        test_nbt.putInt("y", y);
                        test_nbt.putInt("z", z);
                        test_nbt.putString("Command", cb_command);

                        LOGGER.info("### test_nbt: " + test_nbt.toString());

                        // read the new custom NBT into the TileEntity...
                        cbte.read(test_nbt);

                        CompoundNBT verify_nbt = new CompoundNBT();
                        verify_nbt = cbte.write(verify_nbt);
                        LOGGER.info("### verify_nbt ###");
                        LOGGER.info(verify_nbt.toString());
                    }
                }

                result = "block: " + name + " placed successfully at "
                        + Integer.toString(x)
                        + "," + Integer.toString(y)
                        + "," + Integer.toString(z)
                        + " with tags: " + tags;
            }
        }

        //
        // players.list
        //
        else if (command.equals("players.list")) {

            PlayerList players = this.minecraftServer.getPlayerList();
            String players_list = "\"players\":[";

            for (ServerPlayerEntity p : players.getPlayers()) {

                BlockPos bp = p.getPosition();
                String pos_text = bp.toString();

                players_list += "{";
                players_list += "\"name\":\"" + p.getScoreboardName() + "\",";
                players_list += "\"id\":\"" + p.getUniqueID().toString() + "\",";
                players_list += "\"x\":" + String.valueOf((int)bp.getX()) + ",";
                players_list += "\"y\":" + String.valueOf((int)bp.getY()) + ",";
                players_list += "\"z\":" + String.valueOf((int)bp.getZ()) + "";

                // get player inventory
                PlayerInventory pi = p.inventory;
                players_list += ", \"inventory\":[";
                for (int i = 0; i < pi.mainInventory.size(); i++) {
                    NonNullList<ItemStack> items = pi.mainInventory;
                    ItemStack item = items.get(i);

                    // get NBT/TileEntity info
                    CompoundNBT cnbt = item.getTag();
                    String nbt_str = "";

                    if (cnbt != null) {
                        nbt_str = cnbt.toString();
                    }

                    players_list += "(" + item.toString() + " " + nbt_str +  ")";
                }


                players_list += "]";

                players_list += "},";
            }
            players_list += "]";

            LOGGER.info("players_list: " + players_list);
            result = players_list;
        }

        //
        // players.setPos <target: [@a|name]> <x> <y> <z>
        //
        else if (command.equals("players.setpos")) {

            String target = fields[1].toLowerCase();
            // note: these are int's but setPosition uses doubles...
            x = Integer.parseInt(fields[2]);
            y = Integer.parseInt(fields[3]);
            z = Integer.parseInt(fields[4]);

            LOGGER.info("players.setPos " + target + " " + fields[2] + " " + fields[3] + " " + fields[4]);

            PlayerList players = this.minecraftServer.getPlayerList();

            for (ServerPlayerEntity p : players.getPlayers()) {
                if (target.equals("@a")
                || target.equals(p.getScoreboardName().toLowerCase())) {

                    LOGGER.info("moving player " + p.getScoreboardName()
                            + " from "
                            + Integer.toString(p.getPosition().getX()) + " "
                            + Integer.toString(p.getPosition().getY()) + " "
                            + Integer.toString(p.getPosition().getY()) + " "
                            + " to "
                            + Integer.toString(x) + " "
                            + Integer.toString(y) + " "
                            + Integer.toString(z)
                    );

                    p.setPositionAndUpdate(x, y, z);

                }
            }
        }

        //
        // title.title
        //
        // title.title selector [@a, name], "title-text", "sub-title", duration-seconds, fade-in-seconds, fade-out-seconds
        // NOTE: send "*" (or any single character for the sub-title for it to be blank/skipped)
        // NOTE: to send a title with spaces you MUST replace the spaces with underscores, which we will replace with spaces below otherwise the parameters won't parse correctly
        //
        else if (command.equals("title.title")) {

            String target = fields[1].toLowerCase();       // who
            String title_text = fields[2].replace('_', ' ');    // title
            String subtitle_text = fields[3].replace('_', ' '); // sub-title
            int display_duration = Integer.parseInt(fields[4]);
            int fade_in_seconds = Integer.parseInt(fields[5]);
            int fade_out_seconds = Integer.parseInt(fields[6]);

            TextComponent title_text_component = new StringTextComponent(title_text);

            // TODO: verify fadeIn, displayTime and fadeOut parameter positions...
            // public STitlePacket(STitlePacket.Type _type, ITextComponent _title?, int _fadeIn, int _displayTime, int _fadeOut)
            STitlePacket titlePacket = new
                    STitlePacket(STitlePacket.Type.TITLE,
                    title_text_component,
                    fade_in_seconds,
                    display_duration,
                    fade_out_seconds);

            STitlePacket subtitlePacket = null;

            // send a single char, like "*", to skip the sub-title
            if (subtitle_text.length() > 1) {

                TextComponent subtitle_text_component = new StringTextComponent(subtitle_text);

                subtitlePacket = new
                        STitlePacket(STitlePacket.Type.SUBTITLE,
                        subtitle_text_component,
                        fade_in_seconds,
                        display_duration,
                        fade_out_seconds);
            }

            PlayerList players = this.minecraftServer.getPlayerList();
            for (ServerPlayerEntity p : players.getPlayers()) {
                if (target.equals("@a") || p.getScoreboardName().toLowerCase().equals(target)) {

                    if (subtitlePacket != null)
                        p.connection.sendPacket(subtitlePacket);

                    p.connection.sendPacket(titlePacket);
                }
            }
        }

        else {
            result = "ERROR: UNKNOWN COMMAND: " + command;
        }

        return result;
    }

    /*
     * reads incoming messages from connect clients and passes them along for parsing and response
     *
     * @param commands_filename - name of the file to read for commands
     * @param results_filename - name of the file to read for commands
     */
    // NOTE: this was made public so it can be called externally to test a bug/issue with redstone blocks and command blocks
    public void processCommandFile(World world, String commands_filename, String results_filename) {

        LOGGER.info("[APIServer] processing commands");

        // testing to see if we can push this processing back to the main thread somehow...
        if (this.mc_server_thread_handle != null) {
            // rename the commands file so it doesn't get 'seen' again and another packet sent!
            UUID uuid = UUID.randomUUID();
            String packet_filename = uuid.toString() + ".txt";  // keeping the ".txt" file extension

            File commands = new File(commands_filename);
            File packet = new File(packet_filename);

            LOGGER.info("renamed " + commands_filename + " to " + packet_filename);
            commands.renameTo(packet);

            LOGGER.info("[APIServer] send packet to process commands in main server thread...");
            PacketHandler.sendToServer_RunManualCommandPacket(
                    new RunManualCommandPacket(packet_filename, results_filename)
            );
            return;
        }

        String results = "";

        boolean report_thread_debugging_info = false;
        if (report_thread_debugging_info) {
            String world_details = "toString(): " + world.toString()
                    + ", dimension: " + world.dimension.toString()
                    + ", isRemote: " + world.isRemote
                    + ", getProviderName(): " + world.getProviderName()
                    + ", getWorldInfo(): " + world.getWorldInfo().toString()
                    + ", getWorldType(): " + world.getWorldType().toString();

            // for debugging we will output details about our world, since we are trying to track down
            // why our world works differently when run as a command or from a thread...
            results += "# world details: " + world_details + "\n";
            LOGGER.info("APIServer world details: " + world_details);

            String thread_details = Thread.currentThread().toString();
            results += "# thread details: " + thread_details + "\n";
            LOGGER.info("APIServer thread details: " + thread_details);
        }

        // read the commands as individual lines
        try {
            List<String> lines = Files.readAllLines(Paths.get(commands_filename));

            // delete the commands file (otherwise we'll create an endless loop!)
            Files.delete(Paths.get(commands_filename));

            // parse and execute individual command lines
            for (String line : lines) {
                try {
                    results += parseAndExecuteLine(world, line.trim()) + "\n";
                }
                catch (Exception ex) {
                    results += "ERROR: exception parsing line " + ex.toString();
                    break;
                }
            }

        } catch (IOException e) {
            results = "ERROR: failed reading commands file\n" + e.toString();
            LOGGER.error(results);
        }

        // write our results
        try {
            // NOTE: we must remove any existing "results.txt" file, for now, we should send in a 'results.txt' filename from the client and let the client clean it up

            // we'll write to a temp file and then rename it, rather than risking partially writing and having that read
            Files.write(Paths.get(results_filename + ".tmp"), Collections.singleton(results));
            File tmp = new File(results_filename + ".tmp");
            File final_results = new File(results_filename);

            if (final_results.exists()) {
                LOGGER.warn("previous results file " + results_filename + " found, deleting so we can write new results file");
                final_results.delete();
            }

            tmp.renameTo(final_results);
        } catch (IOException e) {
            LOGGER.error("Exception writing results file to " + results_filename + ": " + e.toString());
        }
    }
}
