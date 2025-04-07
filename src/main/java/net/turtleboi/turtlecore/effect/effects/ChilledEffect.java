package net.turtleboi.turtlecore.effect.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.turtleboi.turtlecore.compat.aspects.AspectsCompat;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.init.CoreAttributeModifiers;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;
import net.turtleboi.turtlecore.particle.CoreParticles;

import java.util.UUID;

public class ChilledEffect extends MobEffect {
    private final String attributeModifierName = "chilled_movement_speed";
    public ChilledEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.hasEffect(CoreEffects.CHILLED.get())) {
            MobEffectInstance originalInstance = pLivingEntity.getEffect(CoreEffects.CHILLED.get());
            if (originalInstance.isVisible()) {
                originalInstance.update(
                        new MobEffectInstance(
                                CoreEffects.CHILLED.get(),
                                originalInstance.getDuration(),
                                originalInstance.getAmplifier(),
                                originalInstance.isAmbient(),
                                false,
                                originalInstance.showIcon()));
            }
        }

        if (!pLivingEntity.level().isClientSide && pLivingEntity.tickCount % 5 == 0){
            for (int i = 0; i < ((pAmplifier + 1) * 2); i++) {
                double offX = (pLivingEntity.level().random.nextDouble() - 0.5) * 0.5;
                double offY = pLivingEntity.getBbHeight() * pLivingEntity.level().random.nextDouble();
                double offZ = (pLivingEntity.level().random.nextDouble() - 0.5) * 0.5;
                CoreNetworking.sendToNear(new SendParticlesS2C(
                        CoreParticles.CHILLED_PARTICLES.get(),
                        pLivingEntity.getX() + offX,
                        pLivingEntity.getY() + offY,
                        pLivingEntity.getZ() + offZ,
                        0,0,0), pLivingEntity);
            }
        }

        if (!pLivingEntity.level().isClientSide()) {
            if(pLivingEntity instanceof Mob mob) {
                CoreAttributeModifiers.applyPermanentModifier(
                        mob,
                        Attributes.MOVEMENT_SPEED,
                        attributeModifierName,
                        -0.125 * (1 + pAmplifier),
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
            }

            if(pLivingEntity instanceof Player player) {
                CoreAttributeModifiers.applyPermanentModifier(
                        player,
                        Attributes.MOVEMENT_SPEED,
                        attributeModifierName,
                        -0.2 * (1 + pAmplifier),
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
            }

            int freezeDuration = 100;

            Player chillingPlayer = getChiller(pLivingEntity);
            if (chillingPlayer != null) {
                // Only load if Aspects is loaded
                if (ModList.get().isLoaded("aspects")) {
                    freezeDuration = AspectsCompat.getGlaciusFreezeDuration(chillingPlayer, freezeDuration);
                    AspectsCompat.applyGlaciusTerraSynergy(chillingPlayer, pAmplifier);
                }
            }

            if (pAmplifier > 3) {
                pLivingEntity.removeEffect(CoreEffects.CHILLED.get());
                pLivingEntity.addEffect(new MobEffectInstance(CoreEffects.FROZEN.get(), freezeDuration, 0));
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
        CoreAttributeModifiers.removeModifier(
                pLivingEntity,
                Attributes.MOVEMENT_SPEED,
                attributeModifierName);
    }

    private static Player getChiller(LivingEntity livingEntity){
        Player chillingPlayer = null;
        if (livingEntity.getPersistentData().hasUUID("ChilledBy")) {
            chillingPlayer = livingEntity.level().getPlayerByUUID(livingEntity.getPersistentData().getUUID("ChilledBy"));
        }
        return chillingPlayer;
    }
}
