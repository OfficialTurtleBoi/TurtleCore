package net.turtleboi.turtlecore.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.turtlecore.TurtleCore;

import java.util.function.Supplier;

public class CoreParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TurtleCore.MOD_ID);

    public static final RegistryObject<SimpleParticleType> LIFE_DRAIN_PARTICLES =
            PARTICLE_TYPES.register("life_drain_particles", () -> new SimpleParticleType(true));

    public static final Supplier<SimpleParticleType> NONE_PARTICLES =
            PARTICLE_TYPES.register("none_particles", () -> new SimpleParticleType(true));

    public static final Supplier<SimpleParticleType> CHILLED_PARTICLES =
            PARTICLE_TYPES.register("chilled_particles", () -> new SimpleParticleType(true));

    public static final Supplier<SimpleParticleType> STUNNED_PARTICLES =
            PARTICLE_TYPES.register("stunned_particles", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> HEAL_PARTICLE =
            PARTICLE_TYPES.register("heal_particles", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SLEEP_PARTICLE =
            PARTICLE_TYPES.register("sleep_particles", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
