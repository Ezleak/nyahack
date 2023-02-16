package me.jiyun233.nya.guis

import me.jiyun233.nya.NyaHack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

class MainMenuShaderHelper {
    val initTime = System.currentTimeMillis()

    fun createShader(path: String, shaderType: Int): Int {
        val source = this.javaClass.getResourceAsStream(path).readBytes().decodeToString()
        val shaderId = GL20.glCreateShader(shaderType)
        GL20.glShaderSource(shaderId, source)
        GL20.glCompileShader(shaderId)
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            GL20.glDeleteShader(shaderId)
            println("Failed to compile shader $path")
        }
        return shaderId
    }

    fun getShaderProgram(vshShader : String,fshShader : String) : Int{
        return GL20.glCreateProgram().also {
            val vshID = createShader(vshShader,GL20.GL_VERTEX_SHADER)
            val fshID = createShader(fshShader,GL20.GL_FRAGMENT_SHADER)
            GL20.glAttachShader(it,vshID)
            GL20.glAttachShader(it,fshID)
            GL20.glLinkProgram(it)
            if (GL20.glGetProgrami(it, GL20.GL_LINK_STATUS) == 0) {
                NyaHack.logger.info("Failed to link shader " + GL20.glGetProgramInfoLog(it, 1024))
                GL20.glDeleteProgram(it)
                GL20.glDeleteShader(vshID)
                GL20.glDeleteShader(fshID)
                return@also
            }
            GL20.glDetachShader(it, vshID)
            GL20.glDetachShader(it, fshID)
            GL20.glDeleteShader(vshID)
            GL20.glDeleteShader(fshID)
        }
    }

    fun drawShader(shaderProgramId : Int){
        val timeUniform = GL20.glGetUniformLocation(shaderProgramId, "time")
        val mouseUniform = GL20.glGetUniformLocation(shaderProgramId, "mouse")
        val resolutionUniform = GL20.glGetUniformLocation(shaderProgramId, "resolution")

        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        GlStateManager.enableTexture2D()
        GlStateManager.enableBlend()
        GL20.glUseProgram(shaderProgramId)


        val width = Minecraft.getMinecraft().displayWidth.toFloat()
        val height = Minecraft.getMinecraft().displayHeight.toFloat()

        GL20.glUniform2f(resolutionUniform, width, height)
        GL20.glUniform4f(mouseUniform,0f ,0f,0f,0f)
        GL20.glUniform1f(timeUniform, ((System.currentTimeMillis() - initTime) / 1000.0).toFloat())

        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        buffer.pos(-1.0, -1.0, 0.2).endVertex()
        buffer.pos(1.0, -1.0, 0.2).endVertex()
        buffer.pos(1.0, 1.0, 0.2).endVertex()
        buffer.pos(-1.0, 1.0, 0.2).endVertex()
        tessellator.draw()

        GL20.glUseProgram(0)
        GlStateManager.disableTexture2D()
    }
}