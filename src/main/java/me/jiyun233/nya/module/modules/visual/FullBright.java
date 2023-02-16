package me.jiyun233.nya.module.modules.visual;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.FloatSetting;
import me.jiyun233.nya.settings.IntegerSetting;
import me.jiyun233.nya.settings.ModeSetting;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.FloatSetting;
import me.jiyun233.nya.settings.ModeSetting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

@ModuleInfo(name = "FullBright", descriptions = "Always light", category = Category.VISUAL)
public class FullBright extends Module {

    ModeSetting<?> modeSetting = registerSetting("Mode", mode.GAMMA);
    FloatSetting gamma = registerSetting("Gamma", 800f, -10f, 1000f).modeOrVisible(modeSetting, mode.GAMMA, mode.BOTH);

    @Override
    public void onEnable() {
        if (modeSetting.getValue().equals(mode.GAMMA) || modeSetting.getValue().equals(mode.BOTH)) {
            mc.gameSettings.gammaSetting = gamma.getValue();
        }
        if (modeSetting.getValue().equals(mode.POTION) || modeSetting.getValue().equals(mode.BOTH)) {
            mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 100));
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = 1f;
        mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
    }

    private enum mode {
        GAMMA,
        POTION,
        BOTH
    }
}
