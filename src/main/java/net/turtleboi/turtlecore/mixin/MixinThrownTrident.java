package net.turtleboi.turtlecore.mixin;

import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.turtleboi.turtlecore.api.IThrownTridentAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ThrownTrident.class)
public class MixinThrownTrident implements IThrownTridentAccessor {
    @Shadow
    private ItemStack tridentItem;

    @Override
    public ItemStack getTridentItem() {
        return tridentItem;
    }
}
