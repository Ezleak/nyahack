package me.jiyun233.nya.guis.buttons.setting

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.guis.buttons.ModuleButton
import me.jiyun233.nya.guis.buttons.SettingButton
import me.jiyun233.nya.module.modules.client.ClickGui
import me.jiyun233.nya.settings.BooleanSetting
import me.jiyun233.nya.utils.render.Render2DUtil
import java.awt.Color

class BooleanSettingButton(
    width: Float,
    height: Float,
    value: BooleanSetting,
    father: ModuleButton
) : SettingButton<BooleanSetting>(width, height, value, father) {
    override fun drawButton(x: Float, y: Float, mouseX: Int, mouseY: Int) {
        Render2DUtil.drawRect(x, y, this.width, this.height, Color(15, 15, 15, 95).rgb)
        NyaHack.fontManager!!.CustomFont.drawStringWithShadow(
            value.name,
            x + 3,
            y + (height / 2) - (NyaHack.fontManager!!.CustomFont.height / 4),
            if (value.value as Boolean) ClickGui.getCurrentColor() else Color.WHITE
        )
        this.x = x
        this.y = y
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (!isHoveredButton(mouseX, mouseY) || !value.isVisible || !father.isShowSettings) {
            return
        }
        when (mouseButton) {
            0, 1 -> value.value = !(value.value as Boolean)
        }
    }

}