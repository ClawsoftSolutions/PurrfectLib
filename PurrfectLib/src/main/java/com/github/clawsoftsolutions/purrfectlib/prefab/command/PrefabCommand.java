package com.github.clawsoftsolutions.purrfectlib.prefab.command;

import com.github.clawsoftsolutions.purrfectlib.prefab.encoding.PrefabEncoder;
import com.github.clawsoftsolutions.purrfectlib.prefab.decoding.PrefabDecoder;
import com.github.clawsoftsolutions.purrfectlib.prefab.renderering.PrefabRenderer;
import com.github.clawsoftsolutions.purrfectlib.prefab.utils.BlockData;
import com.github.clawsoftsolutions.purrfectlib.prefab.utils.FileManagement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PrefabCommand {

    public static BlockPos pos1;
    public static BlockPos pos2;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("prefab").then(
                        // Testing commands
                        Commands.literal("test")
                                .then(Commands.literal("encode")
                                        .executes(PrefabCommand::encode)
                                )
                                .then(Commands.literal("decode")
                                        .then(Commands.argument("filename", StringArgumentType.word())
                                                .executes(PrefabCommand::decodePrefab)
                                        )
                                )

                )
                        .then(Commands.literal("setpos1")
                                .executes(PrefabCommand::setPos1)
                        )
                        .then(Commands.literal("setpos2")
                                .executes(PrefabCommand::setPos2)
                        )
                        .then(Commands.literal("list")
                                .executes(PrefabCommand::listPrefabs)
                        )
                        .then(Commands.literal("export")
                                .then(Commands.argument("filename", StringArgumentType.word())
                                        .executes(PrefabCommand::exportPrefab)
                                )
                        )
                        .then(Commands.literal("preview")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            BlockPos playerPos = context.getSource().getPlayerOrException().blockPosition().below();
                                            return previewPrefab(name, playerPos, context.getSource());
                                        })
                                )
                        )

        );
    }

    private static int previewPrefab(String name, BlockPos pos, CommandSourceStack source) throws CommandSyntaxException {
        try {
            System.out.println("Attempting to preview prefab: " + name);

            CompoundTag tag = FileManagement.loadPrefab(name);
            if (tag == null) {
                System.err.println("Error: Prefab not found: " + name);
                source.sendFailure(Component.literal("Prefab not found: " + name));
                return 0;
            }

            System.out.println("Loaded prefab data: " + tag);

            List<BlockData> blocks = PrefabDecoder.decodePrefab(tag);
            if (blocks == null || blocks.isEmpty()) {
                System.err.println("Error: Decoded prefab returned an empty or null block list!");
                source.sendFailure(Component.literal("Prefab has no blocks!"));
                return 0;
            }

            System.out.println("Successfully loaded prefab with " + blocks.size() + " blocks.");

            if (pos == null) {
                System.err.println("Error: Provided position is null!");
                source.sendFailure(Component.literal("Invalid preview position!"));
                return 0;
            }

            System.out.println("Setting preview at position: " + pos);
            PrefabRenderer.setPreview(blocks, pos);
            source.sendSuccess(Component.literal("Successfully viewing prefab"), true);

            return 1;
        } catch (Exception e) {
            System.err.println("Critical Error in previewPrefab: " + e.getMessage());
            e.printStackTrace();
            source.sendFailure(Component.literal("Unexpected error: " + e.getMessage()));
            return 0;
        }
    }


    private static int setPos1(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = new BlockPos(context.getSource().getPosition());
        pos1 = pos;
        context.getSource().sendSuccess(Component.literal("Prefab position 1 set to: " + pos.toShortString()), true);
        return 1;
    }

    private static int setPos2(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = new BlockPos(context.getSource().getPosition());
        pos2 = pos;
        context.getSource().sendSuccess(Component.literal("Prefab position 2 set to: " + pos.toShortString()), true);
        return 1;
    }

    private static int encode(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (pos1 == null || pos2 == null) {
            context.getSource().sendFailure(Component.literal("Both positions must be set before encoding a prefab."));
            return 0;
        }
        ServerLevel level = context.getSource().getLevel();
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        BlockPos origin = new BlockPos(minX, minY, minZ);

        List<BlockData> blockDataList = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!state.isAir()) {
                        BlockPos relativePos = pos.subtract(origin);
                        BlockData blockData = new BlockData(state.getBlock(), relativePos, state);
                        blockDataList.add(blockData);
                    }
                }
            }
        }
        CompoundTag prefabNBT = PrefabEncoder.encodePrefab(blockDataList, origin);
        context.getSource().sendSuccess(Component.literal("Prefab encoded with " + blockDataList.size() + " blocks."), true);
        return 1;
    }

    private static int exportPrefab(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (pos1 == null || pos2 == null) {
            context.getSource().sendFailure(Component.literal("Both positions must be set before exporting a prefab."));
            return 0;
        }
        ServerLevel level = context.getSource().getLevel();
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        BlockPos origin = new BlockPos(minX, minY, minZ);

        List<BlockData> blockDataList = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!state.isAir()) {
                        BlockPos relativePos = pos.subtract(origin);
                        BlockData blockData = new BlockData(state.getBlock(), relativePos, state);
                        blockDataList.add(blockData);
                    }
                }
            }
        }
        CompoundTag prefabNBT = PrefabEncoder.encodePrefab(blockDataList, origin);
        String filename = StringArgumentType.getString(context, "filename");
        try {
            File file = FileManagement.exportNBTToFile(prefabNBT, filename);
            context.getSource().sendSuccess(Component.literal("Prefab exported successfully to: " + file.getAbsolutePath()), true);
        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("Failed to export prefab: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int listPrefabs(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        File folder = new File("prefabs");
        if (!folder.exists() || !folder.isDirectory()) {
            context.getSource().sendFailure(Component.literal("No prefab folder found."));
            return 0;
        }
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".nbt"));
        if (files == null || files.length == 0) {
            context.getSource().sendSuccess(Component.literal("No prefabs found."), true);
            return 1;
        }
        StringBuilder fileList = new StringBuilder("Prefabs: ");
        for (File file : files) {
            fileList.append(file.getName()).append(" ");
        }
        context.getSource().sendSuccess(Component.literal(fileList.toString()), true);
        return 1;
    }

    private static int decodePrefab(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String filename = StringArgumentType.getString(context, "filename");
        File file = new File("prefabs", filename + ".nbt");
        if (!file.exists()) {
            context.getSource().sendFailure(Component.literal("Prefab file not found: " + file.getAbsolutePath()));
            return 0;
        }
        CompoundTag prefabNBT;
        try (FileInputStream fis = new FileInputStream(file)) {
            DataInput dataInput = new DataInputStream(fis);
            prefabNBT = NbtIo.read(dataInput);
        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("Error reading prefab file: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
        try {
            PrefabDecoder.decodePrefab(prefabNBT);
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error decoding prefab: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
        context.getSource().sendSuccess(Component.literal("Prefab decoded successfully."), true);
        return 1;
    }
}
