package me.jiyun233.nya.settings;

import me.jiyun233.nya.module.AbstractModule;
import me.jiyun233.nya.module.AbstractModule;

public class LongSetting extends NumberSetting<Long> {
    public Long maxValue;
    public Long minValue;

    public LongSetting(String name, Long value, Long minValue, Long maxValue, AbstractModule father) {
        super(name, value, father);
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    public LongSetting booleanVisible(BooleanSetting setting) {
        return (LongSetting) this.visible(v -> setting.getValue());
    }

    public LongSetting booleanDisVisible(BooleanSetting setting) {
        return (LongSetting) this.visible(v -> !setting.getValue());
    }

    public LongSetting modeVisible(ModeSetting<?> setting, Enum<?> currentValue) {
        return (LongSetting) this.visible(v -> setting.getValue().equals(currentValue));
    }

    public LongSetting modeOrVisible(ModeSetting<?> setting, Enum<?> currentValue, Enum<?> secondValue) {
        return (LongSetting) this.visible(v -> setting.getValue().equals(currentValue) || setting.getValue().equals(secondValue));
    }

    public LongSetting modeDisVisible(ModeSetting<?> setting, Enum<?> currentValue) {
        return (LongSetting) this.visible(v -> !setting.getValue().equals(currentValue));
    }
}
