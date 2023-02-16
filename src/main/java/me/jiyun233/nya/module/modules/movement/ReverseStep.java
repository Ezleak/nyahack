package me.jiyun233.nya.module.modules.movement;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.BooleanSetting;
import me.jiyun233.nya.settings.DoubleSetting;
import me.jiyun233.nya.settings.IntegerSetting;

@ModuleInfo(name = "ReverseStep", category = Category.MOVEMENT, descriptions = "fast fall down")
public class ReverseStep extends Module {

    public BooleanSetting FallSpeed = registerSetting("UseFallSpeed", true);
    private final DoubleSetting height = registerSetting("Height", 3, 0.5, 3).booleanDisVisible(FallSpeed);
    public IntegerSetting FallingSpeed = registerSetting("FallSpeed", 3, 1, 10);

    @Override
    public void onUpdate() {
        if (fullNullCheck() || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }
        if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder()) {
            if (FallSpeed.getValue()) {
                mc.player.motionY -= FallingSpeed.getValue();
            } else {
                for (double y = 0.0; y < this.height.getValue() + 0.5; y += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                        mc.player.motionY = -15.0;
                        break;
                    }
                }
            }
        }
    }
}
