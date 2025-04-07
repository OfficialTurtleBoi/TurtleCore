package net.turtleboi.turtlecore.potion;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.turtlecore.TurtleCore;
import net.turtleboi.turtlecore.effect.CoreEffects;

public class CorePotions {
    public static final DeferredRegister<Potion> POTIONS
            = DeferredRegister.create(ForgeRegistries.POTIONS, TurtleCore.MOD_ID);

    public static final RegistryObject<Potion> CHILLING_POTION = POTIONS.register("aspects_chilling_potion",
            () -> new Potion(new MobEffectInstance(CoreEffects.CHILLED.get(), 300, 0)));

    public static final RegistryObject<Potion> CHILLING_POTION2 = POTIONS.register("aspects_chilling_potion2",
            () -> new Potion(new MobEffectInstance(CoreEffects.CHILLED.get(), 300, 1)));

    public static final RegistryObject<Potion> FREEZING_POTION = POTIONS.register("aspects_freezing_potion",
            () -> new Potion(new MobEffectInstance(CoreEffects.FROZEN.get(), 300, 0)));

    public static final RegistryObject<Potion> STUNNING_POTION = POTIONS.register("aspects_stunning_potion",
            () -> new Potion(new MobEffectInstance(CoreEffects.STUNNED.get(), 300, 0)));

    public static final RegistryObject<Potion> VIGOR_POTION = POTIONS.register("aspects_vigor_potion",
            () -> new Potion(new MobEffectInstance(CoreEffects.VIGOR.get(), 300, 0)));

    public static final RegistryObject<Potion> VIGOR_POTION2 = POTIONS.register("aspects_vigor_potion2",
            () -> new Potion(new MobEffectInstance(CoreEffects.VIGOR.get(), 300, 1)));

    public static final RegistryObject<Potion> SAPPING_POTION = POTIONS.register("aspects_sapping_potion",
            () -> new Potion(new MobEffectInstance(CoreEffects.SAPPED.get(), 300, 0)));

    public static final RegistryObject<Potion> SAPPING_POTION2 = POTIONS.register("aspects_sapping_potion2",
            () -> new Potion(new MobEffectInstance(CoreEffects.SAPPED.get(), 300, 1)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
