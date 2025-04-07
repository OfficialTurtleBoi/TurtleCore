package net.turtleboi.turtlecore.network.packet.targeting;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.turtleboi.turtlecore.util.TargetingUtils;

import java.util.function.Supplier;

public class ToggleLockC2SPacket {

    public ToggleLockC2SPacket() {
    }

    public ToggleLockC2SPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                TargetingUtils.toggleLockOn(player);
            }
        });
        return true;
    }
}
