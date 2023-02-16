package me.jiyun233.nya.module.modules.world;

import me.jiyun233.nya.event.events.client.DisplayGuiScreenEvent;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.event.events.client.DisplayGuiScreenEvent;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "AutoRespawn", descriptions = "Anti Death Screen", category = Category.WORLD)
public class AutoRespawn extends Module {

    @SubscribeEvent
    public void onDisplayGui(DisplayGuiScreenEvent event){
        if (event.getScreen() instanceof GuiGameOver){
            mc.displayGuiScreen(null);
            mc.player.respawnPlayer();
        }
    }
}
