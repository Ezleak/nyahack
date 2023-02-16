package me.jiyun233.nya.module.modules.client

import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.utils.client.ChatUtil
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.text.SimpleDateFormat
import java.util.*

@ModuleInfo(name = "ChatTimeStamp", category = Category.CLIENT, descriptions = "Add time stamp at revice chat")
class ChatTimeStamp : Module() {
    @SubscribeEvent
    fun awa(event: ClientChatReceivedEvent) {
        val newTextComponentString = TextComponentString(
            ChatUtil.translateAlternateColorCodes(
                "&8[&6&l${
                    SimpleDateFormat("k:mm").format(
                        Date()
                    )
                }&r&8]&r"
            )
        )
        newTextComponentString.appendSibling(event.message)
        event.message = newTextComponentString
    }
}