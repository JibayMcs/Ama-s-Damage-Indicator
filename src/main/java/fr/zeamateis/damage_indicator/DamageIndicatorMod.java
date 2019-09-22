package fr.zeamateis.damage_indicator;

import fr.zeamateis.damage_indicator.amy.network.AmyNetwork;
import fr.zeamateis.damage_indicator.client.DamageIndicatorParticles;
import fr.zeamateis.damage_indicator.client.config.DamageIndicatorConfig;
import fr.zeamateis.damage_indicator.client.particle.ParticleDamage;
import fr.zeamateis.damage_indicator.network.packet.PacketParticles;
import fr.zeamateis.damage_indicator.network.packet.PacketPotionAddInfo;
import fr.zeamateis.damage_indicator.network.packet.PacketPotionRemoveInfo;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DamageIndicatorMod.MODID)
public class DamageIndicatorMod {
    public static final String MODID = "damage_indicator";

    private static final Logger LOGGER = LogManager.getLogger();
    private static final DamageIndicatorConfig CONFIG = new DamageIndicatorConfig();

    private final CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public DamageIndicatorMod() {
        CONFIG.register(ModLoadingContext.get());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
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

    private void clientSetup(FMLClientSetupEvent event) {
        this.PROXY.registerFactory();
    }

    static class ClientProxy extends CommonProxy {
        @Override
        void registerFactory() {
            Minecraft.getInstance().particles.registerFactory(DamageIndicatorParticles.DAMAGE, new ParticleDamage.Factory());
        }
    }

    static class CommonProxy {
        void registerFactory() {
        }
    }
}
