package net.turtleboi.turtlecore.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.capabilities.targeting.PlayerTargetingProvider;
import net.turtleboi.turtlecore.util.TargetingUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SeekingEnchantment extends Enchantment {
    private final Map<Projectile, Boolean> projectileSeekingMap = new HashMap<>();
    public SeekingEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... slots) {
        super(rarity, category, slots);
    }

    @Override
    public int getMinCost(int level) {
        return 10 + 30 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    public void applyEffect(LivingEntity shooter, Projectile projectile, int level) {
        if (shooter instanceof Player player) {
            player.getCapability(PlayerTargetingProvider.PLAYER_TARGET).ifPresent(targetData -> {
                LivingEntity target = targetData.getLockedTarget();

                if (target != null) {
                    //System.out.println("Target found: " + target.getName().getString());
                } else {
                    //System.out.println("No target found.");
                }

                if (target != null && target.isAlive() && !(target instanceof EnderMan)) {
                    if (isSeeking(projectile)) {
                        projectile.setNoGravity(true);
                        Vec3 direction = new Vec3(
                                target.getX() - projectile.getX(),
                                (target.getEyeY() - 0.5) - projectile.getY(),
                                target.getZ() - projectile.getZ()
                        ).normalize();
                        double resetSpeed = 0.5 * level;
                        Vec3 newVelocity = direction.scale(resetSpeed);
                        projectile.setDeltaMovement(newVelocity);

                        //System.out.println("Projectile seeking towards target: " + target.getName().getString());
                        //System.out.println("New velocity: " + newVelocity);
                    } else {
                        projectile.setNoGravity(false);
                        //System.out.println("Projectile seeking stopped.");
                    }
                } else {
                    projectile.setNoGravity(false);
                    if (target == null) {
                        //System.out.println("Target is null. Stopping seeking.");
                    } else if (!target.isAlive()) {
                        //System.out.println("Target is dead. Stopping seeking.");
                    } else if (target instanceof EnderMan) {
                        //System.out.println("Target is an EnderMan. Stopping seeking.");
                    }
                }

                projectile.setNoGravity(false);
            });
        }
    }


    public void startSeeking(Projectile projectile) {
        projectileSeekingMap.put(projectile, true);
        //System.out.println("Started seeking for projectile: " + projectile.getId());
    }

    public void stopSeeking(Projectile projectile) {
        projectileSeekingMap.put(projectile, false);
        //System.out.println("Stopped seeking for projectile: " + projectile.getId());
    }

    public boolean isSeeking(Projectile projectile) {
        return projectileSeekingMap.getOrDefault(projectile, false);
    }

    @Override
    public boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment != Enchantments.PIERCING;
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack itemStack) {
        return itemStack.is(Items.BOW) || itemStack.is(Items.CROSSBOW) || itemStack.is(Items.TRIDENT) || super.canApplyAtEnchantingTable(itemStack);
    }
}
