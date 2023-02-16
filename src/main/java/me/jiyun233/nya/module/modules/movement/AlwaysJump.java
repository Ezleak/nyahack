package me.jiyun233.nya.module.modules.movement;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;

@ModuleInfo(name = "AlwaysJump", descriptions = "Auto jump always", category = Category.MOVEMENT)
public class AlwaysJump extends Module {
    @Override
    public void onUpdate() {
        if (mc.player.onGround) mc.player.jump();
    }
}
