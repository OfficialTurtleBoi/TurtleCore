package net.turtleboi.turtlecore.capabilities.targeting;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerTargetingProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<PlayerTargetingData> PLAYER_TARGET = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerTargetingData targetingData = null;
    private final LazyOptional<PlayerTargetingData> optional = LazyOptional.of(this::createPlayerTargetingData);

    private final Player player;

    public PlayerTargetingProvider(Player player) {
        this.player = player;
    }

    private PlayerTargetingData createPlayerTargetingData() {
        if (this.targetingData == null) {
            this.targetingData = new PlayerTargetingData();
        }
        return this.targetingData;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_TARGET) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerTargetingData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerTargetingData().loadNBTData(nbt, player);
    }
}
