package me.jiyun233.nya.command.commands

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.command.Command
import me.jiyun233.nya.command.CommandInfo
import me.jiyun233.nya.module.AbstractModule
import me.jiyun233.nya.settings.BindSetting
import me.jiyun233.nya.utils.client.ChatUtil
import org.lwjgl.input.Keyboard
import java.util.*


@CommandInfo(name = "bind", aliases = ["KeyBind"],descriptions = "Bind module to key",usage = "bind <module> <key>")
class ModuleBindCommand : Command() {
    override fun execute(args: Array<String>) {
        if (args.size == 1) {
            ChatUtil.sendNoSpamMessage("&cPlease specify a module.")
            return
        }
        try {
            val module = args[0]
            val rKey = args[1]
            val m: AbstractModule? = NyaHack.moduleManager.getModuleByName(module)
            if (m == null) {
                ChatUtil.sendNoSpamMessage("Unknown module '$module'!")
                return
            }
            val key = Keyboard.getKeyIndex(rKey.uppercase())
            if (Keyboard.KEY_NONE == key) {
                ChatUtil.sendMessage("&cUnknown Key $rKey")
                return
            }
            m.keyBind.value = BindSetting.KeyBind(key)
            ChatUtil.sendMessage("&aSuccess bind ${m.name} to key: ${args[1]}")
        } catch (e: Exception) {
            ChatUtil.sendMessage("&c&lUsage: bind <module> <bind>")
        }
    }
}