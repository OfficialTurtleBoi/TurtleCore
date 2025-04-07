package net.turtleboi.turtlecore.effect.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.effects.FrozenDataS2C;
import net.turtleboi.turtlecore.network.packet.effects.SleepDataS2C;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;
import net.turtleboi.turtlecore.particle.CoreParticles;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SleepEffect extends MobEffect {
    public static final Map<UUID, SleepPlayerData> sleptPlayers = new HashMap<>();
    public SleepEffect(MobEffectCategory mobEffectCategory, int color){
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier){
        CoreNetworking.sendToAllPlayers(new SleepDataS2C(pLivingEntity.getId(), true));

        if (pLivingEntity.hasEffect(CoreEffects.SLEEP.get())) {
            MobEffectInstance originalInstance = pLivingEntity.getEffect(CoreEffects.SLEEP.get());
            if (originalInstance.isVisible()) {
                originalInstance.update(
                        new MobEffectInstance(
                                CoreEffects.SLEEP.get(),
                                originalInstance.getDuration(),
                                originalInstance.getAmplifier(),
                                originalInstance.isAmbient(),
                                false,
                                originalInstance.showIcon()));
            }
        }

        if (!pLivingEntity.level().isClientSide &&
                (pLivingEntity.tickCount % 20 == 0 || pLivingEntity.tickCount % 20 == 3 || pLivingEntity.tickCount % 20 == 6)) {
            Vec3 lookVector = pLivingEntity.getLookAngle();
            double velocityX = lookVector.x * 0.05 + 0.1;
            double velocityY = 0.1;
            double velocityZ = lookVector.z * 0.05;
            double particleX = pLivingEntity.getX();
            double particleY = pLivingEntity.getY() + pLivingEntity.getBbHeight();
            double particleZ = pLivingEntity.getZ();
            if (pLivingEntity.tickCount % 20 == 0) {
                CoreNetworking.sendToNear(new SendParticlesS2C(
                        CoreParticles.SLEEP_PARTICLE.get(),
                        particleX, particleY, particleZ,
                        velocityX * 1.5, velocityY * 1.5, velocityZ * 1.5)
                        , pLivingEntity);
            } else if (pLivingEntity.tickCount % 20 == 3) {
                CoreNetworking.sendToNear(new SendParticlesS2C(
                        CoreParticles.SLEEP_PARTICLE.get(),
                        particleX, particleY, particleZ,
                        velocityX, velocityY, velocityZ)
                        , pLivingEntity);
            } else if (pLivingEntity.tickCount % 20 == 6) {
                CoreNetworking.sendToNear(new SendParticlesS2C(
                        CoreParticles.SLEEP_PARTICLE.get(),
                        particleX, particleY, particleZ,
                        velocityX * 0.66, velocityY * 0.66, velocityZ * 0.66)
                        , pLivingEntity);
            }
        }

        if (pLivingEntity instanceof Player player) {
            if (!sleptPlayers.containsKey(player.getUUID())) {
                SleepPlayerData data = new SleepPlayerData(
                        player.getYRot(), player.getXRot(),
                        player.getAbilities().getWalkingSpeed(), player.getAbilities().getFlyingSpeed());
                sleptPlayers.put(player.getUUID(), data);
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
            SleepPlayerData data = sleptPlayers.get(player.getUUID());
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

        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier){
        return true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        removeEntitySleep(pLivingEntity);
    }

    public static void removeEntitySleep(LivingEntity pLivingEntity) {
        //System.out.println("Waking: " + pLivingEntity);
        if (pLivingEntity instanceof Player player) {
            sleptPlayers.remove(player.getUUID());
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

    public static class SleepPlayerData {
        public final float savedYaw;
        public final float savedPitch;
        public final float savedWalkingSpeed;
        public final float savedFlyingSpeed;
        public SleepPlayerData(float yaw, float pitch, float walkingSpeed, float flyingSpeed) {
            this.savedYaw = yaw;
            this.savedPitch = pitch;
            this.savedWalkingSpeed = walkingSpeed;
            this.savedFlyingSpeed = flyingSpeed;
        }
    }
}
