package net.turtleboi.turtlecore.network.packet.util.experience;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.turtleboi.turtlecore.network.CoreNetworking;

import java.util.function.Supplier;

public class UpdateExperienceC2SPacket {

    public UpdateExperienceC2SPacket() {
    }

    public UpdateExperienceC2SPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                CoreNetworking.sendToPlayer(new SyncExperienceS2CPacket(
                        player.totalExperience, player.experienceLevel, player.experienceProgress), player);
                //System.out.println("Exp updated!");  //debug code
            }
        });
        return true;
    }
}
