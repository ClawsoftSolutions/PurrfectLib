package com.github.clawsoftsolutions.purrfectlib.prefab.renderering;

import com.github.clawsoftsolutions.purrfectlib.Constants;
import com.github.clawsoftsolutions.purrfectlib.prefab.utils.BlockData;
import com.github.clawsoftsolutions.purrfectlib.render.SuperByteBuffer;
import com.github.clawsoftsolutions.purrfectlib.render.SuperRenderTypeBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrefabRenderer {

    private static final Map<RenderType, SuperByteBuffer> bufferCache = new LinkedHashMap<>();
    private static boolean active;
    private static boolean changed;
    private static BlockPos prefabOrigin;
    private static final Minecraft mc = Minecraft.getInstance();
    private static final RandomSource random = RandomSource.createNewThreadLocalInstance();
    private static final Logger LOGGER = Constants.addExt("PrefabRenderer");

    public static List<BlockData> prefabBlocks;

    public PrefabRenderer() {
        changed = false;
    }
    public static void setPreview(List<BlockData> blocks, BlockPos origin) {
        prefabBlocks = blocks;
        prefabOrigin = origin;
        active = true;
        changed = true;
    }

    public static void render(PoseStack ms, SuperRenderTypeBuffer buffers) {
        if (!active || mc.level == null || mc.player == null) return;

        ms.pushPose();
        double camX = mc.gameRenderer.getMainCamera().getPosition().x;
        double camY = mc.gameRenderer.getMainCamera().getPosition().y;
        double camZ = mc.gameRenderer.getMainCamera().getPosition().z;
        ms.translate(-camX, -camY, -camZ);

        if (changed) {
            redraw(buffers);
            changed = false;
        }

        bufferCache.forEach((layer, buffer) -> {
            if (buffer != null && !buffer.isEmpty()) {
                buffer.renderInto(ms, buffers.getBuffer(layer));
            }
        });

        ms.popPose();
    }

    private static void redraw(SuperRenderTypeBuffer buffers) {
        bufferCache.clear();
        SuperByteBuffer buffer = drawLayer(RenderType.translucent(), buffers.getBuffer(RenderType.translucent()));
        if (!buffer.isEmpty()) {
            bufferCache.put(RenderType.translucent(), buffer);
        }
    }

    private static SuperByteBuffer drawLayer(RenderType currentLayer, VertexConsumer vertexConsumer) {
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        SuperByteBuffer buffer = new SuperByteBuffer();

        for (BlockData blockData : prefabBlocks) {
            BlockPos worldPos = blockData.getPos().offset(prefabOrigin);
            BlockState state = blockData.getState();

            if (state == null) {
                LOGGER.warn("Skipping null block state at: {}", worldPos);
                continue;
            }

            if (state.getRenderShape() != RenderShape.MODEL) {
                LOGGER.warn("Block at {} does not use MODEL render shape.", worldPos);
                continue;
            }

            BakedModel model = dispatcher.getBlockModel(state);
            if (model == null) {
                LOGGER.warn("Skipping block at {} due to missing model.", worldPos);
                continue;
            }

            BlockEntity blockEntity = mc.level.getBlockEntity(worldPos);
            ModelData modelData = blockEntity != null ? blockEntity.getModelData() : ModelData.EMPTY;
            modelData = model.getModelData(mc.level, worldPos, state, modelData);
            long seed = state.getSeed(worldPos);
            random.setSeed(seed);

            ChunkRenderTypeSet renderTypes = model.getRenderTypes(state, random, modelData);
            if (!renderTypes.contains(currentLayer)) continue;

            poseStack.pushPose();
            poseStack.translate(worldPos.getX(), worldPos.getY(), worldPos.getZ());

            for (Direction direction : Direction.values()) {
                List<BakedQuad> faceQuads = model.getQuads(state, direction, random, modelData, currentLayer);
                if (!faceQuads.isEmpty()) {
                    processQuads(faceQuads, poseStack, vertexConsumer, buffer, 1.0f);
                }
            }

            List<BakedQuad> generalQuads = model.getQuads(state, null, random, modelData, currentLayer);
            if (!generalQuads.isEmpty()) {
                processQuads(generalQuads, poseStack, vertexConsumer, buffer, 1.0f);
            }

            poseStack.popPose();
        }

        poseStack.popPose();
        return buffer;
    }

    private static void processQuads(List<BakedQuad> quads, PoseStack poseStack, VertexConsumer vertexConsumer, SuperByteBuffer buffer, float alpha) {
        for (BakedQuad quad : quads) {
            if (quad == null || quad.getVertices().length == 0) {
                LOGGER.warn("Skipping invalid quad.");
                continue;
            }
            buffer.putBulkData(
                    poseStack.last(), vertexConsumer, quad,
                    1.0f, 1.0f, 1.0f, alpha,// RGBA color
                    OverlayTexture.NO_OVERLAY, 15728880 // Lightmap and overlay
            );
        }
    }
}
