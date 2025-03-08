package com.github.clawsoftsolutions.purrfectlib.render;

import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.Map;

public class SuperRenderTypeBuffer implements MultiBufferSource {

    private static SuperRenderTypeBuffer instance;
    private final Map<RenderType, VertexConsumer> consumerCache = new HashMap<>();
    private final MultiBufferSource.BufferSource underlyingBuffer;

    public SuperRenderTypeBuffer(MultiBufferSource.BufferSource bufferSource) {
        this.underlyingBuffer = bufferSource;
    }

    public static SuperRenderTypeBuffer getInstance() {
        if (instance == null) {
            instance = new SuperRenderTypeBuffer(MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()));
        }
        return instance;
    }

    @Override
    public VertexConsumer getBuffer(RenderType type) {
        return consumerCache.computeIfAbsent(type, underlyingBuffer::getBuffer);
    }

    public void endBatch(RenderType type) {
        underlyingBuffer.endBatch(type);
    }

    public void endAll() {
        consumerCache.keySet().forEach(underlyingBuffer::endBatch);
        consumerCache.clear();
    }
}
