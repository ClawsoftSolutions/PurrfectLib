package com.github.clawsoftsolutions.prefablib.decoding;

import com.github.clawsoftsolutions.prefablib.utils.BlockData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PrefabDecoder {
    public static List<BlockData> decodePrefab(CompoundTag prefabTag) {
        List<BlockData> blocks = new ArrayList<>();

        if (prefabTag == null) {
            System.err.println("Error: decodePrefab received a null CompoundTag!");
            return blocks;
        }

        ListTag blockList = prefabTag.getList("blocks", 10);
        if (blockList.isEmpty()) {
            System.err.println("Error: Prefab has no blocks in the NBT data!");
            return blocks;
        }

        BlockPos origin = new BlockPos(
                prefabTag.getCompound("origin").getInt("x"),
                prefabTag.getCompound("origin").getInt("y"),
                prefabTag.getCompound("origin").getInt("z")
        );

        System.out.println("Decoding prefab with origin at " + origin + " and " + blockList.size() + " blocks.");

        for (int i = 0; i < blockList.size(); i++) {
            CompoundTag blockTag = blockList.getCompound(i);
            BlockData blockData = BlockData.fromNBT(blockTag);
            BlockPos adjustedPos = blockData.getPos().offset(origin);

            blocks.add(new BlockData(blockData.getBlock(), adjustedPos, blockData.getState()));

            System.out.println("Loaded block at adjusted position: " + adjustedPos);
        }

        System.out.println("decodePrefab successfully loaded " + blocks.size() + " blocks.");
        return blocks;
    }

}
