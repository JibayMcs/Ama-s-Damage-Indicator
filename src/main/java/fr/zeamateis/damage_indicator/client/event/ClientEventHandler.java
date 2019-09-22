package fr.zeamateis.damage_indicator.client.event;

import fr.zeamateis.damage_indicator.DamageIndicatorMod;
import fr.zeamateis.damage_indicator.client.gui.GuiDamageIndicator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = DamageIndicatorMod.MODID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    static final GuiDamageIndicator guiDamageIndicator;

    static {
        guiDamageIndicator = new GuiDamageIndicator();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (DamageIndicatorMod.getConfig().getClient().showInGameOverlay.get()) {
            if (event.getType() != ElementType.EXPERIENCE) {
                return;
            }
            guiDamageIndicator.renderGameOverlay(event.getPartialTicks());
        }
    }

}
