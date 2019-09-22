package fr.zeamateis.damage_indicator.client;

import fr.zeamateis.damage_indicator.DamageIndicatorMod;
import fr.zeamateis.damage_indicator.client.particle.type.NumericParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//TODO Reactivate to register custom particle
//@Mod.EventBusSubscriber(modid = DamageIndicatorMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DamageIndicatorParticles {

    public static NumericParticleType DAMAGE = new NumericParticleType(true);

    @SubscribeEvent
    public static void onRegisterParticle(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().register(DAMAGE.setRegistryName(DamageIndicatorMod.MODID, "damage_indicator"));
    }

    private static NumericParticleType register(ResourceLocation key, boolean alwaysShow) {
        return (NumericParticleType) Registry.<ParticleType<? extends IParticleData>>register(Registry.PARTICLE_TYPE, key, new NumericParticleType(alwaysShow));
    }

    private static <T extends IParticleData> ParticleType<T> register(String key, IParticleData.IDeserializer<T> deserializer) {
        return Registry.register(Registry.PARTICLE_TYPE, key, new ParticleType<>(false, deserializer));
    }

}
