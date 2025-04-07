package net.turtleboi.turtlecore.network;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkDirection;
import net.turtleboi.turtlecore.TurtleCore;
import net.turtleboi.turtlecore.network.packet.effects.FrozenDataC2S;
import net.turtleboi.turtlecore.network.packet.effects.FrozenDataS2C;
import net.turtleboi.turtlecore.network.packet.effects.SleepDataC2S;
import net.turtleboi.turtlecore.network.packet.effects.SleepDataS2C;
import net.turtleboi.turtlecore.network.packet.util.CameraShakeS2C;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;
import net.turtleboi.turtlecore.network.packet.util.SendSoundS2C;
import net.turtleboi.turtlecore.network.packet.util.experience.ExperienceHandler;
import net.turtleboi.turtlecore.network.packet.util.experience.RemoveExperienceC2SPacket;
import net.turtleboi.turtlecore.network.packet.util.experience.SyncExperienceS2CPacket;
import net.turtleboi.turtlecore.network.packet.util.experience.UpdateExperienceC2SPacket;
import net.turtleboi.turtlecore.network.packet.targeting.TargetC2SPacket;
import net.turtleboi.turtlecore.network.packet.targeting.TargetS2CPacket;
import net.turtleboi.turtlecore.network.packet.targeting.ToggleLockC2SPacket;
import net.turtleboi.turtlecore.network.packet.targeting.ToggleLockS2CPacket;

import java.util.function.Supplier;

public class CoreNetworking {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
    public static void register () {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(TurtleCore.MOD_ID, "networking"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(RemoveExperienceC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RemoveExperienceC2SPacket::new)
                .encoder(RemoveExperienceC2SPacket::toBytes)
                .consumerMainThread(RemoveExperienceC2SPacket::handle)
                .add();

        net.messageBuilder(SyncExperienceS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncExperienceS2CPacket::new)
                .encoder(SyncExperienceS2CPacket::toBytes)
                .consumerMainThread((packet, context) -> packet.handle(context, getExperienceHandler()))
                .add();

        net.messageBuilder(UpdateExperienceC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UpdateExperienceC2SPacket::new)
                .encoder(UpdateExperienceC2SPacket::toBytes)
                .consumerMainThread(UpdateExperienceC2SPacket::handle)
                .add();

        net.messageBuilder(TargetC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(TargetC2SPacket::new)
                .encoder(TargetC2SPacket::toBytes)
                .consumerMainThread(TargetC2SPacket::handle)
                .add();

        net.messageBuilder(TargetS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(TargetS2CPacket::new)
                .encoder(TargetS2CPacket::toBytes)
                .consumerMainThread(TargetS2CPacket::handle)
                .add();

        net.messageBuilder(ToggleLockC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ToggleLockC2SPacket::new)
                .encoder(ToggleLockC2SPacket::toBytes)
                .consumerMainThread(ToggleLockC2SPacket::handle)
                .add();

        net.messageBuilder(ToggleLockS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ToggleLockS2CPacket::new)
                .encoder(ToggleLockS2CPacket::toBytes)
                .consumerMainThread(ToggleLockS2CPacket::handle)
                .add();

        net.messageBuilder(SendParticlesS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SendParticlesS2C::new)
                .encoder(SendParticlesS2C::toBytes)
                .consumerMainThread(SendParticlesS2C::handle)
                .add();

        net.messageBuilder(SendSoundS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SendSoundS2C::new)
                .encoder(SendSoundS2C::toBytes)
                .consumerMainThread(SendSoundS2C::handle)
                .add();

        net.messageBuilder(CameraShakeS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CameraShakeS2C::new)
                .encoder(CameraShakeS2C::toBytes)
                .consumerMainThread(CameraShakeS2C::handle)
                .add();

        net.messageBuilder(FrozenDataS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(FrozenDataS2C::new)
                .encoder(FrozenDataS2C::toBytes)
                .consumerMainThread(FrozenDataS2C::handle)
                .add();

        net.messageBuilder(FrozenDataC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(FrozenDataC2S::new)
                .encoder(FrozenDataC2S::toBytes)
                .consumerMainThread(FrozenDataC2S::handle)
                .add();

        net.messageBuilder(SleepDataS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SleepDataS2C::new)
                .encoder(SleepDataS2C::toBytes)
                .consumerMainThread(SleepDataS2C::handle)
                .add();

        net.messageBuilder(SleepDataC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SleepDataC2S::new)
                .encoder(SleepDataC2S::toBytes)
                .consumerMainThread(SleepDataC2S::handle)
                .add();
    }

    public static <MSG> void sendToPlayer (MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayers (MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToNear (MSG message, LivingEntity livingEntity) {
        double x = livingEntity.getX();
        double y = livingEntity.getY();
        double z = livingEntity.getZ();
        double r2 = Minecraft.getInstance().options.renderDistance().get() * 16;
        INSTANCE.send(PacketDistributor.NEAR.with(() ->
                        new PacketDistributor.TargetPoint(x, y, z, r2, livingEntity.level().dimension())),
                message);
    }

    public static <MSG> void sendToServer (MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static Supplier<ExperienceHandler> experienceHandlerSupplier;

    public static ExperienceHandler getExperienceHandler() {
        if (experienceHandlerSupplier == null) {
            throw new IllegalStateException("ExperienceHandler not provided");
        }
        return experienceHandlerSupplier.get();
    }
}
