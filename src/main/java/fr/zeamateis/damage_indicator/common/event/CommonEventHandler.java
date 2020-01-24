package fr.zeamateis.damage_indicator.common.event;

import fr.zeamateis.damage_indicator.DamageIndicatorMod;
import fr.zeamateis.damage_indicator.amy.network.AmyNetwork;
import fr.zeamateis.damage_indicator.amy.util.Colors;
import fr.zeamateis.damage_indicator.network.packet.PacketParticles;
import fr.zeamateis.damage_indicator.network.packet.PacketPotionAddInfo;
import fr.zeamateis.damage_indicator.network.packet.PacketPotionRemoveInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = DamageIndicatorMod.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class CommonEventHandler {

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entityHealed = event.getEntityLiving();

        if (entityHealed != null && !entityHealed.isInvisible()) {
            if (!entityHealed.getEntityWorld().isRemote()) {
                double d0 = entityHealed.getPosition().getX() + 0.5D;
                double d1 = entityHealed.getPosition().getY() + 2D;
                double d2 = entityHealed.getPosition().getZ() + 0.5D;

                if (!(entityHealed instanceof PlayerEntity)) {
                    AmyNetwork.sendPacketToTrakingEntity(entityHealed, new PacketParticles(entityHealed.getEntityId(), event.getAmount(), d0, d1, d2, 0.0D, 0.0D, 0.0D, Colors.DARK_GREEN.getColor()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity entityAttacked = event.getEntityLiving();

        if (entityAttacked != null && !entityAttacked.isInvisible()) {
            if (!entityAttacked.getEntityWorld().isRemote()) {
                double d0 = entityAttacked.getPosition().getX() + 0.5D;
                double d1 = entityAttacked.getPosition().getY() + 2D;
                double d2 = entityAttacked.getPosition().getZ() + 0.5D;

                if (event.getSource().getTrueSource() instanceof PlayerEntity) {
                    AmyNetwork.sendPacketToTrakingEntity(entityAttacked, new PacketParticles(entityAttacked.getEntityId(), event.getAmount(), d0, d1, d2, 0.0D, 0.0D, 0.0D, Colors.DARK_RED.getColor()));
                }
            }
        }
    }

    //TODO Fix packets
    @SubscribeEvent
    public static void onEntityHasPotion(PotionEvent.PotionAddedEvent event) {
        if (event.getEntityLiving().world.isRemote()) {
            return;
        }
        if (!DamageIndicatorMod.getConfig().getClient().showEntityActivesPotions.get()) {
            return;
        }
        if (event.getEntity() != null) {
            event.getPotionEffect();
            AmyNetwork.sendPacketToTrakingEntity(event.getEntity(), new PacketPotionAddInfo(event.getPotionEffect().write(new CompoundNBT())));
        }
    }

    @SubscribeEvent
    public static void onEntityHasPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        if (event.getEntityLiving().world.isRemote()) {
            return;
        }
        if (!DamageIndicatorMod.getConfig().getClient().showEntityActivesPotions.get()) {
            return;
        }
        if (event.getEntity() != null) {
            if (event.getPotionEffect() != null) {
                AmyNetwork.sendPacketToTrakingEntity(event.getEntity(), new PacketPotionRemoveInfo(event.getPotionEffect().write(new CompoundNBT())));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHasExpiredPotion(PotionEvent.PotionExpiryEvent event) {
        if (event.getEntityLiving().world.isRemote()) {
            return;
        }
        if (!DamageIndicatorMod.getConfig().getClient().showEntityActivesPotions.get()) {
            return;
        }
        if (event.getEntity() != null) {
            if (event.getPotionEffect() != null) {
                AmyNetwork.sendPacketToTrakingEntity(event.getEntity(), new PacketPotionRemoveInfo(event.getPotionEffect().write(new CompoundNBT())));
            }
        }
    }
}
