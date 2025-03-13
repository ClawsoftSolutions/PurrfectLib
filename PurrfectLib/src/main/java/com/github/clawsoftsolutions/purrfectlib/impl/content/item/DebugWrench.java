package com.github.clawsoftsolutions.purrfectlib.impl.content.item;

import com.github.clawsoftsolutions.purrfectlib.Constants;
import com.github.clawsoftsolutions.purrfectlib.impl.content.utils.WrenchMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DebugWrench extends Item {

    public DebugWrench() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();

        if (player == null || level.isClientSide) return InteractionResult.SUCCESS;

        WrenchMode currentMode = getMode(context.getItemInHand());
        Constants.LOGGER.info("Wrench mode in useOn: {}", currentMode.name());

        if (currentMode == null) {
            player.displayClientMessage(Component.literal("Please select a mode!"), true);
        }
        switch (currentMode){
            case ROTATE -> {
                if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    Direction currentFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    Direction newFacing = currentFacing.getClockWise();
                    level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing));
                    level.sendBlockUpdated(pos, state, state.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing), 3);
                    setMode(stack, currentMode);
                    return InteractionResult.SUCCESS;
                }

                if (state.hasProperty(BlockStateProperties.FACING)) {
                    Direction currentFacing = state.getValue(BlockStateProperties.FACING);
                    Direction newFacing;
                    if (player.isCrouching()) {
                        newFacing = (currentFacing == Direction.UP) ? Direction.DOWN : Direction.UP;
                    } else {
                        newFacing = currentFacing.getClockWise();
                    }
                    level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.FACING, newFacing));
                    level.sendBlockUpdated(pos, state, state.setValue(BlockStateProperties.FACING, newFacing), 3);
                    setMode(stack, currentMode);
                    return InteractionResult.SUCCESS;
                }

                if (state.hasProperty(BlockStateProperties.AXIS)) {
                    Direction.Axis currentAxis = state.getValue(BlockStateProperties.AXIS);
                    Direction.Axis newAxis;
                    if (player.isCrouching()) {
                        newAxis = (currentAxis == Direction.Axis.Y) ? Direction.Axis.X : Direction.Axis.Y;
                    } else {
                        newAxis = (currentAxis == Direction.Axis.X) ? Direction.Axis.Z
                                : (currentAxis == Direction.Axis.Z) ? Direction.Axis.Y : Direction.Axis.X;
                    }
                    BlockState newState = state.setValue(BlockStateProperties.AXIS, newAxis);
                    level.setBlockAndUpdate(pos, newState);
                    level.sendBlockUpdated(pos, state, newState, 2);
                    setMode(stack, currentMode);
                    return InteractionResult.SUCCESS;
                }

                BlockState rotatedState = state.rotate(level, pos, Rotation.CLOCKWISE_90);
                if (!rotatedState.equals(state)) {
                    level.setBlockAndUpdate(pos, rotatedState);
                    setMode(stack, currentMode);
                    return InteractionResult.SUCCESS;
                }
            }

            // There's no need to check for DEBUG mode in this class, the check is made in DebugOverlay.
        }
        return InteractionResult.PASS;
    }

    public static WrenchMode getMode(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("Mode")) {
                return WrenchMode.valueOf(tag.getString("Mode"));
            }
        }
        return WrenchMode.DEFAULT;
    }

    public static void setMode(ItemStack stack, WrenchMode mode) {
        stack.getOrCreateTag().putString("Mode", mode.name());
    }

}
