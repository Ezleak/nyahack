package me.jiyun233.nya.command.commands;

import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.command.Command;
import me.jiyun233.nya.command.CommandInfo;
import me.jiyun233.nya.utils.client.ChatUtil;

import java.util.Arrays;

@CommandInfo(name = "help", aliases = {"?", "h"}, descriptions = "Show command list", usage = "Help")
public class HelpCommand extends Command {
    @Override
    public void execute(String[] args) {
        ChatUtil.sendMessage("Commands list:");
        for (Command command : NyaHack.commandManager.getCommands()) {
            ChatUtil.sendColoredMessage("&bCommand: &6" + command.name + "&b " + command.descriptions + " &bUsage: " + command.usage + " &bAliases: " + Arrays.toString(command.aliases));
        }
    }
}
