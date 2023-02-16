package me.jiyun233.nya.utils.player;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import java.util.ArrayList;

public class InventoryUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int currentItem;
    public static void push() {
        currentItem = mc.player.inventory.currentItem;
    }

    public static void pop() {
        mc.player.inventory.currentItem = currentItem;
    }

    public static void setSlot(int slot) {
        if (slot > 8 || slot < 0) return;
        mc.player.inventory.currentItem = slot;
    }

    public static int pickItem(int item, boolean allowInventory) {
        ArrayList<ItemStack> filter = new ArrayList<>();
        for (int i1 = 0; i1 < (allowInventory ? mc.player.inventory.mainInventory.size() : 9); i1++) {
            if (Item.getIdFromItem(mc.player.inventory.mainInventory.get(i1).getItem()) == item) {
                filter.add(mc.player.inventory.mainInventory.get(i1));
            }
        }
        if (!(filter.size() < 1))
            return mc.player.inventory.mainInventory.indexOf(filter.get(0));
        return -1;
    }

    public static int findHotbarItem(final Class<?> clazz) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static void switchToHotbarSlot(final int slot, final boolean silent) {
        if (mc.player == null || mc.world == null || mc.player.inventory == null) {
            return;
        }
        if (mc.player.inventory.currentItem == slot || slot < 0) {
            return;
        }
        if (silent) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.playerController.updateController();
        } else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public static int findHotbarBlock(final Class<?> clazz) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock) stack.getItem()).getBlock();
                    if (clazz.isInstance(block)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

}
