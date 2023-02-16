package me.jiyun233.nya.guis

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.guis.buttons.ModuleButton
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.modules.client.ClickGui
import me.jiyun233.nya.utils.render.Render2DUtil
import java.awt.Color
import java.util.*

class CategoryPanel(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float,
    var category: Category,
) {
    val modules = arrayListOf<ModuleButton>()
    var isShowModules = true
    private var dragging = false
    private var x2 = 0.0f
    private var y2 = 0.0f

    init {
        for (module in NyaHack.moduleManager!!.getModulesByCategory(this.category).sortedBy { it.name }) {
            modules.add(ModuleButton(this.width, this.height * 0.85f, this, module))
        }
    }

    fun onDraw(mouseX: Int, mouseY: Int) {
        if (this.dragging) {
            this.x = this.x2 + mouseX
            this.y = this.y2 + mouseY
        }
        Render2DUtil.drawRect(x, y, width, height, ClickGui.getCurrentColor().rgb)
        NyaHack.fontManager!!.IconFont.drawStringWithShadow(
            category.icon,
            x + 3,
            y + (height / 2) - (NyaHack.fontManager!!.IconFont.height / 2),
            Color.WHITE
        )
        NyaHack.fontManager!!.CustomFont.drawStringWithShadow(
            category.getName(),
            x + 5 + NyaHack.fontManager!!.IconFont.getWidth(category.icon),
            y + (height / 2) - (NyaHack.fontManager!!.CustomFont.height / 2),
            Color.WHITE
        )
        if (modules.isEmpty() || !isShowModules) return
        var calcYPos = this.y + this.height
        for (moduleButton in modules) {
            moduleButton.drawButton(this.x, calcYPos, mouseX, mouseY)
            calcYPos += moduleButton.height
            if (moduleButton.isShowSettings && moduleButton.settings.isNotEmpty()) {
                var buttonX: Double = (moduleButton.x + moduleButton.height).toDouble()
                for (settingButton in moduleButton.settings.filter { it.value.isVisible }) {
                    settingButton.drawButton(x + (this.width - settingButton.width) / 2, calcYPos, mouseX, mouseY)
                    calcYPos += settingButton.height
                    buttonX = (x + (this.width - settingButton.width) / 2).toDouble()
                }
                Render2DUtil.drawLine(
                    buttonX,
                    (moduleButton.y + moduleButton.height).toDouble(),
                    buttonX,
                    calcYPos.toDouble(),
                    0.75f,
                    ClickGui.getCurrentColor()
                )
            }
        }
    }

    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0 && this.isHoveredCategoryTab(mouseX, mouseY)) {
            this.x2 = this.x - mouseX
            this.y2 = this.y - mouseY
            this.dragging = true
        }
        modules.forEach { it.mouseClicked(mouseX, mouseY, mouseButton) }
    }

    fun mouseReleased(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            this.dragging = false
        }
        if (mouseButton == 1 && this.isHoveredCategoryTab(mouseX, mouseY)) {
            this.isShowModules = !this.isShowModules
        }
        modules.forEach { it.mouseReleased(mouseX, mouseY, mouseButton) }
    }

    private fun isHoveredCategoryTab(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height
    }

    fun keyTyped(typedChar: Char, keyCode: Int) {
        modules.forEach { it.keyTyped(typedChar, keyCode) }
    }
}