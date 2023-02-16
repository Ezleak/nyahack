package me.jiyun233.nya.module.huds;

import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.module.*;
import me.jiyun233.nya.module.modules.client.ClickGui;
import me.jiyun233.nya.settings.ModeSetting;

import java.util.Comparator;
import java.util.stream.Collectors;

@HudModuleInfo(name = "ModuleArrayList", descriptions = "Show all enable module", category = Category.HUD, y = 100, x = 100)
public class ModuleArrayList extends HudModule {

    public ModeSetting<?> alignSetting = registerSetting("Align", alignMode.Left);
    public ModeSetting<?> sortSetting = registerSetting("Sort", sortMode.Top);
    public int count = 0;

    @Override
    public void onRender2D() {
        count = 0;
        NyaHack.moduleManager.getModuleList().stream()
                .filter(AbstractModule::isEnabled)
                .filter(module -> !module.category.isHud)
                .filter(module -> ((Module) module).visible.getValue())
                .sorted(Comparator.comparing(module -> NyaHack.fontManager.CustomFont.getWidth(module.getFullHud())
                        * (sortSetting.getValue().equals(sortMode.Bottom) ? 1 : -1)))
                .forEach(module -> {
                    float modWidth = NyaHack.fontManager.CustomFont.getWidth(module.getFullHud());
                    String modText = module.getFullHud();
                    if (alignMode.Right.equals(alignSetting.getValue())) {
                        NyaHack.fontManager.CustomFont.drawStringWithShadow(modText,
                                (int) (this.x - 2 - modWidth + this.width),
                                this.y + (10 * count),
                                ClickGui.getCurrentColor().getRGB());
                    } else {
                        NyaHack.fontManager.CustomFont.drawStringWithShadow(modText,
                                this.x - 2,
                                this.y + (10 * count),
                                ClickGui.getCurrentColor().getRGB());
                    }
                    count++;
                });
        width = NyaHack.moduleManager.getModuleList().stream()
                .filter(AbstractModule::isEnabled)
                .filter(module -> !module.category.isHud)
                .noneMatch(module -> ((Module) module).visible.getValue()) ? 20 :
                NyaHack.fontManager.CustomFont.getWidth(NyaHack.moduleManager.getModuleList()
                        .stream().filter(AbstractModule::isEnabled)
                        .filter(module -> !module.category.isHud)
                        .filter(module -> ((Module) module).visible.getValue())
                        .sorted(Comparator.comparing(module -> NyaHack.fontManager.CustomFont.getWidth(module.getFullHud()) * (-1)))
                        .collect(Collectors.toList()).get(0).getFullHud());
        height = ((NyaHack.fontManager.CustomFont.getHeight() + 1) *
                (int) NyaHack.moduleManager.getModuleList().stream()
                        .filter(AbstractModule::isEnabled)
                        .filter(module -> !module.category.isHud).count());
    }

    enum alignMode {
        Left,
        Right
    }

    enum sortMode {
        Top,
        Bottom
    }
}
