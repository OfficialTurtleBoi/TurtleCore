package net.turtleboi.turtlecore.event;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.turtleboi.turtlecore.TurtleCore;
import net.turtleboi.turtlecore.capabilities.party.PlayerPartyProvider;
import net.turtleboi.turtlecore.capabilities.targeting.PlayerTargetingProvider;
import net.turtleboi.turtlecore.compat.aspects.AspectsCompat;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.init.CoreDamageSources;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.targeting.ToggleLockS2CPacket;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;
import net.turtleboi.turtlecore.util.CombatUtils;
import net.turtleboi.turtlecore.util.TargetingUtils;

@Mod.EventBusSubscriber(modid = TurtleCore.MOD_ID)
public class CoreEvents {

    @SubscribeEvent
    public static void onAttachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!event.getObject().getCapability(PlayerPartyProvider.PLAYER_PARTY).isPresent()) {
                event.addCapability(new ResourceLocation(TurtleCore.MOD_ID, "player_party"), new PlayerPartyProvider());
            }
            if (!event.getObject().getCapability(PlayerTargetingProvider.PLAYER_TARGET).isPresent()) {
                event.addCapability(new ResourceLocation(TurtleCore.MOD_ID, "player_target"), new PlayerTargetingProvider(player));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(PlayerPartyProvider.PLAYER_PARTY).ifPresent(oldStore ->
                    event.getEntity().getCapability(PlayerPartyProvider.PLAYER_PARTY).ifPresent(newStore ->
                            newStore.copyFrom(oldStore)));
            event.getOriginal().getCapability(PlayerTargetingProvider.PLAYER_TARGET).ifPresent(oldStore ->
                    event.getEntity().getCapability(PlayerTargetingProvider.PLAYER_TARGET).ifPresent(newStore ->
                            newStore.copyFrom(oldStore)));
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event){
        if(!event.getLevel().isClientSide){
            if(event.getEntity() instanceof ServerPlayer player){
                TargetingUtils.resetLockOn(player);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity targetEntity = event.getEntity();
        Entity sourceEntity = event.getSource().getEntity();
        if (targetEntity instanceof Player player) {
            //Logic for if player gets hurt by something
        }


        if (sourceEntity instanceof Player player) {
             //Logic for if player hurts something, but that also handled by onPlayerAttack
        }
    }

    @SubscribeEvent
    public static void onEntityHurtPost(LivingDamageEvent event){
        Entity hurtEntity = event.getEntity();
        if (hurtEntity instanceof LivingEntity livingEntity) {
            if (livingEntity.hasEffect(CoreEffects.FROZEN.get())) {
                MobEffectInstance effectInstance = livingEntity.getEffect(CoreEffects.FROZEN.get());
                if (effectInstance != null) {
                    livingEntity.removeEffect(CoreEffects.FROZEN.get());
                }
                float entityMaxHealth = livingEntity.getMaxHealth();
                int maxDamage = 10;

                if (ModList.get().isLoaded("aspects")) {
                    if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                        double hurtArcaniFactor = AspectsCompat.getArcaniFactor(livingEntity);
                        double glaciusAmplifier = AspectsCompat.getGlaciusAmplifier(attacker);
                        double attackerArcaniFactor = AspectsCompat.getArcaniFactor(attacker);
                        maxDamage = (int) ((10 + (10 * (glaciusAmplifier / 4) * attackerArcaniFactor) / hurtArcaniFactor));
                    }
                }

                //System.out.println("Dealing " + maxDamage + " damage to " + hurtEntity);
                hurtEntity.hurt(
                        CoreDamageSources.frozenDamage(
                                hurtEntity.level(),
                                null,
                                null),
                        Math.min(maxDamage, entityMaxHealth / 4));
                hurtEntity.level().playSound(
                        null,
                        hurtEntity.getX(),
                        hurtEntity.getY(),
                        hurtEntity.getZ(),
                        SoundEvents.GLASS_BREAK,
                        SoundSource.AMBIENT,
                        1.25F,
                        0.4f / (hurtEntity.level().getRandom().nextFloat() * 0.4f + 0.8f)
                );
                double entitySize = hurtEntity.getBbHeight() * hurtEntity.getBbWidth();
                //System.out.println("Spawning " + (entitySize * 60) + " particles for " + hurtEntity.getName());
                RandomSource random = hurtEntity.level().getRandom();
                int count = (int) (entitySize * 60);
                for (int i = 0; i < count; i++) {
                    double offX = (random.nextDouble() - 0.5) * 0.5;
                    double offY = hurtEntity.getBbHeight() / 2;
                    double offZ = (random.nextDouble() - 0.5) * 0.5;
                    double theta = random.nextDouble() * Math.PI;
                    double phi = random.nextDouble() * 2 * Math.PI;
                    double speed = 0.5 + random.nextDouble() * 0.5;
                    double xSpeed = speed * Math.sin(theta) * Math.cos(phi);
                    double ySpeed = speed * Math.cos(theta);
                    double zSpeed = speed * Math.sin(theta) * Math.sin(phi);
                    ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState());

                    CoreNetworking.sendToNear(new SendParticlesS2C(
                            particle,
                            livingEntity.getX() + offX,
                            livingEntity.getY() + offY,
                            livingEntity.getZ() + offZ,
                            xSpeed, ySpeed, zSpeed), livingEntity);
                }
            }

            if (livingEntity.hasEffect(CoreEffects.SLEEP.get())) {
                MobEffectInstance effectInstance = livingEntity.getEffect(CoreEffects.SLEEP.get());
                if (effectInstance != null) {
                    livingEntity.removeEffect(CoreEffects.SLEEP.get());
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerAttack(LivingHurtEvent event) {
        //Execute Logic
        DamageSource source = event.getSource();
        Entity trueSource = source.getEntity();
        if (!(trueSource instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (!(target instanceof LivingEntity)) return;
        // Check if the execute flag is set for the player
        if (CombatUtils.isExecuteFlagSet(player)) {
            float baseDamage = event.getAmount();
            CombatUtils.applyExecuteDamage(
                    player,
                    target,
                    event,
                    baseDamage,
                    () -> CombatUtils.resetCooldown(player)
            );
            CombatUtils.clearExecuteFlag(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            ServerPlayer serverPlayer = (ServerPlayer) event.player;
            if (event.phase == TickEvent.Phase.START) {

            }

            if (event.phase == TickEvent.Phase.END) {
                serverPlayer.getCapability(PlayerTargetingProvider.PLAYER_TARGET).ifPresent(targetData -> {
                    LivingEntity target = targetData.getLockedTarget();
                    if (targetData.isLockedOn()) {
                        if (target == null || !target.isAlive() || !target.isAddedToWorld() || target.distanceToSqr(serverPlayer) >= 625D) {
                            TargetingUtils.resetLockOn(serverPlayer);
                        }
                    }
                });
            }
        }
    }
}
