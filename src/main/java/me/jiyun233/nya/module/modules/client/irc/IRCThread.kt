package me.jiyun233.nya.module.modules.client.irc

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.modules.client.IRC
import me.jiyun233.nya.utils.client.ChatUtil
import net.minecraft.client.Minecraft
import net.minecraft.util.text.TextComponentString
import java.io.*
import java.net.Socket
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


class IRCThread(val socket: Socket) : Thread() {

    override fun run() {
        val reader = BufferedReader(InputStreamReader(socket.getInputStream(),"UTF-8"))
        while (!socket.isClosed) {
            try {
                val line = reader.readLine()
                if (line.isNotEmpty()) {
                    if (line.startsWith("[LOGIN_FAILED]")) {
                        ChatUtil.sendMessage("&cIRC Connect failed: there has a same name in server")
                        if (IRC.isEnabled) IRC.disable()
                    }
                    if (line.startsWith("[CHAT]")) {
                        ChatUtil.sendMessage(
                            ChatUtil.translateAlternateColorCodes(
                                "&8[&6&lIRC&8]&r${line.substring(6)}"
                            )
                        )
                    }
                }
            } catch (ex: Exception) {
                ChatUtil.sendMessage("&cIRC connect closed!")
                IRC.disable()
                socket.close()
                this.isInterrupted
            }
        }
    }


    fun sendMessage(message: String) {
        try {
            PrintStream(socket.getOutputStream(),false,"UTF-8").println(message)
        } catch (e: Exception) {
            ChatUtil.sendMessage("&cChat failed, socket closed!")
        }
    }
}