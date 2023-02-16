package me.jiyun233.nya.module.modules.function;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.ModeSetting;
import me.jiyun233.nya.utils.client.ChatUtil;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@ModuleInfo(name = "AutoPorn", descriptions = "Auto open e ro i website", category = Category.FUNCTION)
public class AutoPorn extends Module {
    private final ModeSetting<website> web = registerSetting("WebSite", website.PORNHUB);

    @Override
    public void onEnable() {
        try {
            Desktop.getDesktop().browse(URI.create(web.getValue().web));
            ChatUtil.sendMessage("&cHas been open the link: " + web.getValue().web);
            this.disable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private enum website {
        PORNHUB("https://www.pornhub.com/"),
        PIXIV("https://www.pixiv.net/");

        public final String web;

        website(String s) {
            this.web = s;
        }
    }
}
