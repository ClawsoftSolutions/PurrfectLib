package com.github.clawsoftsolutions.purrfectlib.render;

import com.github.clawsoftsolutions.purrfectlib.math.Transform;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
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

    public void renderInto(PoseStack ms, VertexConsumer vertexConsumer) {
        Minecraft mc = Minecraft.getInstance();
        RandomSource random = RandomSource.create();

        for (int i = 0; i < blockStates.size(); i++) {
            BlockState state = blockStates.get(i);
            BlockPos pos = blockPositions.get(i);
            if (state == null || pos == null) {
                continue;
            }

            BakedModel model = mc.getBlockRenderer().getBlockModel(state);
            if (model == null) {
                continue;
            }
            List<BakedQuad> quads = model.getQuads(state, null, random);
            int light = mc.level != null ? mc.level.getLightEngine().getRawBrightness(pos, 0) : LightTexture.FULL_BRIGHT;

            for (BakedQuad quad : quads) {
                putBulkData(ms.last(), vertexConsumer, quad, 1.0f, 1.0f, 1.0f, 1.0f, OverlayTexture.NO_OVERLAY, light);
            }
        }
    }


    public void putBulkData(PoseStack.Pose pose, VertexConsumer vertexConsumer, BakedQuad quad,
                            float r, float g, float b, float alpha, int overlay, int light) {
        int[] vertices = quad.getVertices();
        int vertexCount = vertices.length / 8;

        if (pose == null) {
            throw new IllegalStateException("PoseStack.Pose is null!");
        }

        // Extract 4x4 transformation matrix
        FloatBuffer fbPose = BufferUtils.createFloatBuffer(16);
        pose.pose().store(fbPose);
        fbPose.flip();
        float[] matrix = new float[16];
        fbPose.get(matrix);

        // Extract 3x3 normal matrix using store()
        FloatBuffer fbNormal = BufferUtils.createFloatBuffer(9);
        pose.normal().store(fbNormal);
        fbNormal.flip();
        float[] normalMatrix = new float[9];
        fbNormal.get(normalMatrix);

        Vector3f sourcePos = new Vector3f();
        Vector3f transformedPos;
        Vector3f sourceNormal = new Vector3f();
        Vector3f transformedNormal;

        for (int i = 0; i < vertexCount; i++) {
            if (vertices.length < (i * 8 + 7)) {
                throw new IllegalStateException("Vertex data underfilled! Expected at least " + (i * 8 + 7) + " elements, but got " + vertices.length);
            }

            // Extract position data
            sourcePos.set(
                    Float.intBitsToFloat(vertices[i * 8]),
                    Float.intBitsToFloat(vertices[i * 8 + 1]),
                    Float.intBitsToFloat(vertices[i * 8 + 2])
            );
            transformedPos = Transform.transformVector4x4(matrix, sourcePos);

            // Extract normal data
            sourceNormal.set(
                    Float.intBitsToFloat(vertices[i * 8 + 5]),
                    Float.intBitsToFloat(vertices[i * 8 + 6]),
                    Float.intBitsToFloat(vertices[i * 8 + 7])
            );
            transformedNormal = Transform.transformVector3x3(normalMatrix, sourceNormal);

            // Send vertex data to the consumer
            vertexConsumer
                    .vertex(transformedPos.x(), transformedPos.y(), transformedPos.z())
                    .color(r, g, b, alpha)
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

    public String remaining() {
        return "BlockStates: " + blockStates.size() + ", BlockPositions: " + blockPositions.size();
    }
}
