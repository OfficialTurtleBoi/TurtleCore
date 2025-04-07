package net.turtleboi.turtlecore.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class UnleashFuryModel<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation UNLEASH_FURY_LAYER = new ModelLayerLocation(new ResourceLocation("modid", "unleashfury"), "main");
	private final ModelPart unleash_fury;

	public UnleashFuryModel(ModelPart root) {
		this.unleash_fury = root.getChild("unleash_fury");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition unleash_fury = partdefinition.addOrReplaceChild(
				"unleash_fury",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-24.0F, 0.0F, -24.0F, 48.0F, 1.0F, 48.0F,
								new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 192, 49);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		unleash_fury.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}