package net.turtleboi.turtlecore.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class SanctuaryDomeModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation SANCTUARY_DOME_LAYER = new ModelLayerLocation(new ResourceLocation("modid", "sanctuarydome"), "main");
    private final ModelPart sanctuary_dome;

    public SanctuaryDomeModel(ModelPart root) {
        this.sanctuary_dome = root.getChild("sanctuary_dome");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition sanctuary_dome = partdefinition.addOrReplaceChild("sanctuary_dome", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = sanctuary_dome.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-40.0F, -64.0F, -40.0F, 80.0F, 64.0F, 80.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 320, 144);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        sanctuary_dome.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}