package net.turtleboi.turtlecore.effect.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.ModList;
import net.turtleboi.turtlecore.compat.aspects.AspectsCompat;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.init.CoreAttributeModifiers;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;
import net.turtleboi.turtlecore.particle.CoreParticles;
import net.turtleboi.turtlecore.util.EffectApplicationUtil;

import java.util.*;

public class StunnedEffect extends MobEffect {
    public static final Map<UUID, StunPlayerData> stunnedPlayers = new HashMap<>();
    public StunnedEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        Player stunningPlayer = getStunner(pLivingEntity);

        if (pLivingEntity.hasEffect(CoreEffects.STUNNED.get())) {
            MobEffectInstance originalInstance = pLivingEntity.getEffect(CoreEffects.STUNNED.get());
            if (originalInstance.isVisible()) {
                originalInstance.update(
                        new MobEffectInstance(
                                CoreEffects.STUNNED.get(),
                                originalInstance.getDuration(),
                                originalInstance.getAmplifier(),
                                originalInstance.isAmbient(),
                                false,
                                originalInstance.showIcon()));
            }
        }

        if (!pLivingEntity.level().isClientSide &&
                (pLivingEntity.tickCount % 20 == 0 || pLivingEntity.tickCount % 18 == 0 || pLivingEntity.tickCount % 16 == 0)){
            double offX = (pLivingEntity.level().random.nextDouble() - 0.5) * 0.5;
            double offZ = (pLivingEntity.level().random.nextDouble() - 0.5) * 0.5;
            CoreNetworking.sendToNear(new SendParticlesS2C(
                    CoreParticles.STUNNED_PARTICLES.get(),
                    pLivingEntity.getX() + offX,
                    pLivingEntity.getY() + pLivingEntity.getBbHeight(),
                    pLivingEntity.getZ() + offZ,
                    0,0,0), pLivingEntity);
        }

        //System.out.println("Stunning: " + pLivingEntity);
        //System.out.println("Duration: " + duration);
        if (pLivingEntity instanceof Player player) {
            if (!stunnedPlayers.containsKey(player.getUUID())) {
                StunPlayerData data = new StunPlayerData(
                        player.getYRot(), player.getXRot(),
                        player.getAbilities().getWalkingSpeed(), player.getAbilities().getFlyingSpeed());
                stunnedPlayers.put(player.getUUID(), data);
                player.getAbilities().mayBuild = false;
                player.getAbilities().flying = false;
                player.getAbilities().setWalkingSpeed(0.0f);
                if (!player.isCreative()) {
                    player.getAbilities().setFlyingSpeed(0);
                    player.getAbilities().invulnerable = false;
                }
                player.getAbilities().setWalkingSpeed(data.savedWalkingSpeed);
                player.getAbilities().setFlyingSpeed(data.savedFlyingSpeed);
                player.onUpdateAbilities();
                player.setSprinting(false);
                player.setJumping(false);
            }
            StunPlayerData data = stunnedPlayers.get(player.getUUID());
            if (data != null) {
                player.setYRot(data.savedYaw);
                player.setXRot(data.savedPitch);
            }
            if (data != null) {
                player.setYRot(data.savedYaw);
                player.setXRot(data.savedPitch);
            }
            player.hurtMarked = true;
        } else if (pLivingEntity instanceof Mob mob) {
            if (mob instanceof Creeper creeperMob) {
                creeperMob.setSwellDir(-1);
            }
            mob.setNoAi(true);
            mob.hurtMarked = true;
            mob.setSprinting(false);
            mob.setJumping(false);
            mob.getNavigation().stop();
        }


        if(stunningPlayer != null) {
            // Only load if Aspects is loaded
            if (ModList.get().isLoaded("aspects")) {
                AspectsCompat.applyGlaciusTempestasSynergy(stunningPlayer, pLivingEntity);
            }
        }

        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        removeEntityStun(pLivingEntity);

        Player stunningPlayer = getStunner(pLivingEntity);
        if(stunningPlayer != null) {
            // Only load if Aspects is loaded
            if (ModList.get().isLoaded("aspects")) {
                AspectsCompat.applyTempestasUmbreSynergy(stunningPlayer, pLivingEntity);
                AspectsCompat.applyTempestasArcaniInfernumSynergy(stunningPlayer, pLivingEntity);
            }
        }
    }


    private static Player getStunner(LivingEntity livingEntity){
        Player stunningPlayer = null;
        if (livingEntity.getPersistentData().hasUUID("StunnedBy")) {
            stunningPlayer = livingEntity.level().getPlayerByUUID(livingEntity.getPersistentData().getUUID("StunnedBy"));
        }
        return stunningPlayer;
    }

    public static void removeEntityStun(LivingEntity pLivingEntity) {
        //System.out.println("Unstunning: " + pLivingEntity);
        if (pLivingEntity instanceof Player player) {
            stunnedPlayers.remove(player.getUUID());
            player.getAbilities().mayBuild = true;
            if (player.isCreative()) {
                player.getAbilities().invulnerable = true;
            }
            player.hurtMarked = true;
        } else if (pLivingEntity instanceof Mob mob) {
            mob.setNoAi(false);
            mob.hurtMarked = true;
        }
        pLivingEntity.setDeltaMovement(0 ,0 ,0);
    }

    public static class StunPlayerData {
        public final float savedYaw;
        public final float savedPitch;
        public final float savedWalkingSpeed;
        public final float savedFlyingSpeed;
        public StunPlayerData(float yaw, float pitch, float walkingSpeed, float flyingSpeed) {
            this.savedYaw = yaw;
            this.savedPitch = pitch;
            this.savedWalkingSpeed = walkingSpeed;
            this.savedFlyingSpeed = flyingSpeed;
        }
    }
}
