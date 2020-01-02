package fr.zeamateis.damage_indicator.client.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
 
public class DamageIndicatorConfig {

    private final ForgeConfigSpec clientSpec;
    private final Client client;

    public DamageIndicatorConfig() {
        Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        this.clientSpec = specPair.getRight();
        this.client = specPair.getLeft();
    }

    public Client getClient() {
        return this.client;
    }

    public void register(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, this.clientSpec);
    }

    public static class Client {

        public final BooleanValue showInGameOverlay, showEntityName, showEntityStats, showMinimalStats, showEntityActivesPotions, showPotionInfo, showParticles;

        public final EnumValue<EnumGuiPos> overlayPosition;

        public final ForgeConfigSpec.DoubleValue hudScale;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client-only settings").push("client");

            this.showInGameOverlay = builder.comment("Show InGame Damage Indicator Overlay ?").translation("damage_indicator.config.client.showInGameOverlay").define("showInGameOverlay", true);
            this.showEntityName = builder.comment("Show Entity Name On Overlay ?").translation("damage_indicator.config.client.showEntityName")
                    .define("showEntityNameInOverlay", true);
            this.showEntityStats = builder.comment("Show Entity Stats On Overlay ?").translation("damage_indicator.config.client.showEntityStats")
                    .define("showEntityStatsInOverlay", true);
            this.showEntityActivesPotions = builder.comment("Show Entity Actives Potions on Overlay ?").translation("damage_indicator.config.client.showEntityActivesPotions")
                    .define("showEntityActivesPotions", true);
            this.overlayPosition = builder.comment("Only use 'TOP_LEFT' or 'BOTTOM_LEFT' positions. ('TOP_RIGHT' and 'BOTTOM_RIGHT' W.I.P) ").translation("damage_indicator.config.client.overlayPosition")
                    .defineEnum("overlayPosition", EnumGuiPos.TOP_LEFT);

            this.showPotionInfo = builder.comment("Display huge or little potion effect icons").translation("damage_indicator.config.client.showPotionInfo")
                    .define("showPotionInfo", true);

            this.showParticles = builder.comment("Show Particles on Hit or Heal Entity ?").translation("damage_indicator.config.client.showParticles").define("showParticles", false);

            this.showMinimalStats = builder.comment("Show stats like Hearts and Armor in Minimal View ?").translation("damage_indicator.config.client.showMinimalStats").define("showMinimalStats", true);

            this.hudScale = builder.comment("Define the size of H.U.D when pointing entity").translation("damage_indicator.config.client.hudScale").defineInRange("hudScale", 1.0, 0.0, 1.0);

            builder.pop();
        }

        public enum EnumGuiPos {
            TOP_LEFT,
            TOP_RIGHT,
            BOTTOM_LEFT,
            BOTTOM_RIGHT
        }

    }

}