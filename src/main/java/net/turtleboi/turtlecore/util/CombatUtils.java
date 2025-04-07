package net.turtleboi.turtlecore.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.event.CooldownResetEvent;

import java.util.HashMap;
import java.util.Map;

public class CombatUtils {
    private static final Map<Player, Boolean> executeFlagMap = new HashMap<>();
    public static final Map<Player, ItemStack> executeItemMap = new HashMap<>();

    public static void setExecuteFlag(Player player, boolean value, ItemStack item) {
        executeFlagMap.put(player, value);
        executeItemMap.put(player, item);
    }

    public static boolean isExecuteFlagSet(Player player) {
        return executeFlagMap.getOrDefault(player, false);
    }

    public static void clearExecuteFlag(Player player) {
        executeFlagMap.remove(player);
        executeItemMap.remove(player);
    }

    public static void applyExecuteDamage(Player player, LivingEntity target, LivingHurtEvent event, float baseDamage, Runnable executeCooldownReset) {
        if (player == null || target == null) return;
        Level level = player.level();
        float targetHealth = target.getHealth();
        float missingHealthPercent = 1 - (targetHealth / target.getMaxHealth());
        float bonusMultiplier = Math.min(0.25f + missingHealthPercent, 1.0f);
        float bonusDamage = baseDamage * bonusMultiplier;
        float totalDamage = baseDamage + bonusDamage;

        event.setAmount(totalDamage);
        spawnExecuteParticle(target);

        if (target.hasEffect(CoreEffects.BLEEDING.get()) && !target.hasEffect(CoreEffects.STUNNED.get())) {
            target.addEffect(new MobEffectInstance(CoreEffects.STUNNED.get(), 100, 1));
        }

        if (totalDamage >= targetHealth) {
            executeCooldownReset.run();
            level.playSound(null, player.getOnPos(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS,
                    1.25F, level.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    public static void spawnExecuteParticle(LivingEntity target) {
        if (target.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SWEEP_ATTACK,
                    target.getX(),
                    target.getY(0.5D),
                    target.getZ(),
                    10,
                    0.5,
                    0.5,
                    0.5,
                    0.1);
        }
    }

    public static void resetCooldown(Player player) {
        ItemStack item = executeItemMap.get(player);
        if (item != null) {
            player.getCooldowns().removeCooldown(item.getItem());
        }
        MinecraftForge.EVENT_BUS.post(new CooldownResetEvent(player));
    }
}
