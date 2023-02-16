package me.jiyun233.nya.module.modules.world;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.StringSetting;
import me.jiyun233.nya.utils.client.ChatUtil;
import com.mojang.authlib.GameProfile;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.StringSetting;
import me.jiyun233.nya.utils.client.ChatUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.UUID;

@ModuleInfo(name = "FakePlayer", descriptions = "Spawn other player", category = Category.WORLD)
public class FakePlayer extends Module {
    private final StringSetting name = registerSetting("name", "FakePlayer");
    private EntityOtherPlayerMP otherPlayer;

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
            return;
        }
        this.otherPlayer = null;
        if (mc.player != null) {
            this.otherPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.randomUUID(), this.name.getValue()));
            ChatUtil.sendMessage(ChatUtil.AQUA + String.format("%s has been spawned.", this.name.getValue()));
            this.otherPlayer.copyLocationAndAnglesFrom(mc.player);
            this.otherPlayer.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntityToWorld(-100, this.otherPlayer);
        }
    }

    @Override
    public void onDisable() {
        if (mc.world != null && mc.player != null) {
            super.onDisable();
            if (otherPlayer == null) return;
            mc.world.removeEntity(this.otherPlayer);
        }
    }
}

