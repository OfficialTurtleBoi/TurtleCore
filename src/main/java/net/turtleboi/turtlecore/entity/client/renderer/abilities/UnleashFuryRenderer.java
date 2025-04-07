package net.turtleboi.turtlecore.entity.client.renderer.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.turtleboi.turtlecore.TurtleCore;
import net.turtleboi.turtlecore.entity.abilities.UnleashFuryEntity;
import net.turtleboi.turtlecore.entity.client.UnleashFuryModel;
import org.jetbrains.annotations.NotNull;

public class UnleashFuryRenderer extends EntityRenderer<UnleashFuryEntity> {
    private final UnleashFuryModel<UnleashFuryEntity> model;

    public UnleashFuryRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new UnleashFuryModel<>(context.bakeLayer(UnleashFuryModel.UNLEASH_FURY_LAYER));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull UnleashFuryEntity pEntity) {
        int textureIndex = pEntity.getTextureIndex();
        return new ResourceLocation(TurtleCore.MOD_ID, "textures/entity/unleash_fury/unleash_fury_" + textureIndex + ".png");
    }

    @Override
    public void render(UnleashFuryEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
        poseStack.scale( 1.5F, 1.5F, 1.5F);
        VertexConsumer consumer = bufferSource.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        model.renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1.0F);
        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }
}
