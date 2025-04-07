package net.turtleboi.turtlecore.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.turtleboi.turtlecore.TurtleCore;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CoreDamageSources {

    public static final ResourceLocation BLEED_DAMAGE_ID = new ResourceLocation(TurtleCore.MOD_ID, "bleed");
    public static final ResourceLocation FROZEN_DAMAGE_ID = new ResourceLocation(TurtleCore.MOD_ID, "frozen");

    public static Holder<DamageType> frozenDamageType(Level level) {
        return level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, FROZEN_DAMAGE_ID));
    }

    public static Holder<DamageType> bleedDamageType(Level level) {
        return level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, BLEED_DAMAGE_ID));
    }

    public static DamageSource frozenDamage(Level level, @Nullable Entity attacker, @Nullable Entity directCause) {
        Holder<DamageType> holder = frozenDamageType(level);
        return new DamageSource(holder, attacker, directCause, attacker != null ? attacker.position() : null);
    }

    public static DamageSource causeBleedDamage(Level level, @Nullable Entity attacker, @Nullable Entity directCause) {
        Holder<DamageType> holder = bleedDamageType(level);
        return new DamageSource(holder, attacker, directCause, attacker != null ? attacker.position() : null);
    }

    public static boolean isBleedDamage(DamageSource source) {
        return source.typeHolder().equals(bleedDamageType(Objects.requireNonNull(source.getEntity()).level()));
    }
}
