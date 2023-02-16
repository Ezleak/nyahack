package me.jiyun233.nya.module.modules.combat;

import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.module.modules.function.InstantMinePlus;
import me.jiyun233.nya.settings.BooleanSetting;
import me.jiyun233.nya.settings.DoubleSetting;
import me.jiyun233.nya.utils.player.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@ModuleInfo(name = "AntiBurrow", descriptions = "Auto mine burrowed player", category = Category.COMBAT)
public class AntiBurrow extends Module {
    public static BlockPos pos;
    private final DoubleSetting range = registerSetting("Range", 5.0D, 1.0D, 8.0D);
    private final BooleanSetting toggle = registerSetting("Toggle", false);

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0D) + 1.0D;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.getDistance(mc.player) > range || NyaHack.friendManager.isFriend(player) || player.isDead)
                continue;
            if (target == null) {
                target = player;
                distance = mc.player.getDistanceSq(player);
                continue;
            }
            if (mc.player.getDistanceSq(player) >= distance)
                continue;
            target = player;
            distance = mc.player.getDistanceSq(player);
        }
        return target;
    }

    public void onUpdate() {
        if (fullNullCheck())
            return;
        if (mc.currentScreen instanceof net.minecraft.client.gui.GuiHopper)
            return;
        EntityPlayer player = getTarget(this.range.getValue());
        if (this.toggle.getValue())
            toggle();
        if (player == null)
            return;
        pos = new BlockPos(player.posX, player.posY + 0.5, player.posZ);
        if (InstantMinePlus.breakPos != null) {
            if (InstantMinePlus.breakPos.equals(pos))
                return;
            if (InstantMinePlus.breakPos.equals(new BlockPos(mc.player.posX, mc.player.posY + 2, mc.player.posZ)))
                return;
            if (mc.player.rotationPitch <= 90 && mc.player.rotationPitch >= 80)
                return;
            if (mc.world.getBlockState(InstantMinePlus.breakPos).getBlock() == Blocks.WEB)
                return;
        }
        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR && mc.world.getBlockState(pos).getBlock() != Blocks.WEB && mc.world.getBlockState(pos).getBlock() != Blocks.WOODEN_BUTTON && mc.world.getBlockState(pos).getBlock() != Blocks.STONE_BUTTON && !isOnLiquid() && !isInLiquid() && mc.world.getBlockState(pos).getBlock() != Blocks.WATER && mc.world.getBlockState(pos).getBlock() != Blocks.LAVA) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.playerController.onPlayerDamageBlock(pos, BlockUtil.getRayTraceFacing(pos));
        }
    }

    private boolean isOnLiquid() {
        double y = mc.player.posY - 0.03D;
        for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); x++) {
            for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); z++) {
                BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid)
                    return true;
            }
        }
        return false;
    }

    private boolean isInLiquid() {
        double y = mc.player.posY + 0.01D;
        for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); x++) {
            for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); z++) {
                BlockPos pos = new BlockPos(x, (int) y, z);
                if (mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid)
                    return true;
            }
        }
        return false;
    }
}
