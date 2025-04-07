package net.turtleboi.turtlecore.capabilities.party;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerPartyProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<PlayerParty> PLAYER_PARTY = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerParty party = null;
    private final LazyOptional<PlayerParty> optional = LazyOptional.of(this::createPlayerParty);

    private PlayerParty createPlayerParty() {
        if (this.party == null) {
            this.party = new PlayerParty();
        }
        return this.party;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_PARTY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerParty().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerParty().loadNBTData(nbt);
    }
}
