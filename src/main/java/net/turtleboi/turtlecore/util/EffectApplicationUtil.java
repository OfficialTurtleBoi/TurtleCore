package net.turtleboi.turtlecore.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class EffectApplicationUtil {
    public static void setIgnitor(LivingEntity livingEntity, Entity ignitor){
        if (!livingEntity.getPersistentData().contains("IgnitedBy")) {
            //System.out.println(livingEntity + " ignited by " + ignitor + "!");
            livingEntity.getPersistentData().putUUID("IgnitedBy", ignitor.getUUID());
        }
    }

    public static void setChiller(LivingEntity livingEntity, Entity chiller){
        if (!livingEntity.getPersistentData().contains("ChilledBy")) {
            //System.out.println(livingEntity + " chilled by " + chiller + "!");
            livingEntity.getPersistentData().putUUID("ChilledBy", chiller.getUUID());
        }
    }

    public static void setFrozen(LivingEntity livingEntity, UUID uuid){
        if (!livingEntity.getPersistentData().contains("FrozenBy")) {
            //System.out.println(livingEntity + " was frozen by" + uuid.toString() + "!");
            livingEntity.getPersistentData().putUUID("FrozenBy", uuid);
        }
    }

    public static void setStunner(LivingEntity livingEntity, Entity stunner){
        if (!livingEntity.getPersistentData().contains("StunnedBy")) {
            //System.out.println(livingEntity + " stunned by " + stunner + "!");
            livingEntity.getPersistentData().putUUID("StunnedBy", stunner.getUUID());
        }
    }
}
