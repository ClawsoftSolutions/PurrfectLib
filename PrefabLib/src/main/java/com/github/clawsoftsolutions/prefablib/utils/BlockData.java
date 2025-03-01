package com.github.clawsoftsolutions.prefablib.utils;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class BlockData {
    private final Block block;
    private final BlockPos pos;
    private final BlockState state;

    public BlockData(Block block, BlockPos pos, BlockState state) {
        this.block = block;
        this.pos = pos;
        this.state = state;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("block", Registry.BLOCK.getKey(block).toString());

        CompoundTag posTag = new CompoundTag();
        posTag.putInt("x", pos.getX());
        posTag.putInt("y", pos.getY());
        posTag.putInt("z", pos.getZ());
        tag.put("pos", posTag);

        CompoundTag stateTag = new CompoundTag();
        if (state.hasProperty(BlockStateProperties.FACING)) {
            stateTag.putInt("facing", state.getValue(BlockStateProperties.FACING).get3DDataValue());
        }
        tag.put("state", stateTag);

        return tag;
    }

    public static BlockData fromNBT(CompoundTag tag) {
        String blockName = tag.getString("block");
        ResourceLocation blockLocation = new ResourceLocation(blockName);
        Block block = Registry.BLOCK.get(blockLocation);

        if (!(block != null)) {
            throw new IllegalArgumentException("Block not found for name: " + blockName);
        }

        CompoundTag posTag = tag.getCompound("pos");
        BlockPos pos = new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z"));
        CompoundTag stateTag = tag.getCompound("state");

        BlockState state = block.defaultBlockState();
        if (state.hasProperty(BlockStateProperties.FACING)) {
            Direction facing = Direction.from3DDataValue(stateTag.getInt("facing"));
            state = state.setValue(BlockStateProperties.FACING, facing);
        }

        return new BlockData(block, pos, state);
    }
}
