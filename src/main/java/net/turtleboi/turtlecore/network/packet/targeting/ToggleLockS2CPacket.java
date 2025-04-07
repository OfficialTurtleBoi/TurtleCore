package net.turtleboi.turtlecore.network.packet.targeting;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.turtleboi.turtlecore.client.data.TargetingClientData;

import java.util.function.Supplier;

public class ToggleLockS2CPacket {
    private final boolean isLockedOn;

    public ToggleLockS2CPacket(boolean isLockedOn) {
        this.isLockedOn = isLockedOn;
    }

    public ToggleLockS2CPacket(FriendlyByteBuf buf) {
        this.isLockedOn = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isLockedOn);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            TargetingClientData.setLockedOn(isLockedOn);
        });
        return true;
    }
}
