package com.github.clawsoftsolutions.purrfectlib.prefab.encoding;

import com.github.clawsoftsolutions.purrfectlib.prefab.utils.BlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.List;

public class PrefabEncoder {
    public static CompoundTag encodePrefab(List<BlockData> blocks, BlockPos origin) {
        CompoundTag prefabTag = new CompoundTag();

        CompoundTag originTag = new CompoundTag();
        originTag.putInt("x", origin.getX());
        originTag.putInt("y", origin.getY());
        originTag.putInt("z", origin.getZ());
        prefabTag.put("origin", originTag);

        ListTag blockList = new ListTag();
        for (BlockData blockData : blocks) {
            blockList.add(blockData.toNBT());
        }
        prefabTag.put("blocks", blockList);

        return prefabTag;
    }
}
