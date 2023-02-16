package me.jiyun233.nya.module.modules.movement;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "AutoWalk", descriptions = "Auto move forward", category = Category.MOVEMENT)
public class AutoWalk extends Module {
    @SubscribeEvent
    public void onUpdateInput(InputUpdateEvent event) {
        event.getMovementInput().moveForward = 1.0f;
    }
}

