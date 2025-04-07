package net.turtleboi.turtlecore.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.turtleboi.turtlecore.TurtleCore;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static net.turtleboi.turtlecore.client.util.VertexBuilder.vertex;

public class FireCircleRenderer {
    public static final ResourceLocation FIRE_AURA_TEXTURE = new ResourceLocation(TurtleCore.MOD_ID, "textures/spell_effects/fire_aura.png");
    public static final ResourceLocation ULTRA_AURA_TEXTURE = new ResourceLocation(TurtleCore.MOD_ID, "textures/spell_effects/ultra_fire_aura.png");
    public static final ResourceLocation GREEN_AURA_TEXTURE = new ResourceLocation(TurtleCore.MOD_ID, "textures/spell_effects/green_fire_aura.png");

    public static void renderFireCircle(MultiBufferSource bufferSource, PoseStack poseStack, int vertexAlpha, boolean blueFire, boolean greenFire){
        ResourceLocation texture;
        if (blueFire) {
            texture = ULTRA_AURA_TEXTURE;
        } else if (greenFire) {
            texture = GREEN_AURA_TEXTURE;
        } else {
            texture = FIRE_AURA_TEXTURE;
        }

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(texture));
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
