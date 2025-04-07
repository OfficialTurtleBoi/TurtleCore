package net.turtleboi.turtlecore.network.packet.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.turtleboi.turtlecore.client.data.EffectStatusData;

import java.util.function.Supplier;

public class SleepDataS2C {
    private final int entityId;
    private final boolean isAsleep;

    public SleepDataS2C(int entityId, boolean isAsleep) {
        this.entityId = entityId;
        this.isAsleep = isAsleep;
    }

    public SleepDataS2C(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.isAsleep = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(isAsleep);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                LivingEntity entity = (LivingEntity) Minecraft.getInstance().level.getEntity(entityId);
                if (entity != null){
                    EffectStatusData.setSleepingStatus(entityId, isAsleep);
                }
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}
