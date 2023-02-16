package me.jiyun233.nya.event

import me.jiyun233.nya.NyaHack
import me.jiyun233.nya.event.events.world.Render3DEvent
import me.jiyun233.nya.module.modules.client.IRC
import me.jiyun233.nya.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import org.lwjgl.input.Keyboard


class EventManager {
    val mc: Minecraft = Minecraft.getMinecraft()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        if (event.isCanceled || !Keyboard.getEventKeyState() || Keyboard.getEventKey() <= 0) return
        for (module in NyaHack.moduleManager!!.moduleList) {
            if (module.keyBind.value.keyCode <= 0) continue
            if (Keyboard.isKeyDown(module.keyBind.value.keyCode)) module.toggle()
        }
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (Utils.nullCheck()) return
        NyaHack.moduleManager!!.onUpdate()
    }

    @SubscribeEvent
    fun onLogin(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        NyaHack.moduleManager!!.onLogin()
    }

    @SubscribeEvent
    fun onLogout(event: FMLNetworkEvent.ClientDisconnectionFromServerEvent) {
        NyaHack.moduleManager!!.onLogout()
    }


    @SubscribeEvent
    fun onRender2D(e: RenderGameOverlayEvent.Text) {
        if (e.type == RenderGameOverlayEvent.ElementType.TEXT) {
            NyaHack.moduleManager!!.onRender2D()
        }
    }

    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        if (event.isCanceled) return
        mc.profiler.startSection("NyaHack")
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.shadeModel(7425)
        GlStateManager.disableDepth()
        GlStateManager.glLineWidth(1.0f)
        val render3dEvent = Render3DEvent(event.partialTicks)
        NyaHack.moduleManager!!.onRender3D(render3dEvent)
        GlStateManager.glLineWidth(1.0f)
        GlStateManager.shadeModel(7424)
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.enableCull()
        GlStateManager.enableCull()
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.enableDepth()
        mc.profiler.endSection()
    }

    @SubscribeEvent
    fun onChat(event: ClientChatEvent) {
        if (event.message.startsWith(NyaHack.commandPrefix)) {
            NyaHack.commandManager.run(event.message)
            event.isCanceled = true;
            Minecraft.getMinecraft().ingameGUI.chatGUI.addToSentMessages(event.message);
        }
    }
}