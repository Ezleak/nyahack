package me.jiyun233.nya.module.modules.function;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.StringSetting;
import me.jiyun233.nya.utils.client.ChatUtil;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.StringSetting;
import me.jiyun233.nya.utils.client.ChatUtil;
import net.minecraft.util.text.TextComponentString;

@ModuleInfo(name = "FakeKick", descriptions = "Disconnection form server", category = Category.FUNCTION)
public class FakeKick extends Module {

    StringSetting msg = registerSetting("KickMessage", "&c&lYou has been banned form server!");

    @Override
    public void onEnable() {
        if (!mc.isSingleplayer()) {
            mc.player.connection.getNetworkManager().closeChannel(new TextComponentString(ChatUtil.translateAlternateColorCodes(msg.getValue())));
        } else {
            ChatUtil.sendMessage("&cCouldn't use it in SinglePlayer!");
        }
        this.disable();
    }
}
