package com.github.clawsoftsolutions.purrfectlib.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.Map;

public class SuperRenderTypeBuffer implements MultiBufferSource {

    private final Map<RenderType, VertexConsumer> consumerCache = new HashMap<>();
    private final BufferSource underlyingBuffer;

    public SuperRenderTypeBuffer(BufferSource bufferSource) {
        this.underlyingBuffer = bufferSource;
    }

    @Override
    public VertexConsumer getBuffer(RenderType type) {
        return consumerCache.computeIfAbsent(type, underlyingBuffer::getBuffer);
    }

    public void endBatch(RenderType type) {
        VertexConsumer consumer = consumerCache.get(type);
        if (consumer instanceof BufferSource) {
            underlyingBuffer.endBatch(type);
        }
    }

    public void endAll() {
        consumerCache.keySet().forEach(underlyingBuffer::endBatch);
        consumerCache.clear();
    }
}

