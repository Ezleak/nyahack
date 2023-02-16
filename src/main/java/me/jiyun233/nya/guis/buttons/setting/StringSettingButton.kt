package me.jiyun233.nya.guis.buttons.setting

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.guis.buttons.ModuleButton
import me.jiyun233.nya.guis.buttons.SettingButton
import me.jiyun233.nya.settings.StringSetting
import me.jiyun233.nya.utils.Timer
import me.jiyun233.nya.utils.client.ChatUtil
import me.jiyun233.nya.utils.render.Render2DUtil
import net.minecraft.util.ChatAllowedCharacters
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.util.concurrent.atomic.AtomicReference


class StringSettingButton(
    width: Float,
    height: Float,
    value: StringSetting,
    father: ModuleButton
) : SettingButton<StringSetting>(width, height, value, father) {

    private var inputting = false
    private val stringRef = AtomicReference(value.value)

    private var awa = false

    private var addText = "|"

    private val timer:Timer = Timer()

    override fun drawButton(x: Float, y: Float, mouseX: Int, mouseY: Int) {
        if (timer.passed(500)) {
            if (awa) {
                addText = " "
                awa = false
            } else {
                addText = "|"
                awa = true
            }
            timer.reset()
        }
        val renderString = stringRef.get() + addText
        Render2DUtil.drawRect(x, y, this.width, this.height, Color(15, 15, 15, 95).rgb)
        NyaHack.fontManager!!.CustomFont.drawStringWithShadow(
            if (inputting) renderString else ChatUtil.translateAlternateColorCodes(value.name + ": " + value.value),
            x + 3,
            y + (height / 2) - (NyaHack.fontManager!!.CustomFont.height / 4),
            Color.WHITE
        )
        this.x = x
        this.y = y
    }


    private fun backspace() {
        val cache = stringRef.get() ?: return
        stringRef.set(cache.takeIf { it.isNotEmpty() }?.substring(0 until cache.length - 1) ?: "")
    }

    private operator fun AtomicReference<String>.plusAssign(c: Char) {
        stringRef.set(stringRef.get() + c)
    }

    private fun setString() {
        value.value = stringRef.get() ?: return
        inputting = false
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (this.isHoveredButton(mouseX, mouseY) && mouseButton == 0 && value.isVisible && father.isShowSettings) {
            if (inputting) {
                setString()
            } else {
                inputting = true
                timer.reset()
            }
        } else {
            inputting = false
            setString()
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        if (inputting) {
            when (keyCode) {
                Keyboard.KEY_ESCAPE -> return
                Keyboard.KEY_BACK -> {
                    backspace()
                }

                Keyboard.KEY_RETURN -> {
                    setString()
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                stringRef += typedChar
            }
        }
    }
}