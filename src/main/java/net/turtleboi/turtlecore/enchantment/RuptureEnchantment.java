package net.turtleboi.turtlecore.enchantment;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.effect.effects.BleedEffect;
import org.jetbrains.annotations.NotNull;

public class RuptureEnchantment extends Enchantment {
    public RuptureEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... slots) {
        super(rarity, category, slots);
    }

    @Override
    public int getMinCost(int level) {
        return 20 + (10 * (level - 1));
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void doPostAttack(@NotNull LivingEntity user, @NotNull Entity target, int level) {
        if (target instanceof LivingEntity livingTarget) {
            applyEffect(livingTarget, user, level);
        }
    }

    public void applyEffect(LivingEntity targetEntity, LivingEntity sourceEntity, int level) {
        BleedEffect.applyOrAmplifyBleed(targetEntity, 100, level - 1, sourceEntity);
        //BleedEffect.setSource(sourceEntity);
    }


    @Override
    public boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment != Enchantments.FIRE_ASPECT && enchantment != Enchantments.FLAMING_ARROWS;
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack itemStack) {
        return itemStack.is(Items.BOW) || itemStack.is(Items.CROSSBOW) || itemStack.is(Items.TRIDENT) || super.canApplyAtEnchantingTable(itemStack);
    }
}
