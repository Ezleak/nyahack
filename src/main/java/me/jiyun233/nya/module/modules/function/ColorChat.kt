package me.jiyun233.nya.module.modules.function

import me.jiyun233.nya.event.events.client.PacketEvent
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.settings.BooleanSetting
import me.jiyun233.nya.settings.ModeSetting
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketChatMessage
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@ModuleInfo(name = "ColorChat", descriptions = "Custom your chat message", category = Category.FUNCTION)
class ColorChat : Module() {
    private val rainbow: BooleanSetting = registerSetting("Rainbow", false)
    private val choose: ModeSetting<ColorName> = registerSetting("Color", ColorName.GREEN).booleanDisVisible(rainbow)
    private val obfuscated: BooleanSetting = registerSetting("Obfuscated", false)
    private val bold: BooleanSetting = registerSetting("Bold", false)
    private val strikeThrough: BooleanSetting = registerSetting("Strikethrough", false)
    private val underline: BooleanSetting = registerSetting("Underline", false)
    private val italic: BooleanSetting = registerSetting("Italic", false)

    @SubscribeEvent
    fun onPacketSend(event: PacketEvent.Send) {
        if (event.getPacket<Packet<*>>() is CPacketChatMessage) {
            val packet = event.getPacket<CPacketChatMessage>()
            if (!rainbow.value) {
                packet.message = custom() + choose.value.colorName + packet.message
            } else {
                packet.message = custom() + colouriseRainbow(packet.message)
            }
        }
    }

    private fun custom(): String {
        var result = ""
        if (obfuscated.value) result += "\u00A7k"
        if (bold.value) result += "\u00A7l"
        if (strikeThrough.value) result += "\u00A7m"
        if (underline.value) result += "\u00A7n"
        if (italic.value) result += "\u00A7o"
        return result
    }

    private fun colouriseRainbow(message: String): String {
        val charArray = message.toCharArray()
        val sb = StringBuilder()
        var i = 0
        for (c in charArray) {
            if (i >= rainbowList.size) i = 0;
            sb.append(rainbowList[i] + c)
            i++
        }
        return sb.toString()
    }


    enum class ColorName(val colorName: String) {
        BLACK("\u00A70"),
        DARK_BLUE("\u00A71"),
        DARK_GREEN("\u00A72"),
        DARK_AQUA("\u00A73"),
        DARK_RED("\u00A74"),
        DARK_PURPLE("\u00A75"),
        GOLD("\u00A76"),
        GRAY("\u00A77"),
        DARK_GRAY("\u00A78"),
        BLUE("\u00A79"),
        GREEN("\u00A7a"),
        AQUA("\u00A7b"),
        RED("\u00A7c"),
        LIGHT_PURPLE("\u00A7d"),
        YELLOW("\u00A7e")
    }

    private val rainbowList = arrayListOf(
        "\u00A7c",
        "\u00A76",
        "\u00A7e",
        "\u00A7a",
        "\u00A7b",
        "\u00A79",
        "\u00A7d",
    )
}