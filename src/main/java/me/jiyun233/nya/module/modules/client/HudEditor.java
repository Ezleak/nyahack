package me.jiyun233.nya.module.modules.client;

import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.guis.HudEditorScreen;
import me.jiyun233.nya.managers.ConfigManager;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "HudEditor",
        descriptions = "open Hud screen",
        category = Category.CLIENT,
        defaultKeyBind = Keyboard.KEY_GRAVE
)
public class HudEditor extends Module {

    public static HudEditor INSTANCE;

    public HudEditor() {
        INSTANCE = this;
    }

    public void onEnable() {
        if (!this.fullNullCheck() && !(Module.mc.currentScreen instanceof HudEditorScreen)) {
            Module.mc.displayGuiScreen(NyaHack.hudEditor);
        }
    }

    public void onDisable() {
        if (!this.fullNullCheck() && Module.mc.currentScreen instanceof HudEditorScreen) {
            Module.mc.displayGuiScreen(null);
            ConfigManager configManager = NyaHack.configManager;
            configManager.saveAll();
        }

    }
}
