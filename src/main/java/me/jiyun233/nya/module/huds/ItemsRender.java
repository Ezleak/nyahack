package me.jiyun233.nya.module.huds;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.HudModule;
import me.jiyun233.nya.module.HudModuleInfo;
import me.jiyun233.nya.module.modules.client.ClickGui;
import me.jiyun233.nya.utils.render.Render2DUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@HudModuleInfo(name = "ItemsRender", descriptions = "Draw pvp item", category = Category.HUD, x = 110, y = 80)
public class ItemsRender extends HudModule {

    public static ItemsRender INSTANCE;

    public ItemsRender() {
        INSTANCE = this;
    }

    @Override
    public void onRender2D() {
        width = (16 + 3) * 3;
        height = (16 + 3) * 2;

        int obi1 = mc.player.inventory.mainInventory.stream().filter((var0) -> var0.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
            obi1 += mc.player.getHeldItemOffhand().stackSize;
        }

        ItemStack obiStack = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN), obi1);
        this.itemrender(obiStack, x, y);


        int totem1 = mc.player.inventory.mainInventory.stream().filter((var0) -> var0.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totem1 += mc.player.getHeldItemOffhand().stackSize;
        }

        ItemStack totemStack = new ItemStack(Items.TOTEM_OF_UNDYING, totem1);

        this.itemrender(totemStack, x + 16 + 3, y);

        int cry1 = mc.player.inventory.mainInventory.stream().filter((var0) -> var0.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            cry1 += mc.player.getHeldItemOffhand().stackSize;
        }

        ItemStack cryStack = new ItemStack(Items.END_CRYSTAL, cry1);

        this.itemrender(cryStack, x, y + 16 + 3);

        int gap1 = mc.player.inventory.mainInventory.stream().filter((var0) -> var0.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            gap1 += mc.player.getHeldItemOffhand().stackSize;
        }

        ItemStack gapStack = new ItemStack(Items.GOLDEN_APPLE, gap1);

        this.itemrender(gapStack, x + 16 + 3, y + 16 + 3);

        int exp1 = mc.player.inventory.mainInventory.stream().filter((var0) -> var0.getItem() == Items.EXPERIENCE_BOTTLE).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) {
            exp1 += mc.player.getHeldItemOffhand().stackSize;
        }

        ItemStack expStack = new ItemStack(Items.EXPERIENCE_BOTTLE, exp1);

        this.itemrender(expStack, x + 16 + 16 + 6, y);

        int fru1 = mc.player.inventory.mainInventory.stream().filter((var0) -> var0.getItem() == Items.CHORUS_FRUIT).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.CHORUS_FRUIT) {
            fru1 += mc.player.getHeldItemOffhand().stackSize;
        }

        ItemStack fruStack = new ItemStack(Items.CHORUS_FRUIT, fru1);

        this.itemrender(fruStack, x + (16 * 2) + 6, y + 16 + 3);

    }

    private static void preitemrender() {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale(1.0F, 1.0F, 0.01F);
    }

    private static void postitemrender() {
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GL11.glPopMatrix();
    }

    private void itemrender(ItemStack var1, float x, float y) {
        preitemrender();
        mc.getRenderItem().renderItemAndEffectIntoGUI(var1, (int) x, (int) y);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, var1, (int) x, (int) y);
        Render2DUtil.drawOutlineRect(x, y, 16 + 3, 16 + 3, 1.0f, ClickGui.getCurrentColor());
        postitemrender();
    }

}
