package net.turtleboi.turtlecore.effect.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.init.CoreDamageSources;
import org.jetbrains.annotations.NotNull;

public class BleedEffect extends MobEffect {
    private static Entity source = null;

    public BleedEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide) {
            float damage = 1.0F + amplifier;
            livingEntity.hurt(CoreDamageSources.causeBleedDamage(livingEntity.level(), source, source), damage);
        }
        super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int interval = Math.max(10, 40 - (10 * amplifier));
        return duration % interval == 0;
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
    }

    public static void applyOrAmplifyBleed(LivingEntity entity, int duration, int amplifier, Entity source) {
        if (source != null) {
            BleedEffect.source = source;
        }
        MobEffectInstance existingEffect = entity.getEffect(CoreEffects.BLEEDING.get());
        if (existingEffect != null) {
            int newAmplifier = existingEffect.getAmplifier() + amplifier + 1;
            int newDuration = Math.max(existingEffect.getDuration(), duration);
            MobEffectInstance newEffect = new MobEffectInstance(CoreEffects.BLEEDING.get(), newDuration, newAmplifier);
            entity.addEffect(newEffect);
        } else {
            entity.addEffect(new MobEffectInstance(CoreEffects.BLEEDING.get(), duration, amplifier));
        }
    }
}
