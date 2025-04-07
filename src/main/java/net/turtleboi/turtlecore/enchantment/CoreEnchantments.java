package net.turtleboi.turtlecore.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.turtlecore.TurtleCore;

public class CoreEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, TurtleCore.MOD_ID);

    public static RegistryObject<Enchantment> RUPTURE =
            ENCHANTMENTS.register("rupture",
                    () -> new RuptureEnchantment(Enchantment.Rarity.UNCOMMON,
                            EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND));

    public static RegistryObject<Enchantment> SEEKING =
            ENCHANTMENTS.register("seeking",
                    () -> new SeekingEnchantment(Enchantment.Rarity.VERY_RARE,
                            EnchantmentCategory.BOW, EquipmentSlot.MAINHAND));

    public static void register(IEventBus eventBus){
        ENCHANTMENTS.register(eventBus);
    }
}
