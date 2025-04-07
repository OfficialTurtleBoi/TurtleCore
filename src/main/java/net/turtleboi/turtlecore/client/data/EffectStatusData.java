package net.turtleboi.turtlecore.client.data;

import java.util.HashMap;
import java.util.Map;

public class EffectStatusData {
    private static final Map<Integer, Boolean> frozenStatuses = new HashMap<>();
    private static final Map<Integer, Boolean> sleepingStatuses = new HashMap<>();

    public static void setFrozenStatus(int entityId, boolean frozen) {
        frozenStatuses.put(entityId, frozen);
    }

    public static boolean isFrozen(int entityId) {
        return frozenStatuses.getOrDefault(entityId, false);
    }

    public static void setSleepingStatus(int entityId, boolean sleeping) {
        sleepingStatuses.put(entityId, sleeping);
    }

    public static boolean isAsleep(int entityId) {
        return sleepingStatuses.getOrDefault(entityId, false);
    }
}
