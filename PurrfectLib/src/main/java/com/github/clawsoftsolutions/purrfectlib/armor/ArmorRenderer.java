package com.github.clawsoftsolutions.purrfectlib.armor;

import com.github.clawsoftsolutions.purrfectlib.model.Model;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Map;

public class ArmorRenderer {
    public static void render(PoseStack poseStack, VertexConsumer buffer, Model model, int light, int overlay) {
        for (Model.Cube cube : model.getCubes()) {
            renderCube(poseStack, buffer, cube, light, overlay);
        }
    }

    private static void renderCube(PoseStack poseStack, VertexConsumer buffer, Model.Cube cube, int light, int overlay) {

        float x = cube.position.getX();
        float y = cube.position.getY();
        float z = cube.position.getZ();
        float width = cube.size.getX();
        float height = cube.size.getY();
        float depth = cube.size.getZ();

        drawQuad(buffer, poseStack.last().pose(), x, y, z, x + width, y + height, z, light, overlay);
    }

    private static void drawQuad(VertexConsumer buffer, Matrix4f matrix,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 int light, int overlay) {
        // Render the quad for the cube faces
        buffer.vertex(matrix, x1, y1, z1).overlayCoords(overlay).uv2(light).endVertex();
        buffer.vertex(matrix, x2, y1, z2).overlayCoords(overlay).uv2(light).endVertex();
        buffer.vertex(matrix, x2, y2, z2).overlayCoords(overlay).uv2(light).endVertex();
        buffer.vertex(matrix, x1, y2, z1).overlayCoords(overlay).uv2(light).endVertex();
    }
}
