package net.turtleboi.turtlecore.util;

import net.minecraft.world.damagesource.DamageSource;

public class DamageIdentifierUtil {
    private static boolean isSpecificType(DamageSource source, String... types) {
        String msgId = source.type().msgId();
        for (String type : types) {
            if (type.equals(msgId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRangedDamage(DamageSource source) {
        return isProjectile(source);
    }

    public static boolean isProjectile(DamageSource source) {
        return isSpecificType(source, "projectile", "arrow", "trident", "thrown");
    }

    public static boolean isMagic(DamageSource source) {
        return isSpecificType(source, "magic", "indirectMagic", "sonic_boom", "fireball", "witherSkull");
    }

    public static boolean isExplosion(DamageSource source) {
        return isSpecificType(source, "explosion", "explosion.player");
    }
}
