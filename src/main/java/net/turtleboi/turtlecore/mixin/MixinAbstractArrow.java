package net.turtleboi.turtlecore.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.turtleboi.turtlecore.api.IThrownTridentAccessor;
import net.turtleboi.turtlecore.enchantment.CoreEnchantments;
import net.turtleboi.turtlecore.enchantment.SeekingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class MixinAbstractArrow {
    @Inject(method = "tick", at = @At("TAIL"))
    public void injectTick(CallbackInfo info) {
        AbstractArrow projectile = (AbstractArrow) (Object) this;

        Entity owner = projectile.getOwner();
        if (owner instanceof LivingEntity shooter) {
            ItemStack seekerWeapon = shooter.getMainHandItem();
            int seekingLevel = EnchantmentHelper.getTagEnchantmentLevel(CoreEnchantments.SEEKING.get(), seekerWeapon);
            if (seekingLevel > 0 && (seekerWeapon.getItem() instanceof BowItem || seekerWeapon.getItem() instanceof CrossbowItem)) {
              if (shooter instanceof Player player) {
                  if (CoreEnchantments.SEEKING.get() instanceof SeekingEnchantment seekingEnchantment) {
                      if (seekingEnchantment.isSeeking(projectile)) {
                          seekingEnchantment.applyEffect(player, projectile, seekingLevel);
                      }
                  }
              }
            }

            if (projectile instanceof ThrownTrident thrownTrident) {
                ItemStack tridentItem = ((IThrownTridentAccessor) thrownTrident).getTridentItem();
                int tridentSeekingLevel = EnchantmentHelper.getTagEnchantmentLevel(CoreEnchantments.SEEKING.get(), tridentItem);
                if (tridentSeekingLevel > 0) {
                    if (shooter instanceof Player player) {
                        if (CoreEnchantments.SEEKING.get() instanceof SeekingEnchantment seekingEnchantment) {
                            if (seekingEnchantment.isSeeking(projectile)) {
                                seekingEnchantment.applyEffect(player, projectile, tridentSeekingLevel);
                            }
                        }
                    }
                }
            }
        }
    }
    @Inject(method = "doPostHurtEffects", at = @At("TAIL"))
    public void injectDoPostHurtEffects(LivingEntity pTarget, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        Entity owner = arrow.getOwner();
        if (CoreEnchantments.SEEKING.get() instanceof SeekingEnchantment seekingEnchantment){
            if (arrow instanceof ThrownTrident) {
                seekingEnchantment.stopSeeking(arrow);
            }
            if (arrow.shotFromCrossbow()){
                seekingEnchantment.stopSeeking(arrow);
            }
        }
    }
    @Inject(method = "shoot", at = @At("TAIL"))
    public void injectShoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        Entity owner = arrow.getOwner();
        if (CoreEnchantments.SEEKING.get() instanceof SeekingEnchantment seekingEnchantment){
            if (owner instanceof LivingEntity shooter) {
                ItemStack weaponUsed = shooter.getMainHandItem();
                if (weaponUsed.isEmpty() || (!weaponUsed.is(Items.CROSSBOW) && !weaponUsed.is(Items.BOW))) {
                    weaponUsed = shooter.getOffhandItem();
                }
                int seekingLevel = EnchantmentHelper.getTagEnchantmentLevel(CoreEnchantments.SEEKING.get(), weaponUsed);
                if (seekingLevel > 0) {
                    seekingEnchantment.startSeeking(arrow);
                }
            }
            if (arrow instanceof ThrownTrident thrownTrident) {
                ItemStack tridentItem = ((IThrownTridentAccessor) thrownTrident).getTridentItem();
                int seekingLevel = EnchantmentHelper.getTagEnchantmentLevel(CoreEnchantments.SEEKING.get(), tridentItem);
                if (seekingLevel > 0) {
                    seekingEnchantment.startSeeking(arrow);
                }
            }
        }
    }
}
