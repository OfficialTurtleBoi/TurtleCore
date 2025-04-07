package net.turtleboi.turtlecore.network.packet.targeting;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.util.TargetingUtils;

import java.util.function.Supplier;

public class TargetC2SPacket {

    public TargetC2SPacket() {
    }

    public TargetC2SPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                LivingEntity target = TargetingUtils.getTarget(player);

                if (target != null && !TargetingUtils.isLockedOn(player)) {
                    target = TargetingUtils.getTarget(player);
                }

                if (TargetingUtils.isLockedOn(player)) {
                    target = TargetingUtils.getLockedTarget(player);
                } else if (target == null) {
                    TargetingUtils.resetLockOn(player);
                }

                CoreNetworking.sendToPlayer(new TargetS2CPacket(target), player);
            }
        });
        return true;
    }

}
