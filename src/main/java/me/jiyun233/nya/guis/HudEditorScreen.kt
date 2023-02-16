package me.jiyun233.nya.guis

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.modules.client.HudEditor
import me.jiyun233.nya.utils.render.Render2DUtil
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Mouse
import java.awt.Color

class HudEditorScreen : GuiScreen() {
    val panels = arrayListOf<CategoryPanel>()

    init {
        var x = 20.0f
        for (category in Category.values().asList().stream().filter { it.isHud }) {
            panels.add(
                CategoryPanel(
                    x,
                    35.0f,
                    105.0f,
                    20.0f,
                    category
                )
            )
            x += 110
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        checkDWHeel()
        for (panel in panels) {
            panel.onDraw(mouseX, mouseY)
        }
        panels.forEach {
            for (moduleButton in it.modules) {
                if (moduleButton.isHoveredButton(mouseX, mouseY)) {
                    Render2DUtil.drawRect(
                        mouseX + 0.3f,
                        mouseY + 0.3f,
                        NyaHack.fontManager.CustomFont.getWidth(moduleButton.father.descriptions) + 2f,
                        NyaHack.fontManager.CustomFont.height + 0.3f,
                        Color(0, 0, 0, 115).rgb
                    )
                    Render2DUtil.drawOutlineRect(
                        mouseX + 0.3,
                        mouseY + 0.3,
                        NyaHack.fontManager.CustomFont.getWidth(moduleButton.father.descriptions) + 2.0,
                        NyaHack.fontManager.CustomFont.height + 0.3,
                        1.0f,
                        Color(0, 0, 0, 115)
                    )
                    NyaHack.fontManager.CustomFont.drawStringWithShadow(
                        moduleButton.father.descriptions,
                        mouseX + 1.5f,
                        mouseY + 1.5f,
                        Color.WHITE
                    )
                }
            }
        }
    }

    private fun checkDWHeel() {
        val dWheel: Int = Mouse.getDWheel()
        if (dWheel < 0) {
            panels.forEach { it.y -= 10f }
        } else if (dWheel > 0) {
            panels.forEach { it.y += 10f }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mousebutton: Int) {
        panels.forEach { it.mouseClicked(mouseX, mouseY, mousebutton) }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, mousebutton: Int) {
        panels.forEach { it.mouseReleased(mouseX, mouseY, mousebutton) }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1) {
            HudEditor.INSTANCE.disable()
        }
        panels.forEach { it.keyTyped(typedChar, keyCode) }
    }

    override fun onGuiClosed() {
        if (HudEditor.INSTANCE.isEnabled) {
            HudEditor.INSTANCE.disable()
        }
        NyaHack.configManager.saveAll()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }
}