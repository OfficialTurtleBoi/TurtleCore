package net.turtleboi.turtlecore.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.turtlecore.TurtleCore;
import net.turtleboi.turtlecore.effect.effects.*;

public class CoreEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TurtleCore.MOD_ID);

    public static final RegistryObject<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding",
            () -> new BleedEffect(MobEffectCategory.HARMFUL, 15421520));

    public static final RegistryObject<MobEffect> ROOTED = MOB_EFFECTS.register("rooted",
            () -> new RootedEffect(MobEffectCategory.HARMFUL, 4928256));

    public static final RegistryObject<MobEffect> CHILLED = MOB_EFFECTS.register("chilled",
            () -> new ChilledEffect(MobEffectCategory.HARMFUL, 59903));

    public static final RegistryObject<MobEffect> FROZEN = MOB_EFFECTS.register("frozen",
            () -> new FrozenEffect(MobEffectCategory.HARMFUL, 8752371));

    public static final RegistryObject<MobEffect> STUNNED = MOB_EFFECTS.register("stunned",
            () -> new StunnedEffect(MobEffectCategory.HARMFUL, 13676558));

    public static final RegistryObject<MobEffect> SAPPED = MOB_EFFECTS.register("sapped",
            () -> new SappedEffect(MobEffectCategory.HARMFUL, 6422601));

    public static final RegistryObject<MobEffect> VIGOR = MOB_EFFECTS.register("vigor",
            () -> new VigorEffect(MobEffectCategory.BENEFICIAL, 15269961));

    public static final RegistryObject<MobEffect> SLEEP = MOB_EFFECTS.register("sleep",
            () -> new SleepEffect(MobEffectCategory.HARMFUL, 59903));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
