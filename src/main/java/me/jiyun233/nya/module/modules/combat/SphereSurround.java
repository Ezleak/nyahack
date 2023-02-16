package me.jiyun233.nya.module.modules.combat;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.BooleanSetting;
import me.jiyun233.nya.settings.DoubleSetting;
import me.jiyun233.nya.utils.player.BlockUtil;
import me.jiyun233.nya.utils.player.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "SphereSurround",descriptions = "Auto Place a ball",category = Category.COMBAT)
public class SphereSurround extends Module {
    public DoubleSetting range = registerSetting("Range", 5.0d, 1.0d, 10.0d);
    public BooleanSetting rotate = registerSetting("Rotate", true);
    public BooleanSetting packet = registerSetting("Packet", true);

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;
        for (BlockPos pos : getSphere(mc.player.getPositionVector(), range.getValue() / 2, range.getValue() / 2, false, true)) {
            BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            if (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && pos != playerPos && pos != playerPos.add(0, 1, 0)) {
                int obi = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                int old = mc.player.inventory.currentItem;
                if (obi != -1) {
                    InventoryUtil.switchToHotbarSlot(obi, false);
                    BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue());
                    InventoryUtil.switchToHotbarSlot(old, false);
                }
            }
        }
    }

    public static List<BlockPos> getSphere(Vec3d loc, double r, double h, boolean hollow, boolean sphere) {
        List<BlockPos> circleBlocks = new ArrayList<>();
        int cx = (int) loc.x;
        int cy = (int) loc.y;
        int cz = (int) loc.z;
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }
}
