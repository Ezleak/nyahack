package me.jiyun233.nya.notification

import me.jiyun233.nya.NyaHack
import java.awt.Color

enum class NotificationType(private val iconName: String, private val color: Int) {
    SUCCESS("F", Color.GREEN.rgb),
    ERROR("E", Color.RED.rgb),
    INFO("v", Color.WHITE.rgb),
    WARNING("g", Color.YELLOW.rgb);


    fun drawTextIcon(x: Float, y: Float) {
        NyaHack.fontManager.IconFont.drawString(
            iconName,
            x,
            y,
            color
        )
    }

    val fontHeight = NyaHack.fontManager.IconFont.height
    val fontWidth = NyaHack.fontManager.IconFont.getWidth(iconName)
}