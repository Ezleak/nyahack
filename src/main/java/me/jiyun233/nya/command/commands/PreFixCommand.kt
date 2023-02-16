package me.jiyun233.nya.command.commands

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.command.Command
import me.jiyun233.nya.command.CommandInfo
import me.jiyun233.nya.utils.client.ChatUtil

@CommandInfo(name = "prefix", descriptions = "Change command prefix", usage = "prefix <char>")
class PreFixCommand : Command() {
    override fun execute(args: Array<String>) {
        if (args.isEmpty()) {
            ChatUtil.sendMessage("&c&lUsage: prefix <char>")
            return
        }
        NyaHack.commandPrefix = args[0]
        NyaHack.configManager.saveCommand()
        ChatUtil.sendNoSpamMessage("&aPrefix set to ${args[0]}")
    }
}