package net.turtleboi.turtlecore.entity.weapons;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.turtleboi.turtlecore.entity.CoreEntities;

public class ThrowableDagger extends AbstractThrowableWeapon {
    public ThrowableDagger(EntityType<? extends ThrowableDagger> entityType, Level level) {
        super(entityType, level);
    }

    public ThrowableDagger(Level level, LivingEntity thrower, ItemStack itemStack, ResourceLocation resourceLocation, Float thrownDamage) {
        super(CoreEntities.THROWABLE_DAGGER.get(), level, thrower, itemStack, resourceLocation, thrownDamage);
    }

    @Override
    protected ItemStack getDefaultWeaponItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected String getWeaponTagName() {
        return "Dagger";
    }
}
