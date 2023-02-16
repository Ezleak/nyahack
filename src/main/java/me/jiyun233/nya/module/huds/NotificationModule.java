package me.jiyun233.nya.module.huds;

import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.module.*;
import me.jiyun233.nya.settings.FloatSetting;

@ModuleInfo(name = "Notifications", category = Category.CLIENT, descriptions = "Show notification")
public class NotificationModule extends Module {

    public FloatSetting speed = registerSetting("Speed", 0.8f, 0.1f, 1.5f);

    public static NotificationModule INSTANCE;

    public NotificationModule() {
        INSTANCE = this;
    }

    @Override
    public void onRender2D() {
        NyaHack.notificationsManager.draw();
    }
}
