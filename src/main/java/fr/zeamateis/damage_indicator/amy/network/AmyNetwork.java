package fr.zeamateis.damage_indicator.amy.network;

import fr.zeamateis.damage_indicator.DamageIndicatorMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class AmyNetwork {

    private static final String PROTOCOL_VERSION = String.valueOf(1);
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(DamageIndicatorMod.MODID, "damage_indicator_channel")).networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals).simpleChannel();

    public static SimpleChannel getNetworkChannel() {
        return CHANNEL;
    }

    public static void sendPacketToServer(IPacket<?> packetIn) {
        CHANNEL.sendToServer(packetIn);

    }

    public static void sendPacketTo(ServerPlayerEntity player, IPacket<?> packetIn) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packetIn);
    }

    public static void sendPacketToEveryone(IPacket<?> packetIn) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), packetIn);
    }

    public static void sendPacketToTrakingEntity(Entity trackingEntityIn, IPacket<?> packetIn) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> trackingEntityIn), packetIn);
    }

    public static void sendPacketToDimension(DimensionType dimension, IPacket<?> packetIn) {
        CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dimension), packetIn);
    }
}