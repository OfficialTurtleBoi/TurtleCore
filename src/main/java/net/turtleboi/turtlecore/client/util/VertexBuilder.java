package net.turtleboi.turtlecore.client.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class VertexBuilder {
    public static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix, Matrix3f normalMatrix,
                              float x, float y, float z, float u, float v, int red, int green, int blue, int vertexAlpha) {
        vertexConsumer.vertex(matrix, x, y, z)
                .color(red, green, blue, vertexAlpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normalMatrix,0, 0, 1)
                .endVertex();
    }
}
