package net.turtleboi.turtlecore.network.packet.effects;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.turtleboi.turtlecore.network.CoreNetworking;

import java.util.function.Supplier;

public class SleepDataC2S {
    private final int entityId;
    private final boolean frozen;

    public SleepDataC2S(int entityId, boolean frozen) {
        this.entityId = entityId;
        this.frozen = frozen;
    }

    public SleepDataC2S(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.frozen = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(frozen);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                CoreNetworking.sendToAllPlayers(new SleepDataS2C(entityId, frozen));
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}
