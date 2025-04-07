package net.turtleboi.turtlecore.entity.client.renderer.weapons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.turtleboi.turtlecore.entity.weapons.ThrowableHandaxe;
import org.jetbrains.annotations.NotNull;

public class ThrowableHandaxeRenderer extends EntityRenderer<ThrowableHandaxe> {

    public ThrowableHandaxeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ThrowableHandaxe entity, float yaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();

        if (!entity.hasHitBlock() || entity.isReturning() || entity.shouldFall()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(90F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180F));

            if (!entity.isReturning() || !entity.shouldFall()) {
                float spin = (entity.tickCount + partialTicks) * -20.0F;
                poseStack.mulPose(Axis.XP.rotationDegrees(spin));
            }

            entity.lastYP = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
            entity.lastZP = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.lastYP - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(entity.lastZP + 90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(90F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
            float rotateVariation = entity.getRotateVariation();
            poseStack.mulPose(Axis.XP.rotationDegrees(20F + rotateVariation));
        }

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack = entity.getPickupItem();

        itemRenderer.renderStatic(null, itemStack, ItemDisplayContext.HEAD, false, poseStack, bufferSource, entity.level(), light, OverlayTexture.NO_OVERLAY, entity.getId());

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrowableHandaxe entity) {
        return entity.getTexture();
    }
}
