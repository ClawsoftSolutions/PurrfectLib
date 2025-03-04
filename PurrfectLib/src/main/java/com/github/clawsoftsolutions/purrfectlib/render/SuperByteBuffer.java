package com.github.clawsoftsolutions.purrfectlib.render;

import com.github.clawsoftsolutions.purrfectlib.math.Transform;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class SuperByteBuffer {
    private final List<BlockState> blockStates = new ArrayList<>();
    private final List<BlockPos> blockPositions = new ArrayList<>();

    public void addBlock(BlockState state, BlockPos pos) {
        blockStates.add(state);
        blockPositions.add(pos);
    }

    public void render(PoseStack ms, VertexConsumer vertexConsumer) {
        Minecraft mc = Minecraft.getInstance();
        for (int i = 0; i < blockStates.size(); i++) {
            BlockState state = blockStates.get(i);
            BlockPos pos = blockPositions.get(i);
            List<BakedQuad> quads = mc.getBlockRenderer()
                    .getBlockModel(state)
                    .getQuads(state, null, RandomSource.create());

            for (BakedQuad quad : quads) {
                putBulkData(ms.last(), vertexConsumer, quad, 1.0f, 1.0f, 1.0f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            }
        }
    }

    public void renderInto(PoseStack ms, VertexConsumer vertexConsumer) {
        render(ms, vertexConsumer);
    }

    public void putBulkData(PoseStack.Pose pose, VertexConsumer vertexConsumer, BakedQuad quad,
                            float r, float g, float b, int overlay, int light) {
        int[] vertices = quad.getVertices();
        int vertexCount = vertices.length / 8;

        Vector3f sourcePos = new Vector3f();
        Vector3f transformedPos;
        Vector3f sourceNormal = new Vector3f();
        Vector3f transformedNormal;

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        pose.pose().store(fb);
        fb.flip();
        float[] matrix = new float[16];
        fb.get(matrix);

        FloatBuffer fbNormal = BufferUtils.createFloatBuffer(9);
        pose.normal().store(fbNormal);
        fbNormal.flip();
        float[] normalMatrix = new float[9];
        fbNormal.get(normalMatrix);

        for (int i = 0; i < vertexCount; i++) {
            sourcePos.set(
                    Float.intBitsToFloat(vertices[i * 8]),
                    Float.intBitsToFloat(vertices[i * 8 + 1]),
                    Float.intBitsToFloat(vertices[i * 8 + 2])
            );
            transformedPos = Transform.transformVector4x4(matrix, sourcePos);
            sourceNormal.set(
                    Float.intBitsToFloat(vertices[i * 8 + 5]),
                    Float.intBitsToFloat(vertices[i * 8 + 6]),
                    Float.intBitsToFloat(vertices[i * 8 + 7])
            );
            transformedNormal = Transform.transformVector3x3(normalMatrix, sourceNormal);

            vertexConsumer
                    .vertex(transformedPos.x(), transformedPos.y(), transformedPos.z())
                    .color(r, g, b, 1.0f)
                    .uv(Float.intBitsToFloat(vertices[i * 8 + 3]), Float.intBitsToFloat(vertices[i * 8 + 4]))
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(transformedNormal.x(), transformedNormal.y(), transformedNormal.z())
                    .endVertex();
        }
    }


    public boolean isEmpty() {
        return blockStates.isEmpty() || blockPositions.isEmpty();
    }
}
