package net.turtleboi.turtlecore.capabilities.targeting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.UUID;

@AutoRegisterCapability
public class PlayerTargetingData {
    private LivingEntity lockedTarget;
    private boolean isLockedOn;

    public LivingEntity getLockedTarget() {
        return lockedTarget;
    }

    public void setLockedTarget(LivingEntity target) {
        this.lockedTarget = target;
    }

    public void setLockedOn(boolean lockedOn) {
        isLockedOn = lockedOn;
    }

    public boolean isLockedOn() {
        return isLockedOn;
    }

    public void copyFrom(PlayerTargetingData source) {
        this.lockedTarget = source.lockedTarget;
        this.isLockedOn = source.isLockedOn;
    }

    public void saveNBTData(CompoundTag nbt) {
        if (lockedTarget != null) {
            nbt.putUUID("lockedTargetUUID", lockedTarget.getUUID());
        }
        nbt.putBoolean("isLockedOn", isLockedOn);
    }

    public void loadNBTData(CompoundTag nbt, Player player) {
        this.isLockedOn = nbt.getBoolean("isLockedOn");
        if (nbt.contains("lockedTargetUUID")) {
            UUID uuid = nbt.getUUID("lockedTargetUUID");
            this.lockedTarget = findEntityByUUID(uuid, player);
        } else {
            this.lockedTarget = null;
        }
    }

    private LivingEntity findEntityByUUID(UUID uuid, Player player) {
        return player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(25))
                .stream()
                .filter(entity -> entity.getUUID().equals(uuid))
                .findFirst()
                .orElse(null);
    }
}
