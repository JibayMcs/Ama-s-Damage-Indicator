package fr.zeamateis.damage_indicator;

import fr.zeamateis.damage_indicator.amy.network.AmyNetwork;
import fr.zeamateis.damage_indicator.client.config.DamageIndicatorConfig;
import fr.zeamateis.damage_indicator.client.particle.ParticleDamage;
import fr.zeamateis.damage_indicator.client.particle.type.NumericParticleType;
import fr.zeamateis.damage_indicator.network.packet.PacketParticles;
import fr.zeamateis.damage_indicator.network.packet.PacketPotionAddInfo;
import fr.zeamateis.damage_indicator.network.packet.PacketPotionRemoveInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DamageIndicatorMod.MODID)
public class DamageIndicatorMod {
    public static final String MODID = "damage_indicator";

    private static final Logger LOGGER = LogManager.getLogger();
    private static final DamageIndicatorConfig CONFIG = new DamageIndicatorConfig();

    public static NumericParticleType DAMAGE_PARTICLE = new NumericParticleType(true);


    public DamageIndicatorMod() {
        CONFIG.register(ModLoadingContext.get());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static DamageIndicatorConfig getConfig() {
        return CONFIG;
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PacketParticles packetParticles = new PacketParticles();
        AmyNetwork.getNetworkChannel().messageBuilder(PacketParticles.class, 0).encoder(packetParticles::encode).decoder(packetParticles::decode).consumer(packetParticles::handle).add();

        PacketPotionAddInfo packetPotionAddInfo = new PacketPotionAddInfo();
        AmyNetwork.getNetworkChannel().messageBuilder(PacketPotionAddInfo.class, 1).encoder(packetPotionAddInfo::encode).decoder(packetPotionAddInfo::decode).consumer(packetPotionAddInfo::handle).add();

        PacketPotionRemoveInfo packetPotionRemoveInfo = new PacketPotionRemoveInfo();
        AmyNetwork.getNetworkChannel().messageBuilder(PacketPotionRemoveInfo.class, 2).encoder(packetPotionRemoveInfo::encode).decoder(packetPotionRemoveInfo::decode).consumer(packetPotionRemoveInfo::handle).add();
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class CommonRegistryHandler {
        public static void onRegisterParticles(RegistryEvent.Register<ParticleType<?>> event) {
            event.getRegistry().register(DAMAGE_PARTICLE.setRegistryName(MODID, "damage_particle"));
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ClientRegistryHandler {
        @SubscribeEvent
        public static void onParticleFactoryRegistry(ParticleFactoryRegisterEvent event) {
            Minecraft.getInstance().particles.registerFactory(DAMAGE_PARTICLE, factory -> new ParticleDamage.Factory());
        }
    }
}
