package net.turtleboi.turtlecore.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.turtlecore.TurtleCore;
import net.turtleboi.turtlecore.entity.abilities.SanctuaryDomeEntity;
import net.turtleboi.turtlecore.entity.abilities.UnleashFuryEntity;
import net.turtleboi.turtlecore.entity.weapons.ThrowableDagger;
import net.turtleboi.turtlecore.entity.weapons.ThrowableHandaxe;

public class CoreEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TurtleCore.MOD_ID);

    public static final RegistryObject<EntityType<UnleashFuryEntity>> UNLEASH_FURY =
            ENTITY_TYPES.register("unleash_fury" , () -> EntityType.Builder.of(UnleashFuryEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.1F)
                    .build("unleash_fury"));

    public static final RegistryObject<EntityType<SanctuaryDomeEntity>> SANCTUARY_DOME =
            ENTITY_TYPES.register("sanctuary_dome" , () -> EntityType.Builder.of(SanctuaryDomeEntity::new, MobCategory.MISC)
                    .sized(5F, 5F)
                    .build("sanctuary_dome"));

    public static final RegistryObject<EntityType<ThrowableDagger>> THROWABLE_DAGGER = ENTITY_TYPES.register("throwable_dagger",
            () -> EntityType.Builder.<ThrowableDagger>of(ThrowableDagger::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(TurtleCore.MOD_ID, "throwable_dagger").toString())
    );

    public static final RegistryObject<EntityType<ThrowableHandaxe>> THROWABLE_HANDAXE = ENTITY_TYPES.register("throwable_handaxe",
            () -> EntityType.Builder.<ThrowableHandaxe>of(ThrowableHandaxe::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(TurtleCore.MOD_ID, "throwable_handaxe").toString())
    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
