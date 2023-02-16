package me.jiyun233.nya.module.modules.client

import com.google.gson.JsonObject
import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.module.modules.client.irc.IRCThread
import me.jiyun233.nya.utils.client.ChatUtil
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.PrintStream
import java.net.Socket

@ModuleInfo(name = "IRC", descriptions = "极云的聊天服务器Nya~", category = Category.CLIENT, defaultEnable = true)
object IRC : Module() {

    private val prefix = registerSetting("IRCPrefix", "#")

    var socket: Socket? = null

    var ircThread: IRCThread? = null


    override fun onEnable() {
        if (socket == null) {
            Thread {
                try {
                    socket = Socket("irc.hqpvp.top", 15028)
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("Name", mc.player.name)
                    jsonObject.addProperty("ClientName", "NyaHack")
                    PrintStream(socket!!.getOutputStream(), false, "UTF-8").println(
                        "[LOGIN]" + mc.session.username + "##" + NyaHack.MOD_NAME
                    )
                    ircThread = IRCThread(socket!!)
                    ircThread!!.start()
                    ChatUtil.sendMessage("&a&lConnection to IRCServer")
                } catch (e: Exception) {
                    ChatUtil.sendMessage("&cIRC Connect failed!")
                    this.disable()
                }
            }.start()
        }
    }

    override fun onUpdate() {
        if (socket == null) {
            this.enable()
        } else if (socket!!.isClosed) {
            this.disable()
            this.enable()
        }
    }

    override fun onLogin() {
        this.enable()
    }

    @SubscribeEvent
    fun onChat(event: ClientChatEvent) {
        if (event.message.startsWith(prefix.value)) {
            val substring = event.message.substring(prefix.value.length)
            if (substring.isEmpty()) {
                ChatUtil.sendMessage("&cNot a message!")
                return
            }
            if (ircThread != null && socket != null) {
                ircThread!!.sendMessage("[CHAT]$substring")
                event.isCanceled = true;
                Minecraft.getMinecraft().ingameGUI.chatGUI.addToSentMessages(event.message);
            }
        }
    }

    override fun onDisable() {
        try {
            if (socket != null) {
                if (!socket!!.isClosed) {
                    socket!!.shutdownInput()
                    socket!!.shutdownOutput()
                    socket!!.close()
                }
            }
            socket = null
            if (ircThread != null) {
                if (!ircThread!!.socket.isClosed) {
                    ircThread!!.socket.shutdownInput()
                    ircThread!!.socket.shutdownOutput()
                    ircThread!!.socket.close()
                }
                ircThread!!.isInterrupted
            }
            ircThread = null
        } catch (_: Exception) {
        }
    }
}