package net.turtleboi.turtlecore.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.turtleboi.turtlecore.TurtleCore;
import net.turtleboi.turtlecore.client.data.TargetingClientData;
import net.turtleboi.turtlecore.client.renderer.TargetArrowRenderer;
import net.turtleboi.turtlecore.client.util.ParticleSpawnQueue;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.effect.effects.SleepEffect;
import net.turtleboi.turtlecore.effect.effects.StunnedEffect;
import net.turtleboi.turtlecore.init.CoreAttributes;
import net.turtleboi.turtlecore.item.weapon.AbstractDaggerItem;
import net.turtleboi.turtlecore.item.weapon.AbstractHandaxeItem;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.targeting.TargetC2SPacket;
import net.turtleboi.turtlecore.network.packet.targeting.ToggleLockC2SPacket;
import net.turtleboi.turtlecore.util.CoreKeyBinding;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public class CoreClientEvents {
    @Mod.EventBusSubscriber(modid = TurtleCore.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (CoreKeyBinding.LOCK_ON.consumeClick()) {
                CoreNetworking.sendToServer(new ToggleLockC2SPacket());
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            LocalPlayer clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer != null) {
                UUID uuid = clientPlayer.getUUID();
                if (StunnedEffect.stunnedPlayers.containsKey(uuid)) {
                    StunnedEffect.StunPlayerData data = StunnedEffect.stunnedPlayers.get(uuid);
                    clientPlayer.setYRot(data.savedYaw);
                    clientPlayer.setXRot(data.savedPitch);
                    clientPlayer.yRotO = data.savedYaw;
                    clientPlayer.xRotO = data.savedPitch;
                }

                if (SleepEffect.sleptPlayers.containsKey(uuid)) {
                    SleepEffect.SleepPlayerData data = SleepEffect.sleptPlayers.get(uuid);
                    clientPlayer.setYRot(data.savedYaw);
                    clientPlayer.setXRot(data.savedPitch);
                    clientPlayer.yRotO = data.savedYaw;
                    clientPlayer.xRotO = data.savedPitch;
                }
            }

            ParticleSpawnQueue.tick();
        }

        @SubscribeEvent
        public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && (player.hasEffect(CoreEffects.STUNNED.get()) || player.hasEffect(CoreEffects.FROZEN.get())) || player.hasEffect(CoreEffects.SLEEP.get())) {
                event.getInput().forwardImpulse = 0f;
                event.getInput().leftImpulse = 0f;
                event.getInput().jumping = false;
                event.getInput().shiftKeyDown = false;
            }
        }

        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            if (stack.getItem() instanceof AbstractDaggerItem || stack.getItem() instanceof AbstractHandaxeItem) {
                for (int i = 0; i < event.getToolTip().size(); i++) {
                    Component tooltipLine = event.getToolTip().get(i);
                    if (tooltipLine.getString().contains("Thrown Damage")) {
                        String newText = tooltipLine.getString().replace("+", " ");
                        Component newTooltip = Component.literal(newText).withStyle(ChatFormatting.DARK_GREEN);
                        event.getToolTip().set(i, newTooltip);
                    }
                }
            }
        }

        @Mod.EventBusSubscriber(modid = TurtleCore.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
        public static class RenderEvents {

            @SubscribeEvent
            public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
                Player player = event.getEntity();
                PoseStack poseStack = event.getPoseStack();
                float scale = (float) player.getAttributeValue(CoreAttributes.PLAYER_SIZE.get());

                if (scale != 1.0F) {
                    poseStack.pushPose();
                    poseStack.scale(scale, scale, scale);
                }
            }

            @SubscribeEvent
            public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
                Minecraft minecraft = Minecraft.getInstance();
                Player player = event.getEntity();
                PoseStack poseStack = event.getPoseStack();
                MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
                float scale = (float) player.getAttributeValue(CoreAttributes.PLAYER_SIZE.get());

                if (scale != 1.0F) {
                    poseStack.popPose();
                }
                bufferSource.endBatch();
            }

            @SubscribeEvent (priority = EventPriority.HIGHEST)
            public static void onRenderLiving(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
                Minecraft minecraft = Minecraft.getInstance();
                Player player = minecraft.player;
                if (player != null){
                    CoreNetworking.sendToServer(new TargetC2SPacket());
                    LivingEntity target = TargetingClientData.getLivingTarget(player.level());
                    if (target == event.getEntity()) {
                        PoseStack poseStack = event.getPoseStack();
                        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.enableDepthTest();
                        RenderSystem.depthFunc(GL11.GL_ALWAYS);
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        TargetArrowRenderer.renderTargetArrow(poseStack, bufferSource, minecraft, target, player);
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        RenderSystem.disableBlend();
                        RenderSystem.depthFunc(GL11.GL_LEQUAL);
                        RenderSystem.disableDepthTest();
                        bufferSource.endBatch();
                    }
                }
            }
        }
    }
}
