package me.jiyun233.nya.module.modules.function;

import me.jiyun233.nya.event.events.client.PacketEvent;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.event.events.client.PacketEvent;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoRotate", descriptions = "Dangerous to use might desync you.", category = Category.FUNCTION)
public class NoRotate extends Module {
    private boolean cancelPackets = true;
    private boolean timerReset = false;

    @Override
    public void onUpdate() {
        if (this.timerReset && !this.cancelPackets) {
            this.cancelPackets = true;
            this.timerReset = false;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (fullNullCheck()) return;
        if (event.getStage() == 0 && this.cancelPackets && event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = event.getPacket();
            packet.yaw = NoRotate.mc.player.rotationYaw;
            packet.pitch = NoRotate.mc.player.rotationPitch;
        }
    }
}

