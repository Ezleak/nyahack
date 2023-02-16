package me.jiyun233.nya.module.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.jiyun233.nya.event.events.player.UpdateWalkingPlayerEvent;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.ModeSetting;
import me.jiyun233.nya.utils.client.ChatUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "AntiVoid", category = Category.MOVEMENT, descriptions = "Void bounce")
public class AntiVoid extends Module {
    public ModeSetting<Mode> mode = registerSetting("Mode", Mode.BOUNCE);
    public me.jiyun233.nya.utils.Timer groundTimer = new me.jiyun233.nya.utils.Timer();
    public BlockPos lastGroundPos;

    @SubscribeEvent
    public void onMotion(UpdateWalkingPlayerEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (mode.getValue().equals(Mode.MOTION) && event != null) {
            if (mc.player.onGround && groundTimer.passed(1000)) {
                lastGroundPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
                groundTimer.reset();
            }
            if (mc.player.posY <= 0.9 && lastGroundPos != null) {
                event.setX(lastGroundPos.getX());
                event.setY(lastGroundPos.getY());
                event.setZ(lastGroundPos.getZ());
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateWalkingPlayerEvent event) {
        if (fullNullCheck()) {
            return;
        }
        final double yLevel = mc.player.posY;
        try {
            if (yLevel <= 0.9) {
                ChatUtil.sendNoSpamMessage(ChatFormatting.RED + "Player " + ChatFormatting.GREEN + mc.player.getName() + ChatFormatting.RED + " is in the void!");
                if (this.mode.getValue().equals(Mode.BOUNCE)) {
                    mc.player.moveVertical = 10.0f;
                    mc.player.jump();
                } else if (this.mode.getValue().equals(Mode.CANCEL)) {
                    mc.player.jump();
                    event.setCanceled(true);
                }
            } else {
                mc.player.moveVertical = 0.0f;
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        mc.player.moveVertical = 0.0f;
        groundTimer.reset();
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        mc.player.moveVertical = 0.0f;
        groundTimer.reset();
    }

    @Override
    public String getHudInfo() {
        if (this.mode.getValue().equals(Mode.BOUNCE)) {
            return "Bounce";
        } else if (this.mode.getValue().equals(Mode.CANCEL)) {
            return "Cancel";
        } else if (mode.getValue().equals(Mode.MOTION)) {
            return "Motion";
        }
        return null;
    }

    public enum Mode {
        BOUNCE,
        CANCEL,
        MOTION
    }
}
