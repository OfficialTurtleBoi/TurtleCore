package net.turtleboi.turtlecore.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.turtleboi.turtlecore.TurtleCore;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static net.turtleboi.turtlecore.client.util.VertexBuilder.vertex;

public class ArcaneCircleRenderer {
    public static final ResourceLocation ARCANE_AURA_TEXTURE = new ResourceLocation(TurtleCore.MOD_ID, "textures/spell_effects/arcane_circle.png");

    public static void renderArcaneCircle(MultiBufferSource bufferSource, PoseStack poseStack, int vertexAlpha){
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(ARCANE_AURA_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        vertex(vertexConsumer, matrix, normalMatrix, -6, -6, 0, 0, 0, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, 6, -6, 0, 1, 0, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, 6, 6, 0, 1, 1, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, -6, 6, 0, 0, 1, 255, 255, 255, vertexAlpha);

        vertex(vertexConsumer, matrix, normalMatrix, -6, 6, 0, 0, 1, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, 6, 6, 0, 1, 1, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, 6, -6, 0, 1, 0, 255, 255, 255, vertexAlpha);
        vertex(vertexConsumer, matrix, normalMatrix, -6, -6, 0, 0, 0, 255, 255, 255, vertexAlpha);
    }
}
