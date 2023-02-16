package me.jiyun233.nya.utils.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BlockPlacement {

    protected static Minecraft mc = Minecraft.getMinecraft();

    public BlockPos pos;
    public int a;
    public EnumFacing f;
    public double dist;
    public double rots, rota;

    public static boolean doPlace(BlockPlacement event, boolean swing) {
        if (event == null)
            return false;

        return event.doPlace(swing);
    }

    public static BlockPlacement isPlaceable(BlockPos pos, double dist, boolean Collide) {
        BlockPlacement event = new BlockPlacement(pos, 0, null, dist);

        if (isAir(pos))
            return null;

        AxisAlignedBB axisalignedbb = Block.FULL_BLOCK_AABB;

        if (isAir(pos)) {
            if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                Block block = mc.world.getBlockState(pos.offset(EnumFacing.UP)).getBlock();
                if (block instanceof BlockLiquid) {
                    event.f = EnumFacing.DOWN;
                    event.pos.offset(EnumFacing.UP);
                } else {
                    event.f = EnumFacing.UP;
                    event.pos.offset(EnumFacing.DOWN);
                }
                return event;
            }
        }

        for (EnumFacing f : EnumFacing.values()) {
            if (isAir(new BlockPos(pos.getX() - f.getDirectionVec().getX(), pos.getY() - f.getDirectionVec().getY(), pos.getZ() - f.getDirectionVec().getZ()))) {
                event.f = f;


                if (Collide && axisalignedbb != Block.NULL_AABB && !mc.world.checkNoEntityCollision(axisalignedbb.offset(pos), null)) {
                    return null;
                }

                return event;
            }
        }
        if (isRePlaceable(pos)) {
            event.f = EnumFacing.UP;
            event.pos.offset(EnumFacing.UP);
            pos.offset(EnumFacing.DOWN);

            if (Collide && axisalignedbb != Block.NULL_AABB && !mc.world.checkNoEntityCollision(axisalignedbb.offset(pos), null)) {
                return null;
            }

            return event;
        }
        return null;
    }

    public static boolean isAir(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return !(block instanceof BlockAir);
    }

    public static boolean isRePlaceable(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return block.isReplaceable(mc.world, pos) && !(block instanceof BlockAir);
    }

    public BlockPlacement(BlockPos pos, int a, EnumFacing f, double dist) {
        this.pos = pos;
        this.a = a;
        this.f = f;
        this.dist = dist;
    }

    public boolean doPlace(boolean swing) {
        double dx = ((pos.getX() + 0.5 - mc.player.posX) - ((double) f.getDirectionVec().getX()) / 2);
        double dy = ((pos.getY() + 0.5 - mc.player.posY) - ((double) f.getDirectionVec().getY()) / 2) - mc.player.getEyeHeight();
        double dz = ((pos.getZ() + 0.5 - mc.player.posZ) - ((double) f.getDirectionVec().getZ()) / 2);

        double x = getDirection2D(dz, dx);
        double y = getDirection2D(dy, Math.sqrt(dx * dx + dz * dz));

        Vec3d vec = getVectorForRotation(-y, x - 90);

        this.rota = -y;
        this.rots = x - 90;

        EnumActionResult enumactionresult = mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(f, -1), f, vec, EnumHand.MAIN_HAND);
        if (enumactionresult == EnumActionResult.SUCCESS) {
            if (swing)
                mc.player.swingArm(EnumHand.MAIN_HAND);

            return true;
        }
        return false;
    }

    protected final Vec3d getVectorForRotation(double pitch, double yaw) {
        float f = MathHelper.cos((float) (-yaw * 0.017453292F - (float) Math.PI));
        float f1 = MathHelper.sin((float) (-yaw * 0.017453292F - (float) Math.PI));
        float f2 = -MathHelper.cos((float) (-pitch * 0.017453292F));
        float f3 = MathHelper.sin((float) (-pitch * 0.017453292F));
        return new Vec3d(f1 * f2, f3, f * f2);
    }

    public static double getDirection2D(double dx, double dy) {
        double d;
        if (dy == 0) {
            if (dx > 0) {
                d = 90;
            } else {
                d = -90;
            }
        } else {
            d = Math.atan(dx / dy) * 57.2957796;
            if (dy < 0) {
                if (dx > 0) {
                    d += 180;
                } else {
                    if (dx < 0) {
                        d -= 180;
                    } else {
                        d = 180;
                    }
                }
            }
        }
        return d;
    }
}