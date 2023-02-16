package me.jiyun233.nya.module.modules.visual;

import me.jiyun233.nya.event.events.client.AspectRatioEvent;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.FloatSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "ViewChange",descriptions = "make your camera queer lol",category = Category.VISUAL)
public class ViewChange extends Module {

    private final FloatSetting width = registerSetting("Width",(float) mc.displayWidth,0.0f,(float) mc.displayWidth);

    private final FloatSetting height =registerSetting("Height",(float) mc.displayHeight,0.0f,(float) mc.displayHeight);

    @SubscribeEvent
    public void onAspectRation(AspectRatioEvent event){
        event.setAspectRatio(width.getValue() / height.getValue());
    }
}
