package me.jiyun233.nya.module.modules.visual;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.module.modules.client.ClickGui;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "SkyColor", category = Category.VISUAL, descriptions = "Custom Sky Color")
public class SkyColor extends Module {

    @SubscribeEvent
    public void fog_colour(final EntityViewRenderEvent.FogColors event) {
        event.setRed(ClickGui.getCurrentColor().getRed() / 255f);
        event.setGreen(ClickGui.getCurrentColor().getGreen() / 255f);
        event.setBlue(ClickGui.getCurrentColor().getBlue() / 255f);
    }

    @SubscribeEvent
    public void fog_density(final EntityViewRenderEvent.FogDensity event) {
        event.setDensity(0.0f);
        event.setCanceled(true);
    }
}
