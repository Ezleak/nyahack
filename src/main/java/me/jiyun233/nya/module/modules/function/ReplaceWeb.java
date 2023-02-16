package me.jiyun233.nya.module.modules.function;

import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.event.events.player.BlockEvent;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.BooleanSetting;
import me.jiyun233.nya.settings.DoubleSetting;
import me.jiyun233.nya.utils.player.BlockUtil;
import me.jiyun233.nya.utils.player.InventoryUtil;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;

@ModuleInfo(name = "ReplaceWeb", descriptions = "Break target under block replace web", category = Category.FUNCTION)
public class ReplaceWeb extends Module {

    public DoubleSetting range = registerSetting("Range", 5.0d, 1.0d, 10.0d);
    public BooleanSetting rotate = registerSetting("Rotate", true);
    public BooleanSetting packet = registerSetting("Packet", true);
    public BooleanSetting toggle = registerSetting("AutoToggle", true);

    private EntityPlayer target = null;

    private BlockPos currentPos = null;

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;
        int webSlot;
        if ((webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class)) == -1) {
            this.toggle();
            return;
        }
        if (target == null) {
            target = findClosestTarget(range.getValue());
            currentPos = new BlockPos(Math.floor(target.posX), Math.floor(target.posY - 1), Math.floor(target.posZ));
            if (mc.world.getBlockState(currentPos).getBlock() == Blocks.OBSIDIAN) {
                BlockEvent event = new BlockEvent(0, currentPos, BlockUtil.getRayTraceFacing(currentPos));
                MinecraftForge.EVENT_BUS.post(event);
            } else {
                rend();
            }
        } else {
            if (new BlockPos(Math.floor(target.posX), Math.floor(target.posY - 1), Math.floor(target.posZ)).equals(currentPos)) {
                if (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.AIR)) {
                    int oldSlot = mc.player.inventory.currentItem;
                    InventoryUtil.switchToHotbarSlot(webSlot, false);
                    if (InstantMinePlus.breakPos == currentPos) InstantMinePlus.breakPos = null;
                    BlockUtil.placeBlock(currentPos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue());
                    InventoryUtil.switchToHotbarSlot(oldSlot, false);
                    if (toggle.getValue()) this.toggle();
                    rend();
                }
            } else {
                rend();
                if (toggle.getValue()) this.toggle();
            }
        }
    }

    public void rend() {
        this.target = null;
        this.currentPos = null;
    }

    public EntityPlayer findClosestTarget(Double range) {
        HashMap<EntityPlayer, Double> temp = new HashMap<>();

        mc.world.loadedEntityList.forEach(entity -> {
            if (entity instanceof EntityPlayer && mc.player.getDistanceSq(entity) <= range && !NyaHack.friendManager.isFriend((EntityPlayer) entity) && entity != mc.player) {
                temp.put((EntityPlayer) entity, mc.player.getDistanceSq(entity));
            }
        });

        List<Map.Entry<EntityPlayer, Double>> list = new ArrayList<>(temp.entrySet());
        list.sort(Map.Entry.comparingByValue());

        return list.isEmpty() ? null : list.get(0).getKey();
    }
}
