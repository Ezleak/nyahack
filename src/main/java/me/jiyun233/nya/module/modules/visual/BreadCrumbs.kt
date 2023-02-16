package me.jiyun233.nya.module.modules.visual

import me.jiyun233.nya.module.Category
import me.jiyun233.nya.module.Module
import me.jiyun233.nya.module.ModuleInfo
import me.jiyun233.nya.module.modules.client.ClickGui
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import kotlin.math.hypot

@ModuleInfo(name = "BreadCrumbs", descriptions = "Draws a small line behind you", category = Category.VISUAL)
class BreadCrumbs : Module() {
    private var length = registerSetting("Length", 15, 5, 100)
    private var lineWidth = registerSetting("Width", 1.5f, 0.5f, 10.0f)
    private var vets = ArrayList<DoubleArray>()

    override fun onUpdate() {
        try {
            val renderPosX = mc.getRenderManager().renderPosX
            val renderPosY = mc.getRenderManager().renderPosY
            val renderPosZ = mc.getRenderManager().renderPosZ
            for (next in mc.world.playerEntities) {
                if (next == null) continue
                val b = next === mc.player
                var n =
                    renderPosY + java.lang.Double.longBitsToDouble(java.lang.Double.doubleToLongBits(0.48965838138858014) xor 0x7FDF56901B91AE07L)
                if (mc.player.isElytraFlying) n -= java.lang.Double.longBitsToDouble(
                    java.lang.Double.doubleToLongBits(
                        29.56900080933637
                    ) xor 0x7FC591AA097B7F4BL
                )
                if (!b) continue
                vets.add(doubleArrayOf(renderPosX, n - next.height, renderPosZ))
            }
        } catch (ignored: Exception) {
        }
        if (vets.size > length.value) vets.removeAt(0)
    }

    override fun onDisable() {
        vets.clear()
    }

    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent?) {
        try {
            val renderPosX = mc.getRenderManager().renderPosX
            val renderPosY = mc.getRenderManager().renderPosY
            val renderPosZ = mc.getRenderManager().renderPosZ
            val n =
                ClickGui.getCurrentColor()!!.red / java.lang.Float.intBitsToFloat(
                    java.lang.Float.floatToIntBits(
                        0.49987957f
                    ) xor 0x7D80F037
                )
            val n2 =
                ClickGui.getCurrentColor()!!.green / java.lang.Float.intBitsToFloat(
                    java.lang.Float.floatToIntBits(
                        0.4340212f
                    ) xor 0x7DA13807
                )
            val n3 =
                ClickGui.getCurrentColor()!!.blue / java.lang.Float.intBitsToFloat(
                    java.lang.Float.floatToIntBits(
                        0.0131841665f
                    ) xor 0x7F270267
                )
            prepareGL()
            GL11.glPushMatrix()
            GL11.glEnable(2848)
            GL11.glLineWidth(lineWidth.value)
            GL11.glBlendFunc(770, 771)
            GL11.glLineWidth(lineWidth.value)
            GL11.glDepthMask(false)
            GL11.glBegin(3)
            for (vet in vets) {
                val d = 0.0
                val m = drawM(hypot(vet[0] - mc.player.posX, vet[1] - mc.player.posY))
                if (d > length.value) {
                    continue
                }
                GL11.glColor4f(
                    n,
                    n2,
                    n3,
                    java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(14.099797f) xor 0x7EE198C5) - (m / length.value).toFloat()
                )
                GL11.glVertex3d(vet[0] - renderPosX, vet[1] - renderPosY, vet[2] - renderPosZ)
            }
            GL11.glEnd()
            GL11.glDepthMask(true)
            GL11.glPopMatrix()
            releaseGL()
        } catch (ignored: Exception) {
        }
    }

    private fun drawM(n: Double): Double {
        if (n == java.lang.Double.longBitsToDouble(java.lang.Double.doubleToLongBits(1.7931000183463725E308) xor 0x7FEFEB11C3AAD037L)) return n
        return if (n < java.lang.Double.longBitsToDouble(java.lang.Double.doubleToLongBits(1.1859585260803721E308) xor 0x7FE51C5AEE8AD07FL)) n * java.lang.Double.longBitsToDouble(
            java.lang.Double.doubleToLongBits(-12.527781766526259) xor 0x7FD90E3969654F8FL
        ) else n
    }

    private fun prepareGL() {
        GL11.glBlendFunc(770, 771)
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        GlStateManager.glLineWidth(java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(5.0675106f) xor 0x7F22290C))
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.enableAlpha()
        GlStateManager.color(
            java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(11.925059f) xor 0x7EBECD0B),
            java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(18.2283f) xor 0x7E11D38F),
            java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(9.73656f) xor 0x7E9BC8F3)
        )
    }

    private fun releaseGL() {
        GlStateManager.enableCull()
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.enableDepth()
        GlStateManager.color(
            java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(12.552789f) xor 0x7EC8D839),
            java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(7.122752f) xor 0x7F63ED96),
            java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(5.4278784f) xor 0x7F2DB12E)
        )
        GL11.glColor4f(
            java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(10.5715685f) xor 0x7EA92525),
            java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(4.9474883f) xor 0x7F1E51D3),
            java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(4.9044757f) xor 0x7F1CF177),
            java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(9.482457f) xor 0x7E97B825)
        )
    }
}