package me.jiyun233.nya.module.modules.combat;

import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.BooleanSetting;
import me.jiyun233.nya.utils.client.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(name = "AntiPistonCrystal", descriptions = "Auto mine piston with crystal", category = Category.COMBAT)
public class AntiPiston extends Module {

    BooleanSetting debug = registerSetting("Message", false);

    public static Block getBlock(double x, double y, double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static void breakCrystal(Entity crystal) {
        mc.playerController.attackEntity(mc.player, crystal);
        mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public void onUpdate() {
        this.blockPiston();
    }

    private void blockPiston() {
        for (Entity t : mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && t.posX >= mc.player.posX - 1.5D && t.posX <= mc.player.posX + 1.5D && t.posZ >= mc.player.posZ - 1.5D && t.posZ <= mc.player.posZ + 1.5D) {
                for (int i = -2; i < 3; ++i) {
                    for (int j = -2; j < 3; ++j) {
                        if (getBlock(t.posX + (double) i, t.posY, t.posZ + (double) j) instanceof BlockPistonBase) {
                            this.breakCrystalPiston(t);
                            if (debug.getValue()) {
                                ChatUtil.sendMessage("The piston at:" + (t.posX + (double) i + " " + t.posY + " " + t.posZ + (double) j) + " try to kill you!");
                            }
                        }
                    }
                }
            }
        }

    }

    private void breakCrystalPiston(Entity crystal) {
        breakCrystal(crystal);
    }
}
