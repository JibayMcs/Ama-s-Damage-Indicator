package fr.zeamateis.damage_indicator.network.packet;

import fr.zeamateis.damage_indicator.amy.network.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPotionRemoveInfo implements IPacket<PacketPotionRemoveInfo> {

    public CompoundNBT effects;

    public PacketPotionRemoveInfo() {
    }

    public PacketPotionRemoveInfo(CompoundNBT effetsIn) {
        this.effects = effetsIn;
    }

    @Override
    public void encode(PacketPotionRemoveInfo packet, PacketBuffer buffer) {
        buffer.writeCompoundTag(packet.effects);
    }

    @Override
    public PacketPotionRemoveInfo decode(PacketBuffer buffer) {
        return new PacketPotionRemoveInfo(buffer.readCompoundTag());
    }

    @Override
    public void handle(PacketPotionRemoveInfo packet, Supplier<NetworkEvent.Context> ctxProvider) {
        if (ctxProvider.get().getSender() != null) {
        } else {
            ctxProvider.get().enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.pointedEntity != null) {
                    if (mc.pointedEntity instanceof LivingEntity) {
                        ((LivingEntity) mc.pointedEntity).removePotionEffect(EffectInstance.read(packet.effects).getPotion());
                    }
                }
            });
        }

        ctxProvider.get().setPacketHandled(true);
    }

}