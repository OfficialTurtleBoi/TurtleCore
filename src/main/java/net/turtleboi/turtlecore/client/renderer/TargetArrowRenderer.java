package net.turtleboi.turtlecore.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.turtleboi.turtlecore.TurtleCore;
import net.turtleboi.turtlecore.client.data.TargetingClientData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class TargetArrowRenderer {
    private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation(TurtleCore.MOD_ID, "textures/gui/target_arrow.png");
    private static final ResourceLocation LOCKED_ARROW_TEXTURE = new ResourceLocation(TurtleCore.MOD_ID, "textures/gui/target_arrow_locked.png");

    public static void renderTargetArrow(PoseStack poseStack, MultiBufferSource bufferSource, Minecraft minecraft, Entity target, Player player) {
        if (target != null && player != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            ResourceLocation arrowTexture = TargetingClientData.isLockedOn() ? LOCKED_ARROW_TEXTURE : ARROW_TEXTURE;
            RenderType renderType = RenderType.entityCutoutNoCull(arrowTexture);
            VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
            RenderSystem.setShaderTexture(0, arrowTexture);

            float r, g, b;
            if (target instanceof Monster || target instanceof FlyingMob || target instanceof EnderDragon ||
                    target instanceof Shulker || target instanceof Slime || target instanceof ZombieHorse ||
                    target instanceof SkeletonHorse || target instanceof Hoglin) {
                // Hostile mobs (Red Tint)
                r = 2.0F; g = 0.158F;
            } else {
                // Friendly or neutral mobs (Green Tint)
                r = 0.737F; g = 2.0F;
            }
            b = 0.0F;

            poseStack.pushPose();
            poseStack.translate(0, target.getBbHeight() + (target.getBbHeight() / 3), 0);
            poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
            poseStack.scale(1.0F, 1.0F, 1.0F);
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix = pose.pose();
            Matrix3f normalMatrix = pose.normal();
            vertex(vertexConsumer, matrix, normalMatrix, -0.33F, -0.33F, 0.0F, 0.0F, 1.0F, r, g, b);
            vertex(vertexConsumer, matrix, normalMatrix, -0.33F, 0.33F, 0.0F, 0.0F, 0.0F, r, g, b);
            vertex(vertexConsumer, matrix, normalMatrix, 0.33F, 0.33F, 0.0F, 1.0F, 0.0F, r, g, b);
            vertex(vertexConsumer, matrix, normalMatrix, 0.33F, -0.33F, 0.0F, 1.0F, 1.0F, r, g, b);
            poseStack.popPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix, Matrix3f normalMatrix,
                               float x, float y, float z, float u, float v, float r, float g, float b) {
        vertexConsumer.vertex(matrix, x, y, z)
                .color(r, g, b, 1.0F)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
