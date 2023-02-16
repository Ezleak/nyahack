package me.jiyun233.nya.module.modules.visual;

import me.jiyun233.nya.event.events.world.Render3DEvent;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.module.modules.client.ClickGui;
import me.jiyun233.nya.settings.BooleanSetting;
import me.jiyun233.nya.settings.FloatSetting;
import me.jiyun233.nya.settings.IntegerSetting;
import me.jiyun233.nya.utils.render.Render3DUtil;
import me.jiyun233.nya.event.events.world.Render3DEvent;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.BooleanSetting;
import me.jiyun233.nya.settings.FloatSetting;
import me.jiyun233.nya.settings.IntegerSetting;
import me.jiyun233.nya.utils.render.Render3DUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;

@ModuleInfo(name = "BlockHighlight", descriptions = "Render current block", category = Category.VISUAL)
public class BlockHighlight extends Module {
    private final BooleanSetting outline = registerSetting("Outline", true);
    private final BooleanSetting full = registerSetting("FullBlock", true);
    private final FloatSetting width = registerSetting("OutlineWidth", 1.5f, 0.0f, 10.0f).booleanVisible(outline);

    private final IntegerSetting alpha = registerSetting("Alpha", 55, 0, 255).booleanVisible(full);

    @Override
    public void onRender3D(Render3DEvent event) {
        if (fullNullCheck()) return;
        BlockPos blockpos;
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult ray = mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK && mc.world.getBlockState(blockpos = ray.getBlockPos()).getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
            Render3DUtil.drawBlockBox(blockpos, new Color(ClickGui.getCurrentColor().getRed(), ClickGui.getCurrentColor().getGreen(), ClickGui.getCurrentColor().getBlue(), full.getValue() ? alpha.getValue() : 0), outline.getValue(), width.getValue());
        }
    }
}

