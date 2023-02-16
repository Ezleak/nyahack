package me.jiyun233.nya.module.modules.client

import me.jiyun233.nya.event.events.client.PacketEvent
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketChatMessage
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@ModuleInfo(name = "ChatSuffix", descriptions = "Chat suffix", category = Category.CLIENT)
class ChatSuffix : Module() {
    @SubscribeEvent
    fun onPacketSend(event: PacketEvent.Send) {
        if (event.stage == 0) {
            if (event.getPacket<Packet<*>>() is CPacketChatMessage) {
                var s = (event.getPacket<Packet<*>>() as CPacketChatMessage).getMessage()
                if (s.startsWith("/")) return
                s += " | " + "にゃℍぁｃｋ　０．３"
                if (s.length >= 256) s = s.substring(0, 256)
                (event.getPacket<Packet<*>>() as CPacketChatMessage).message = s
            }
        }
    }
}