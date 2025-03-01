package com.github.clawsoftsolutions.prefablib.renderering;

import com.github.clawsoftsolutions.prefablib.PrefabLib;
import com.github.clawsoftsolutions.prefablib.utils.BlockData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

public class PrefabGhostRenderer {
    private static List<BlockData> previewBlocks;
    private static BlockPos previewOrigin;

    public static void setPreview(List<BlockData> blocks, BlockPos origin) {
        PrefabLib.LOG.info("Setting preview with {} blocks at position {}", blocks.size(), origin);
        if (blocks == null || origin == null) {
            System.err.println("Error: Blocks or position is null!");
            return;
        }

        previewBlocks = blocks;
        previewOrigin = origin;

        System.out.println("setPreview called successfully! Blocks: " + blocks.size() + ", Origin: " + origin);
    }


    public static void render(PoseStack stack) {
        if (previewBlocks == null || previewOrigin == null) {
            System.err.println("Error: previewBlocks or previewOrigin is null!");
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Level world = mc.level;
        BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        for (BlockData data : previewBlocks) {
            if (data == null) {
                System.err.println("Error: Encountered null BlockData in previewBlocks!");
                continue;
            }

            BlockPos pos = data.getPos();
            if (pos == null) {
                System.err.println("Error: BlockData has null position!");
                continue;
            }

            pos = pos.offset(previewOrigin);
            BlockState state = data.getState();

            if (state == null) {
                System.err.println("Error: BlockData has null BlockState!");
                continue;
            }

            stack.pushPose();
            stack.translate(pos.getX(), pos.getY(), pos.getZ());

            try {
                blockRenderer.renderBatched(
                        state, pos, world, stack, buffer.getBuffer(RenderType.translucent()), false, world.random
                );
            } catch (Exception e) {
                System.err.println("Error rendering block at " + pos + ": " + e.getMessage());
                e.printStackTrace();
            }

            stack.popPose();
        }

        buffer.endBatch(RenderType.translucent());
    }

}
