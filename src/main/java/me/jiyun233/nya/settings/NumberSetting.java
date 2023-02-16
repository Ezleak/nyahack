package me.jiyun233.nya.settings;

import me.jiyun233.nya.module.AbstractModule;
import me.jiyun233.nya.module.AbstractModule;

public abstract class NumberSetting<T extends Number> extends Setting<T> {

    public NumberSetting(String name, T value, AbstractModule father) {
        super(name, value, father);
    }
}
