package fr.zeamateis.damage_indicator.network.packet;

import fr.zeamateis.damage_indicator.amy.network.IPacket;
import fr.zeamateis.damage_indicator.client.DamageIndicatorParticles;
import fr.zeamateis.damage_indicator.client.particle.type.NumericParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketParticles implements IPacket<PacketParticles> {

    private float damageAmount;
    private double x, y, z;
    private double xSpeed, ySpeed, zSpeed;
    private int color;
    private int entityID;

    public PacketParticles() {
    }

    public PacketParticles(int entityID, float damageAmount, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int colorIn) {
        this.entityID = entityID;
        this.damageAmount = damageAmount;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.zSpeed = zSpeed;
        this.color = colorIn;
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(PacketParticles packet) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientWorld world = minecraft.world;

        NumericParticleType damageParticle = DamageIndicatorParticles.DAMAGE;
        damageParticle.setNumber(packet.damageAmount);
        damageParticle.setColor(packet.color);

        Entity entity = world.getEntityByID(packet.entityID);

        if (entity != null && entity.isInRangeToRenderDist(64)) {
            world.addParticle(damageParticle, packet.x, packet.y, packet.z, packet.xSpeed, packet.ySpeed, packet.zSpeed);
        }
    }

    @Override
    public void encode(PacketParticles packet, PacketBuffer buffer) {
        buffer.writeInt(packet.entityID);
        buffer.writeFloat(packet.damageAmount);
        buffer.writeDouble(packet.x);
        buffer.writeDouble(packet.y);
        buffer.writeDouble(packet.z);
        buffer.writeDouble(packet.xSpeed);
        buffer.writeDouble(packet.ySpeed);
        buffer.writeDouble(packet.zSpeed);
        buffer.writeInt(packet.color);
    }

    @Override
    public PacketParticles decode(PacketBuffer buffer) {
        return new PacketParticles(buffer.readInt(), buffer.readFloat(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readInt());
    }

    @Override
    public void handle(PacketParticles packet, Supplier<NetworkEvent.Context> ctxProvider) {
        if (ctxProvider.get().getSender() == null) {
            ctxProvider.get().enqueueWork(() -> handleClient(packet));
        }/* else {

            NumericParticleType damageParticle = DamageIndicatorParticles.DAMAGE;
            damageParticle.setNumber(packet.damageAmount);
            damageParticle.setColor(packet.color);

            ctxProvider.get().getSender().world.addParticle(damageParticle, packet.x, packet.y, packet.z, packet.xSpeed, packet.ySpeed, packet.zSpeed);

        }*/

        ctxProvider.get().setPacketHandled(true);
    }

}