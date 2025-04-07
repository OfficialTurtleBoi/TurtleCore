package net.turtleboi.turtlecore.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.turtleboi.turtlecore.client.data.SpikeData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static net.turtleboi.turtlecore.client.util.VertexBuilder.vertex;

public class IceSpikeRenderer {
    public static void renderSpike(PoseStack poseStack, MultiBufferSource bufferSource, SpikeData spike, int amplifier, float zTranslation, float xAngle, float yAngle, float zAngle, int vertexAlpha) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(spike.texture));
        poseStack.pushPose();
        poseStack.translate(0, 0, zTranslation);
        poseStack.scale(1, 1, (float) (1 + (amplifier / 2)));
        poseStack.mulPose(Axis.XP.rotationDegrees(xAngle));
        poseStack.mulPose(Axis.YP.rotationDegrees(yAngle));
        poseStack.mulPose(Axis.ZP.rotationDegrees(zAngle));
        renderSpikeQuad(poseStack, consumer, spike.width, spike.height, vertexAlpha);
        poseStack.popPose();
    }

    private static void renderSpikeQuad(PoseStack postStack, VertexConsumer vertexConsumer, float width, float height, int vertexAlpha) {
        PoseStack.Pose pose = postStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        float halfWidth = width / 2.0f;
        vertex(vertexConsumer, matrix, normalMatrix, -halfWidth, 0, 0, 0, 0, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, halfWidth, 0, 0, 1, 0, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, halfWidth, height, 0, 1, 1, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, -halfWidth, height, 0, 0, 1, 255, 255, 255, vertexAlpha);

        vertex(vertexConsumer, matrix, normalMatrix, -halfWidth, height, 0, 0, 1, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, halfWidth, height, 0, 1, 1, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, halfWidth, 0, 0, 1, 0, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, -halfWidth, 0, 0, 0, 0, 255, 255, 255, vertexAlpha);
    }
}
