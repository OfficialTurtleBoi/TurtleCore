package net.turtleboi.turtlecore.network.packet.targeting;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.turtleboi.turtlecore.client.data.TargetingClientData;

import java.util.function.Supplier;

public class TargetS2CPacket {
    private final int targetEntityId;

    public TargetS2CPacket(LivingEntity targetEntity) {
        if (targetEntity != null) {
            this.targetEntityId = targetEntity.getId();
        } else {
            this.targetEntityId = -1;
        }
    }

    public TargetS2CPacket(FriendlyByteBuf buf) {
        this.targetEntityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(targetEntityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                if (targetEntityId == -1) {
                    TargetingClientData.resetTarget();
                } else {
                    Entity entity = Minecraft.getInstance().level.getEntity(targetEntityId);
                    if (entity instanceof LivingEntity) {
                        TargetingClientData.setTargetId(targetEntityId);
                    }
                }
            }
        });
        return true;
    }
}
