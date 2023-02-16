package me.jiyun233.nya.module.modules.client;

import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.guis.ClickGuiScreen;
import me.jiyun233.nya.managers.ConfigManager;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.BooleanSetting;
import me.jiyun233.nya.settings.FloatSetting;
import me.jiyun233.nya.settings.IntegerSetting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "ClickGui", descriptions = "open click gui screen", category = Category.CLIENT, defaultKeyBind = Keyboard.KEY_RSHIFT)
public final class ClickGui extends Module {
    public BooleanSetting rainbow = registerSetting("Rainbow", false);
    public IntegerSetting red = registerSetting("Red", 25, 0, 255).booleanDisVisible(rainbow);
    public IntegerSetting green = registerSetting("Green", 25, 0, 255).booleanDisVisible(rainbow);
    public IntegerSetting blue = registerSetting("Blue", 25, 0, 255).booleanDisVisible(rainbow);
    public FloatSetting speed = registerSetting("RainbowSpeed", 1.0f, 0.1f, 10.0f).booleanVisible(rainbow);
    public FloatSetting saturation = registerSetting("Saturation", 0.65f, 0.0f, 1.0f).booleanVisible(rainbow);
    public FloatSetting brightness = registerSetting("Brightness", 1.0f, 0.0f, 1.0f).booleanVisible(rainbow);

    public static ClickGui INSTANCE;

    public ClickGui() {
        INSTANCE = this;
    }

    public void onEnable() {
        if (!this.fullNullCheck() && !(Module.mc.currentScreen instanceof ClickGuiScreen)) {
            Module.mc.displayGuiScreen(NyaHack.clickGui);
        }

    }

    public void onDisable() {
        if (!this.fullNullCheck() && Module.mc.currentScreen instanceof ClickGuiScreen) {
            Module.mc.displayGuiScreen(null);
            ConfigManager configManager = NyaHack.configManager;
            configManager.saveAll();
        }
    }

    public static Color getRainbow() {
        float hue = (float) (System.currentTimeMillis() % 11520L) / 11520.0f * INSTANCE.speed.getValue();
        return new Color(Color.HSBtoRGB(hue, INSTANCE.saturation.getValue(), INSTANCE.brightness.getValue()));
    }

    public static Color getCurrentColor() {
        return INSTANCE.rainbow.getValue() ? getRainbow() : new Color(INSTANCE.red.getValue(), INSTANCE.green.getValue(), INSTANCE.blue.getValue());
    }
}
