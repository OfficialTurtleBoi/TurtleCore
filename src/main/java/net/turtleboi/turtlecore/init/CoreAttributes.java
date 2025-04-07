package net.turtleboi.turtlecore.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.turtlecore.TurtleCore;

@Mod.EventBusSubscriber(modid = TurtleCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CoreAttributes {
    public static final DeferredRegister<Attribute> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TurtleCore.MOD_ID);
    //Ranged damage modifier
    public static final RegistryObject<Attribute> RANGED_DAMAGE =
            create("ranged_damage_bonus", 1D, 1D);

    public static final RegistryObject<Attribute> COOLDOWN_REDUCTION =
            create("cooldown_reduction", 100.0D, 0D);

    public static final RegistryObject<Attribute> LIFE_STEAL =
            create("life_steal", 0.0D, 0D);

    public static final RegistryObject<Attribute> PLAYER_SIZE =
            create("player_size", 1.0D, 0D);

    public static final RegistryObject<Attribute> CRITICAL_CHANCE =
            create("critical_chance", 5.0D, 0D);

    public static final RegistryObject<Attribute> CRITICAL_DAMAGE =
            create("critical_damage", 1.5D, 1.5D);

    public static final RegistryObject<Attribute> DAMAGE_RESISTANCE =
            create("damage_resistance", 100.0D, 0D);

    public static final RegistryObject<Attribute> HEALING_EFFECTIVENESS =
            create("healing_effectiveness", 1.0D, 0D);

    public static final RegistryObject<Attribute> ARMOR_PENETRATION =
            create("armor_penetration", 0.0D, 0D);

    public static final RegistryObject<Attribute> DODGE_CHANCE =
            create("dodge_chance", 0.0D, 0D);

    public static final RegistryObject<Attribute> HIT_CHANCE =
            create("hit_chance", 1D, 0D);

    public static final RegistryObject<Attribute> THROWN_DAMAGE =
            create("thrown_damage", 0.0D, 0D);

    public static final RegistryObject<Attribute> MAGIC_AMP =
            create("magic_amp", 1D, 0D);

    private static RegistryObject<Attribute> create(
            String name, double defaultValue, double minValue) {
        String descriptionId = "attribute.name." + TurtleCore.MOD_ID + "." + name;
        return REGISTRY.register(
                name,
                () -> new RangedAttribute(descriptionId, defaultValue, minValue, 1024D).setSyncable(true));
    }

    @SubscribeEvent
    public static void attachAttributes(EntityAttributeModificationEvent event) {
        REGISTRY.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(attribute -> event.add(EntityType.PLAYER, attribute));
    }
}
