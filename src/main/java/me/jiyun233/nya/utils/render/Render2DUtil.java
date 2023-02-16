package me.jiyun233.nya.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;

public class Render2DUtil {

    public static void setColor(Color color) {
        GL11.glColor4d((double) color.getRed() / 255.0, (double) color.getGreen() / 255.0, (double) color.getBlue() / 255.0, (double) color.getAlpha() / 255.0);
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y + h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x + w, y + h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x + w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawOutlineRect(double x, double y, double w, double h, float lineWidth, Color color) {
        drawLine(x, y, x + w, y, lineWidth, color);
        drawLine(x, y, x, y + h, lineWidth, color);
        drawLine(x, y + h, x + w, y + h, lineWidth, color);
        drawLine(x + w, y, x + w, y + h, lineWidth, color);
    }

    public static void drawLine(Double x1, Double y1, Double x2, Double y2, Float lineWidth) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glLineWidth(lineWidth);
        GL11.glShadeModel(7425);
        GL11.glBegin(2);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static void enableScissorArea(int x,int y,int width,int height){
        //GL11.glScissor(x, y, width, height);
        glScissor(x,y,x + width,y + height,new ScaledResolution(Minecraft.getMinecraft()));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void glScissor(float x, float y, float x1, float y1, ScaledResolution sr) {
        GL11.glScissor((int) (x * sr.getScaleFactor()), (int) (Minecraft.getMinecraft().displayHeight - y1 * sr.getScaleFactor()), (int) ((x1 - x) * sr.getScaleFactor()), (int) ((y1 - y) * sr.getScaleFactor()));
    }

    public static void disableScissorArea(){
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void drawArc(double cx, double cy, double r, double start_angle, double end_angle, int num_segments) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBegin(4);
        int i = (int)((double)num_segments / (360.0 / start_angle)) + 1;
        while ((double)i <= (double)num_segments / (360.0 / end_angle)) {
            double previousangle = Math.PI * 2 * (double)(i - 1) / (double)num_segments;
            double angle = Math.PI * 2 * (double)i / (double)num_segments;
            GL11.glVertex2d(cx, cy);
            GL11.glVertex2d(cx + Math.cos(angle) * r, cy + Math.sin(angle) * r);
            GL11.glVertex2d(cx + Math.cos(previousangle) * r, cy + Math.sin(previousangle) * r);
            ++i;
        }
        if (end_angle == 360) {
            GL11.glVertex2d(cx, cy);
            GL11.glVertex2d(cx + Math.cos(360) * r, cy + Math.sin(360) * r);
        }
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static void drawGradientRect(double x, double y, double width, double height, GradientDirection direction, Color startColor, Color endColor) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glBegin(7);
        setColor(result[0]);
        GL11.glVertex2d(x + width, y);
        setColor(result[1]);
        GL11.glVertex2d(x, y);
        setColor(result[2]);
        GL11.glVertex2d(x, y + height);
        setColor(result[3]);
        GL11.glVertex2d(x + width, y + height);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static void drawRoundedRectangle(double x, double y, double width, double height, double radius, GradientDirection direction, Color startColor, Color endColor) {
        if (width < radius * 2.0 || height < radius * 2.0) {
            return;
        }
        GL11.glShadeModel(7425);
        Color[] result = checkColorDirection(direction, startColor, endColor);
        setColor(result[0]);
        drawArc(x + width - radius, y + height - radius, radius, 0.0, 90.0, 16);
        setColor(result[1]);
        drawArc(x + radius, y + height - radius, radius, 90.0, 180.0, 16);
        setColor(result[2]);
        drawArc(x + radius, y + radius, radius, 180.0, 270.0, 16);
        setColor(result[3]);
        drawArc(x + width - radius, y + radius, radius, 270.0, 360.0, 16);
        drawGradientRect(x + radius, y, width - radius * 2.0, radius, GradientDirection.LeftToRight, result[2], result[3]);
        drawGradientRect(x + radius, y + height - radius, width - radius * 2.0, radius, GradientDirection.LeftToRight, result[1], result[0]);
        drawGradientRect(x, y + radius, radius, height - radius * 2.0, GradientDirection.DownToUp, result[1], result[2]);
        drawGradientRect(x + width - radius, y + radius, radius, height - radius * 2.0, GradientDirection.DownToUp, result[0], result[3]);
        drawGradientRect(x + radius, y + radius, width - radius * 2.0, height - radius * 2.0, direction, startColor, endColor);
        GL11.glShadeModel(7424);
    }

    public static void drawRoundedRectangle(double x, double y, double width, double height, double radius, Color color) {
        drawRoundedRectangle(x, y, width, height, radius, GradientDirection.Normal, color, color);
    }

    public static void drawLine(double x1, double y1, double x2, double y2, float lineWidth, Color ColorStart) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glLineWidth(lineWidth);
        GL11.glShadeModel(7425);
        GL11.glBegin(2);
        setColor(ColorStart);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static void drawGradientRectOutline(double x, double y, double width, double height, GradientDirection direction, Color startColor, Color endColor) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glBegin(2);
        GL11.glColor4f((float)result[2].getRed() / 255.0f, (float)result[2].getGreen() / 255.0f, (float)result[2].getBlue() / 255.0f, (float)result[2].getAlpha() / 255.0f);
        GL11.glVertex2d(x + width, y);
        GL11.glColor4f((float)result[3].getRed() / 255.0f, (float)result[3].getGreen() / 255.0f, (float)result[3].getBlue() / 255.0f, (float)result[3].getAlpha() / 255.0f);
        GL11.glVertex2d(x, y);
        GL11.glColor4f((float)result[0].getRed() / 255.0f, (float)result[0].getGreen() / 255.0f, (float)result[0].getBlue() / 255.0f, (float)result[0].getAlpha() / 255.0f);
        GL11.glVertex2d(x, y + height);
        GL11.glColor4f((float)result[1].getRed() / 255.0f, (float)result[1].getGreen() / 255.0f, (float)result[1].getBlue() / 255.0f, (float)result[1].getAlpha() / 255.0f);
        GL11.glVertex2d(x + width, y + height);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static void drawImage(ResourceLocation resourceLocation, float f, float f2, float f3, float f4) {
        drawImage(resourceLocation, (int)f, (int)f2, (int)f3, (int)f4, 1.0f);
    }

    public static void drawImage(ResourceLocation resourceLocation, int n, int n2, int n3, int n4, float f) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture((int)n, (int)n2, (float)0.0f, (float)0.0f, (int)n3, (int)n4, (float)n3, (float)n4);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    private static Color[] checkColorDirection(GradientDirection direction, Color start, Color end) {
        Color[] dir = new Color[4];
        if (direction == GradientDirection.Normal) {
            Arrays.fill(dir, start);
        } else if (direction == GradientDirection.DownToUp) {
            dir[0] = start;
            dir[1] = start;
            dir[2] = end;
            dir[3] = end;
        } else if (direction == GradientDirection.UpToDown) {
            dir[0] = end;
            dir[1] = end;
            dir[2] = start;
            dir[3] = start;
        } else if (direction == GradientDirection.RightToLeft) {
            dir[0] = start;
            dir[1] = end;
            dir[2] = end;
            dir[3] = start;
        } else if (direction == GradientDirection.LeftToRight) {
            dir[0] = end;
            dir[1] = start;
            dir[2] = start;
            dir[3] = end;
        } else {
            Arrays.fill(dir, Color.WHITE);
        }
        return dir;
    }

    public enum GradientDirection {
        LeftToRight,
        RightToLeft,
        UpToDown,
        DownToUp,
        Normal
    }
}
