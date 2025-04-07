package net.turtleboi.turtlecore.network.packet.util.experience;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncExperienceS2CPacket {
    private final int totalExperience;
    private final int experienceLevel;
    private final float experienceProgress;

    public SyncExperienceS2CPacket(int totalExperience, int experienceLevel, float experienceProgress) {
        this.totalExperience = totalExperience;
        this.experienceLevel = experienceLevel;
        this.experienceProgress = experienceProgress;
    }

    public SyncExperienceS2CPacket(FriendlyByteBuf buf) {
        this.totalExperience = buf.readInt();
        this.experienceLevel = buf.readInt();
        this.experienceProgress = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(totalExperience);
        buf.writeInt(experienceLevel);
        buf.writeFloat(experienceProgress);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier, ExperienceHandler handler) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.totalExperience = totalExperience;
                player.experienceLevel = experienceLevel;
                player.experienceProgress = experienceProgress;

                if (handler != null) {
                    handler.handleExperienceSync(totalExperience, experienceLevel, experienceProgress);
                }
            }
        });
        return true;
    }
}
