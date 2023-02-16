package me.jiyun233.nya.guis.buttons.setting

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.guis.buttons.ModuleButton
import me.jiyun233.nya.guis.buttons.SettingButton
import me.jiyun233.nya.settings.BindSetting
import me.jiyun233.nya.settings.BindSetting.KeyBind
import me.jiyun233.nya.utils.render.Render2DUtil
import org.lwjgl.input.Keyboard
import java.awt.Color

class BindSettingButton(
    width: Float,
    height: Float,
    value: BindSetting,
    father: ModuleButton
) : SettingButton<BindSetting>(width, height, value, father) {

    private var listening = false

    override fun drawButton(x: Float, y: Float, mouseX: Int, mouseY: Int) {
        Render2DUtil.drawRect(x, y, this.width, this.height, Color(15, 15, 15, 95).rgb)
        NyaHack.fontManager!!.CustomFont.drawStringWithShadow(
            "${value.name}: ${if (listening) "..." else Keyboard.getKeyName((value.value as KeyBind).keyCode)}",
            x + 3,
            y + (height / 2) - (NyaHack.fontManager!!.CustomFont.height / 4),
            Color.WHITE
        )
        this.x = x
        this.y = y
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (!isHoveredButton(mouseX, mouseY) || !value.isVisible || !father.isShowSettings) {
            return
        }
        when (mouseButton) {
            0 -> listening = true
            1 -> value.value = value.defaultValue
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (this.listening) {
            (value as BindSetting).value = KeyBind(keyCode)
            this.listening = false
        }
    }
}