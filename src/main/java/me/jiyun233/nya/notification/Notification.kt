package me.jiyun233.nya.notification

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.module.huds.NotificationModule
import me.jiyun233.nya.utils.Timer
import me.jiyun233.nya.utils.render.Render2DUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color
import kotlin.math.max

class Notification(
    val title: String,
    val text: String,
    val type: NotificationType
) {
    var x = 0f
    var y = 0f

    val height = 32f
    val width = max(NyaHack.fontManager.CustomFont.getWidth(title), NyaHack.fontManager.CustomFont.getWidth(text)) + 45f

    var entering = true
    var exiting = false

    private val timer = Timer()
    private var timeX = 0f

    private val totalDisplayTime = NotificationModule.INSTANCE.speed.value

    private val xAnimation = AnimationUtils()
    private val yAnimation = AnimationUtils()

    fun animationXTo(target: Float): Boolean {
        x = xAnimation.animate(target, x, 0.05f)
        return x == target
    }

    fun animationYTo(target: Float) {
        y = yAnimation.animate(target, y, 0.05f)
    }

    fun draw() {
        val size = ScaledResolution(Minecraft.getMinecraft())

        if (size.scaledWidth - x > width * 0.95) {
            if (timer.passed(2)) {
                val speed = width / (totalDisplayTime * 100)

                timeX += speed
                timer.reset()
            }
        }

        if (timeX > width) exiting = true

        val font = NyaHack.fontManager.CustomFont

        val textHeight = font.height

        val fx = size.scaledWidth - x
        val fy = size.scaledHeight - y
        Render2DUtil.drawRect(fx, fy, width, height, Color(10, 25, 10, 200).rgb)

        type.drawTextIcon(fx + 5, fy + ((height - textHeight) / 2))
        font.drawString(text, fx.plus(type.fontWidth + 10), fy + ((height - textHeight) / 2), Color.WHITE.rgb)
    }
}