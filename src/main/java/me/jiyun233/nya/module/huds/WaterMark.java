package me.jiyun233.nya.module.huds;

import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.HudModule;
import me.jiyun233.nya.module.HudModuleInfo;
import me.jiyun233.nya.module.modules.client.ClickGui;
import me.jiyun233.nya.settings.FloatSetting;
import org.lwjgl.opengl.GL11;

@HudModuleInfo(name = "WaterMark", x = 114, y = 114, descriptions = "Show hack name", category = Category.HUD)
public class WaterMark extends HudModule {
    public FloatSetting Scala = registerSetting("Size", 1.0f, 0.0f, 3.0f);

    @Override
    public void onRender2D() {
        GL11.glPushMatrix();
        GL11.glTranslated(this.x, (float) this.y, 0);
        GL11.glScaled((double) this.Scala.getValue(), (double) this.Scala.getValue(), 0.0);
        NyaHack.fontManager.CustomFont.drawString("Nya Hack", 0, 0, ClickGui.getCurrentColor().getRGB());
        GL11.glPopMatrix();
        this.width = (int) ((float) NyaHack.fontManager.CustomFont.getWidth("Nya Hack") * this.Scala.getValue());
        this.height = (int) ((float) NyaHack.fontManager.CustomFont.getHeight() * this.Scala.getValue());
    }

}
