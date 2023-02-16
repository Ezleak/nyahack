package me.jiyun233.nya.guis;

import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.utils.IconFontKt;
import me.jiyun233.nya.utils.Timer;
import me.jiyun233.nya.utils.animations.Easing2D;
import me.jiyun233.nya.utils.animations.plus.Easing1D;
import me.jiyun233.nya.utils.render.Render2DUtil;
import net.minecraft.client.gui.*;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends GuiScreen {
    MainMenuShaderHelper backgroundShaderRenderer;

    private ScaledResolution scaledResolution;
    private List<Button> buttons;
    int id;

    @Override
    public void initGui() {
        if (scaledResolution != null && scaledResolution.getScaledWidth() == new ScaledResolution(mc).getScaledWidth() && scaledResolution.getScaledHeight() == new ScaledResolution(mc).getScaledHeight()) {
            return;
        }
        scaledResolution = new ScaledResolution(mc);
        backgroundShaderRenderer = new MainMenuShaderHelper();
        id = backgroundShaderRenderer.getShaderProgram("/assets/shaders/vsh/DefaultVertex.vsh", "/assets/shaders/fsh/matrix.fsh");
        buttons = new ArrayList<>();
        buttons.add(new Button("SinglePlayer", IconFontKt.TAG, scaledResolution.getScaledWidth() / 5 * 1, scaledResolution.getScaledHeight() / 2, 40, 40, () -> {
            mc.displayGuiScreen(new GuiWorldSelection(this));
        }));
        buttons.add(new Button("MultiPlayer", IconFontKt.TAGS, scaledResolution.getScaledWidth() / 5 * 2, scaledResolution.getScaledHeight() / 2, 40, 40, () -> {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }));
        buttons.add(new Button("Settings", IconFontKt.COG, scaledResolution.getScaledWidth() / 5 * 3, scaledResolution.getScaledHeight() / 2, 40, 40, () -> {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }));
        buttons.add(new Button("Exit", IconFontKt.ERROR, scaledResolution.getScaledWidth() / 5 * 4, scaledResolution.getScaledHeight() / 2, 40, 40, () -> {
            mc.shutdown();
        }));
        buttons.forEach(button -> button.easing2D.updatePos(new Vec2f(button.x, button.y)));
    }

    Timer alphaTimer = new Timer();
    Easing1D easing1D = new Easing1D(80f);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackGround();
        //Draw
        buttons.forEach(it -> it.onRender(mouseX, mouseY));
        if (alphaTimer.passed(800)) easing1D.updatePos(180f);
        if (alphaTimer.passed(1600)) {
            easing1D.updatePos(80f);
            alphaTimer.reset();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttons.forEach(button -> {
            button.onMouseClicked(mouseX, mouseY);
        });
    }

    private void drawBackGround() {
        backgroundShaderRenderer.drawShader(id);
    }

    class Button extends AbstractButton {
        public Easing2D easing2D = new Easing2D(new Vec2f(scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 + 90), 0);
        public Easing1D easing1D = new Easing1D(0f);

        public Button(String displayText, String icon, float x, float y, float width, float height, VoidTask action) {
            super(displayText, icon, x, y, width, height, action);

        }

        @Override
        public void onRender(int mouseX, int mouseY) {
            Vec2f vec2f = easing2D.getUpdate();
            float renderX = vec2f.x;
            float renderY = vec2f.y;
            if (isHovered(mouseX, mouseY)) {
                easing2D.updateSize(1.2f);
                easing1D.updatePos(width + 5f);
            } else {
                easing2D.updateSize(1.0f);
                easing1D.updatePos(0f);
            }
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);

            GL11.glEnable(GL11.GL_POINT_SMOOTH);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f);
            GL11.glPointSize(width * scaledResolution.getScaleFactor() * easing2D.getUpdateSize());
            GL11.glBegin(GL11.GL_POINTS);
            GL11.glVertex2f(renderX, renderY);
            GL11.glEnd();
            NyaHack.fontManager.IconFont.drawCenteredString(icon, renderX, renderY, 0);
            float lineWidth = easing1D.getUpdate();
            Render2DUtil.drawLine(renderX - lineWidth / 2f,renderY + height / 2f + 10f,renderX + lineWidth / 2f,renderY + height / 2f + 10f,3f,Color.orange);
            int sw = ((int) (NyaHack.fontManager.CustomFont.getWidth(displayText)));
            NyaHack.fontManager.CustomFont.drawString(displayText,x - sw / 2f - 1f,y + height + 1f,0xFFFFFF);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glTranslatef(0.5f, 0.5f, 0f);
            GL11.glNormal3f(1f, 1f, 0f);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }

        @Override
        public void onMouseClicked(int mouseX, int mouseY) {
            if (isHovered(mouseX, mouseY)) {
                action.invoke();
            }
        }
    }

    abstract class AbstractButton {
        String displayText;
        String icon;
        float x;
        float y;
        float width;
        float height;
        VoidTask action;

        public AbstractButton(String displayText, String icon, float x, float y, float width, float height, VoidTask action) {
            this.displayText = displayText;
            this.icon = icon;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.action = action;
        }

        public boolean isHovered(int mouseX, int mouseY) {
            return (float) mouseX >= Math.min(this.x - this.width / 2, this.x + this.width) && (float) mouseX <= Math.max(this.x - this.width / 2, this.x + this.width) && (float) mouseY >= Math.min(this.y - this.height / 2, this.y + this.height) && (float) mouseY <= Math.max(this.y - this.height / 2, this.y + this.height);
        }

        public abstract void onRender(int mouseX, int mouseY);

        public abstract void onMouseClicked(int mouseX, int mouseY);
    }

    protected interface VoidTask {
        void invoke();
    }
}
