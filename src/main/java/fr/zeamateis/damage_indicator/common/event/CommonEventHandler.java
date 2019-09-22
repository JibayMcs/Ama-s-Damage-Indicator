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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = DamageIndicatorMod.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class CommonEventHandler {


    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {

        /*if (!event.getEntityLiving().world.isRemote()) {
            return;
        }

        double d0 = event.getEntityLiving().posX + 0.5D;
        double d1 = event.getEntityLiving().posY + 2D;
        double d2 = event.getEntityLiving().posZ + 0.5D;

        int currentEntityHealth = (int) Math.ceil(event.getEntityLiving().getHealth());

        if (event.getEntityLiving().getPersistentData().contains("health")) {
            int lastEntityHealth = ((IntNBT) event.getEntityLiving().getPersistentData().get("health")).getInt();

            if (lastEntityHealth != currentEntityHealth) {
                int damage = lastEntityHealth - currentEntityHealth;
                if (damage >= 0) {
                    ParticleDamage particleDamage = new ParticleDamage(lastEntityHealth - currentEntityHealth, event.getEntityLiving().world, d0, d1, d2, 0.0, 0.0, 0.0, TextFormatting.DARK_RED.getColor());
                    Minecraft.getInstance().particles.addEffect(particleDamage);
                }
            }
        }
        event.getEntityLiving().getPersistentData().putInt("health", currentEntityHealth);*/
    }

    //TODO fix first person particles flood
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entityHealed = event.getEntityLiving();
        if (entityHealed != null && !entityHealed.isInvisible()) {
            double d0 = entityHealed.posX + 0.5D;
            double d1 = entityHealed.posY + 2D;
            double d2 = entityHealed.posZ + 0.5D;

            if (!(entityHealed instanceof PlayerEntity)) {
                AmyNetwork.sendPacketToEveryone(new PacketParticles(entityHealed.getEntityId(), event.getAmount(), d0, d1, d2, 0.0D, 0.0D, 0.0D, Colors.DARK_GREEN.getColor()));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity entityAttacked = event.getEntityLiving();
        if (entityAttacked != null && !entityAttacked.isInvisible()) {

            double d0 = (double) entityAttacked.posX + 0.5D;
            double d1 = (double) entityAttacked.posY + 2D;
            double d2 = (double) entityAttacked.posZ + 0.5D;

            if (event.getSource().getTrueSource() instanceof PlayerEntity) {
                AmyNetwork.sendPacketTo((ServerPlayerEntity) event.getSource().getTrueSource(), new PacketParticles(entityAttacked.getEntityId(), event.getAmount(), d0, d1, d2, 0.0D, 0.0D, 0.0D, Colors.DARK_RED.getColor()));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHasPotion(PotionEvent.PotionAddedEvent event) {
        if (event.getEntityLiving().world.isRemote()) {
            return;
        }
        if (event.getEntity() != null) {
            if (event.getPotionEffect() != null) {
                AmyNetwork.sendPacketToEveryone(new PacketPotionAddInfo(event.getPotionEffect().write(new CompoundNBT())));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHasPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        if (event.getEntityLiving().world.isRemote()) {
            return;
        }
        if (event.getEntity() != null) {
            if (event.getPotionEffect() != null) {
                AmyNetwork.sendPacketToEveryone(new PacketPotionRemoveInfo(event.getPotionEffect().write(new CompoundNBT())));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHasExpiredPotion(PotionEvent.PotionExpiryEvent event) {
        if (event.getEntityLiving().world.isRemote()) {
            return;
        }
        if (event.getEntity() != null) {
            if (event.getPotionEffect() != null) {
                AmyNetwork.sendPacketToEveryone(new PacketPotionRemoveInfo(event.getPotionEffect().write(new CompoundNBT())));
            }
        }
    }
}
