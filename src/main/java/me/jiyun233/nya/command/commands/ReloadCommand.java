package me.jiyun233.nya.command.commands;

import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.command.Command;
import me.jiyun233.nya.command.CommandInfo;
import me.jiyun233.nya.utils.client.ChatUtil;

@CommandInfo(name = "reload", aliases = {"reloadConfig"}, descriptions = "reload configuration file", usage = "reload")
public class ReloadCommand extends Command {

    @Override
    public void execute(String[] args) {
        NyaHack.configManager.loadAll();
        ChatUtil.sendMessage("&aSuccess reload all configurations");
    }
}
