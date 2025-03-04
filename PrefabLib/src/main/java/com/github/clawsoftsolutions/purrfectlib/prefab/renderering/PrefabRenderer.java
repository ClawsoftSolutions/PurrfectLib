package com.github.clawsoftsolutions.purrfectlib.prefab.renderering;

import com.github.clawsoftsolutions.purrfectlib.prefab.utils.BlockData;
import com.github.clawsoftsolutions.purrfectlib.render.SuperByteBuffer;
import com.github.clawsoftsolutions.purrfectlib.render.SuperRenderTypeBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrefabRenderer {

    private static final ThreadLocal<ThreadLocalObjects> THREAD_LOCAL_OBJECTS = ThreadLocal.withInitial(ThreadLocalObjects::new);
    private static final Map<RenderType, SuperByteBuffer> bufferCache = new LinkedHashMap<>();
    private static boolean active;
    private static boolean changed;
    private static BlockPos prefabOrigin;
    private static Minecraft mc;

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
        if (!active) return;

        mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        if (changed) redraw(buffers);

        changed = false;

        bufferCache.forEach((layer, buffer) -> {
            buffer.renderInto(ms, buffers.getBuffer(layer));
        });
    }

    private static void redraw(SuperRenderTypeBuffer buffers) {
        bufferCache.clear();
        for (RenderType layer : RenderType.chunkBufferLayers()) {
            SuperByteBuffer buffer = drawLayer(layer, buffers.getBuffer(layer));
            if (!buffer.isEmpty()) bufferCache.put(layer, buffer);
        }
    }

    private static SuperByteBuffer drawLayer(RenderType layer, VertexConsumer vertexConsumer) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer renderer = dispatcher.getModelRenderer();
        ThreadLocalObjects objects = THREAD_LOCAL_OBJECTS.get();

        PoseStack poseStack = objects.poseStack;
        RandomSource random = objects.random;

        SuperByteBuffer buffer = new SuperByteBuffer();

        for (BlockData blockData : prefabBlocks) {
            BlockPos worldPos = blockData.getPos().offset(prefabOrigin);
            BlockState state = blockData.getState();

            if (state.getRenderShape() == RenderShape.MODEL) {
                BakedModel model = dispatcher.getBlockModel(state);
                BlockEntity blockEntity = mc.level.getBlockEntity(worldPos);
                ModelData modelData = blockEntity != null ? blockEntity.getModelData() : ModelData.EMPTY;
                modelData = model.getModelData(mc.level, worldPos, state, modelData);
                long seed = state.getSeed(worldPos);
                random.setSeed(seed);

                if (model.getRenderTypes(state, random, modelData).contains(layer)) {
                    poseStack.pushPose();
                    poseStack.translate(worldPos.getX(), worldPos.getY(), worldPos.getZ());

                    List<BakedQuad> quads = model.getQuads(state, null, random, modelData, layer);
                    for (BakedQuad quad : quads) {
                        buffer.putBulkData(poseStack.last(), vertexConsumer, quad,1.0f, 1.0f, 1.0f, OverlayTexture.NO_OVERLAY, 15728880);
                    }

                    poseStack.popPose();
                }
            }
        }

        return buffer;
    }

    private static class ThreadLocalObjects {
        public final PoseStack poseStack = new PoseStack();
        public final RandomSource random = RandomSource.createNewThreadLocalInstance();
    }
}
