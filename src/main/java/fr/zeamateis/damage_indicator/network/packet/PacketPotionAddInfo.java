package fr.zeamateis.damage_indicator.network.packet;

import fr.zeamateis.damage_indicator.amy.network.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPotionAddInfo implements IPacket<PacketPotionAddInfo> {

    public CompoundNBT effects;

    public PacketPotionAddInfo() {
    }

    public PacketPotionAddInfo(CompoundNBT effetsIn) {
        this.effects = effetsIn;
    }

    @Override
    public void encode(PacketPotionAddInfo packet, PacketBuffer buffer) {
        buffer.writeCompoundTag(packet.effects);
    }

    @Override
    public PacketPotionAddInfo decode(PacketBuffer buffer) {
        return new PacketPotionAddInfo(buffer.readCompoundTag());
    }

    @Override
    public void handle(PacketPotionAddInfo packet, Supplier<NetworkEvent.Context> ctxProvider) {
        if (ctxProvider.get().getSender() != null) {
        } else {
            ctxProvider.get().enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.pointedEntity != null) {
                    if (mc.pointedEntity instanceof LivingEntity) {
                        ((LivingEntity) mc.pointedEntity).addPotionEffect(EffectInstance.read(packet.effects));
                    }
                }
            });
        }

        ctxProvider.get().setPacketHandled(true);
    }

}