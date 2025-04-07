package net.turtleboi.turtlecore.item.weapon;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.enchantment.CoreEnchantments;
import net.turtleboi.turtlecore.entity.weapons.ThrowableDagger;
import net.turtleboi.turtlecore.init.CoreAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractDaggerItem extends SwordItem {
    public AbstractDaggerItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    private static final UUID THROWN_DAMAGE_MODIFIER_UUID = UUID.nameUUIDFromBytes("thrown".getBytes());

    protected abstract ResourceLocation getDaggerTexture();

    protected float getThrownDamage() {
        return (super.getDamage() / 2.0F) + 0.5F;
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(equipmentSlot, stack);
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(modifiers);
            builder.put(
                    CoreAttributes.THROWN_DAMAGE.get(),
                    new AttributeModifier(
                            THROWN_DAMAGE_MODIFIER_UUID,
                            "Weapon modifier",
                            this.getThrownDamage(),
                            AttributeModifier.Operation.ADDITION
                    ));
            modifiers = builder.build();
        }
        return modifiers;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            float thrownDamage = getThrownDamage();
            ThrowableDagger dagger = new ThrowableDagger(level, player, itemstack, getDaggerTexture(), thrownDamage - 1);
            Vec3 direction = player.getViewVector(1.0F);
            double offsetDistance = 0.0;
            double posX = player.getX() + direction.x * offsetDistance;
            double posY = player.getEyeY() - 0.1F + direction.y * offsetDistance;
            double posZ = player.getZ() + direction.z * offsetDistance;
            dagger.setPos(posX, posY, posZ);
            dagger.shoot(direction.x, direction.y, direction.z, 1.5F, 1.0F);
            level.addFreshEntity(dagger);
        }
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, @NotNull Enchantment enchantment) {
        if (enchantment == Enchantments.LOYALTY ||
                enchantment == CoreEnchantments.SEEKING.get() ||
                enchantment == CoreEnchantments.RUPTURE.get()) {
            return true;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }
}
