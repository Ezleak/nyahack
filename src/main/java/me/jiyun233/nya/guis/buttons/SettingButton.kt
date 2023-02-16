package me.jiyun233.nya.guis.buttons

import me.jiyun233.nya.guis.CategoryPanel
import me.jiyun233.nya.settings.Setting

abstract class SettingButton<T : Setting<*>>(
    width: Float,
    height: Float,
    val value: Setting<*>,
    val father: ModuleButton
) : Button(width, height, father.panelFather) {

}