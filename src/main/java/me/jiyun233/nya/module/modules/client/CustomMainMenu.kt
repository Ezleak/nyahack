package me.jiyun233.nya.module.modules.client

import me.jiyun233.nya.guis.MainMenu
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import net.minecraft.client.gui.GuiMainMenu
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@ModuleInfo(name = "CustomMainMenu", category = Category.CLIENT, defaultEnable = true, descriptions = "draw custom main menu")
object CustomMainMenu : Module() {

    private val isFpsLimit = registerSetting("FpsLimit", true)
    private val fpsLimit = registerSetting("MaxFPS", 60, 5, 240).booleanVisible(isFpsLimit)

    @JvmStatic
    fun fpsLimit(cir: CallbackInfoReturnable<Int>) {
        if (isFpsLimit.value && (mc.currentScreen is MainMenu || mc.currentScreen is GuiMainMenu)) {
            cir.returnValue = fpsLimit.value
        }
    }
}