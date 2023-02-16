package me.jiyun233.nya.event.events.client;

import me.jiyun233.nya.event.events.EventStage;
import me.jiyun233.nya.settings.Setting;

public class SettingChangeEvent<T> extends EventStage {
    public final Setting<?> setting;

    public final T oldValue;

    public final T newValue;

    public SettingChangeEvent(Setting<?> setting, T oldValue, T newValue) {
        this.setting = setting;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
