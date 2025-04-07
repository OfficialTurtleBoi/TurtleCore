package net.turtleboi.turtlecore.client.data;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class TargetingClientData {
    private static int targetId = -1;
    private static boolean isLockedOn = false;

    public static void resetTarget() {
        targetId = -1;
    }

    public static void setTargetId(int entityId){
        targetId = entityId;
    }

    public static LivingEntity getLivingTarget(Level level) {
        return (LivingEntity) level.getEntity(targetId);
    }

    public static boolean isLockedOn() {
        return isLockedOn;
    }

    public static void setLockedOn(boolean lockedOn) {
        isLockedOn = lockedOn;
    }
}
