package me.jiyun233.nya;

import me.jiyun233.nya.command.CommandManager;
import me.jiyun233.nya.event.EventManager;
import me.jiyun233.nya.font.FontManager;
import me.jiyun233.nya.guis.ClickGuiScreen;
import me.jiyun233.nya.guis.HudEditorScreen;
import me.jiyun233.nya.managers.ConfigManager;
import me.jiyun233.nya.managers.FriendManager;
import me.jiyun233.nya.module.ModuleManager;
import me.jiyun233.nya.module.modules.client.IRC;
import me.jiyun233.nya.notification.NotificationManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;

@Mod(modid = NyaHack.MOD_ID, name = NyaHack.MOD_NAME, version = NyaHack.MOD_VERSION)
public class NyaHack {
    public static final String MOD_ID = "nya";
    public static final String MOD_NAME = "Nya Hack";
    public static final String MOD_VERSION = "0.3";
    public static final Logger logger = LogManager.getLogger("Nya");
    public static EventManager eventManager = null;
    public static FontManager fontManager = null;
    public static ClickGuiScreen clickGui = null;
    public static HudEditorScreen hudEditor = null;
    public static FriendManager friendManager = null;
    public static ModuleManager moduleManager = null;
    public static ConfigManager configManager = null;
    public static CommandManager commandManager = null;
    public static NotificationManager notificationsManager = null;
    public static String commandPrefix = ".";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger.info("Begin loading Nya Hack");
        Display.setTitle(MOD_NAME + " | " + MOD_VERSION);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        try {
            logger.info("Loading Nya Hack...");
            loadManagers();
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadManagers() throws IOException, FontFormatException {
        fontManager = new FontManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        eventManager = new EventManager();
        clickGui = new ClickGuiScreen();
        hudEditor = new HudEditorScreen();
        configManager = new ConfigManager();
        commandManager = new CommandManager();
        notificationsManager = NotificationManager.INSTANCE;
    }
}
