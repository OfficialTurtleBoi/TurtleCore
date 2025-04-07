package net.turtleboi.turtlecore.event;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.turtleboi.turtlecore.TurtleCore;
import net.turtleboi.turtlecore.client.renderer.FrozenRenderer;
import net.turtleboi.turtlecore.entity.CoreEntities;
import net.turtleboi.turtlecore.entity.client.SanctuaryDomeModel;
import net.turtleboi.turtlecore.entity.client.UnleashFuryModel;
import net.turtleboi.turtlecore.entity.client.renderer.abilities.UnleashFuryRenderer;
import net.turtleboi.turtlecore.entity.client.renderer.abilities.SanctuaryDomeRenderer;
import net.turtleboi.turtlecore.entity.client.renderer.weapons.ThrowableDaggerRenderer;
import net.turtleboi.turtlecore.entity.client.renderer.weapons.ThrowableHandaxeRenderer;
import net.turtleboi.turtlecore.particle.CoreParticles;
import net.turtleboi.turtlecore.particle.custom.*;
import net.turtleboi.turtlecore.util.CoreKeyBinding;

@Mod.EventBusSubscriber(modid = TurtleCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CoreClientModBusEvents {

    @SubscribeEvent
    public static void addFrozenLayer(EntityRenderersEvent.AddLayers event) {
        ForgeRegistries.ENTITY_TYPES.getValues().stream()
                .filter(entityType -> DefaultAttributes.hasSupplier(entityType))
                .filter(entityType -> entityType != EntityType.ENDER_DRAGON)
                .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                .forEach(entityType -> {
                    try {
                        LivingEntityRenderer<?, ?> renderer = event.getRenderer(entityType);
                        if (renderer != null) {
                            renderer.addLayer(new FrozenRenderer.FrozenLayer(renderer));
                            //System.out.println("Added FrozenLayer to renderer for entity type: " + ForgeRegistries.ENTITY_TYPES.getKey(entityType));
                        }
                    } catch (Exception e) {
                        //System.out.println("Could not add FrozenLayer for entity type: " + ForgeRegistries.ENTITY_TYPES.getKey(entityType));
                    }
                });

        for (String skinType : event.getSkins()) {
            LivingEntityRenderer<?, ?> renderer = event.getSkin(skinType);
            if (renderer != null) {
                renderer.addLayer(new FrozenRenderer.FrozenLayer(renderer));
                //System.out.println("Added FrozenLayer to skin renderer for skin type: " + skinType);
            }
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(CoreEntities.UNLEASH_FURY.get(), UnleashFuryRenderer::new);
        EntityRenderers.register(CoreEntities.SANCTUARY_DOME.get(), SanctuaryDomeRenderer::new);
        EntityRenderers.register(CoreEntities.THROWABLE_DAGGER.get(), ThrowableDaggerRenderer::new);
        EntityRenderers.register(CoreEntities.THROWABLE_HANDAXE.get(), ThrowableHandaxeRenderer::new);
    }

    @SubscribeEvent
    public  static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(CoreKeyBinding.LOCK_ON);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {

    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(UnleashFuryModel.UNLEASH_FURY_LAYER, UnleashFuryModel::createBodyLayer);
        event.registerLayerDefinition(SanctuaryDomeModel.SANCTUARY_DOME_LAYER, SanctuaryDomeModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(CoreParticles.LIFE_DRAIN_PARTICLES.get(),
                LifeDrainParticles.Provider::new);
        event.registerSpriteSet(CoreParticles.NONE_PARTICLES.get(),
                StunnedParticles.Provider::new);
        event.registerSpriteSet(CoreParticles.CHILLED_PARTICLES.get(),
                ChilledParticles.Provider::new);
        event.registerSpriteSet(CoreParticles.STUNNED_PARTICLES.get(),
                StunnedParticles.Provider::new);
        event.registerSpriteSet(CoreParticles.HEAL_PARTICLE.get(),
                HealParticle.Provider::new);
        event.registerSpriteSet(CoreParticles.SLEEP_PARTICLE.get(),
                SleepParticle.Provider::new);
    }
}

