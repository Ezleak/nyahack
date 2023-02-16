package me.jiyun233.nya.utils.combat;

import me.jiyun233.nya.utils.player.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class CrystalUtil {

    static Minecraft mc = Minecraft.getMinecraft();

    public static List<BlockPos> getSphere(Vec3d loc, double r, double h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = (int) loc.x;
        int cy = (int) loc.y;
        int cz = (int) loc.z;
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }


    public static List<BlockPos> getSphere(BlockPos loc, double r, double h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int)r; x <= cx + r; x++) {
            for (int z = cz - (int)r; z <= cz + r; ) {
                int y = sphere ? (cy - (int)r) : cy;
                for (;; z++) {
                    if (y < (sphere ? (cy + r) : (cy + h))) {
                        double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
                        if (dist < r * r && (!hollow || dist >= (r - 1.0D) * (r - 1.0D))) {
                            BlockPos l = new BlockPos(x, y + plus_y, z);
                            circleblocks.add(l);
                        }
                        y++;
                        continue;
                    }
                }
            }
        }
        return circleblocks;
    }

    public static Vec3d getPlayerPos(EntityPlayer player) {
        return new Vec3d(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception ignored) {
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (float) (int) ((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception ignored) {
            }
            float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }

    protected static double getDirection2D(double dx, double dy) {
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

    protected static Vec3d getVectorForRotation(double pitch, double yaw) {
        float f = MathHelper.cos((float) (-yaw * 0.017453292F - (float) Math.PI));
        float f1 = MathHelper.sin((float) (-yaw * 0.017453292F - (float) Math.PI));
        float f2 = -MathHelper.cos((float) (-pitch * 0.017453292F));
        float f3 = MathHelper.sin((float) (-pitch * 0.017453292F));
        return new Vec3d(f1 * f2, f3, f * f2);
    }

    public static void placeCrystal(BlockPos pos) {
        pos.offset(EnumFacing.DOWN);
        double dx = (pos.getX() + 0.5 - mc.player.posX);
        double dy = (pos.getY() + 0.5 - mc.player.posY) - .5 - mc.player.getEyeHeight();
        double dz = (pos.getZ() + 0.5 - mc.player.posZ);

        double x = getDirection2D(dz, dx);
        double y = getDirection2D(dy, Math.sqrt(dx * dx + dz * dz));

        Vec3d vec = getVectorForRotation(-y, x - 90);
        if (mc.player.inventory.offHandInventory.get(0).getItem().getClass().equals(Item.getItemById(426).getClass())) {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.OFF_HAND);
        } else if (InventoryUtil.pickItem(426, false) != -1) {
            InventoryUtil.setSlot(InventoryUtil.pickItem(426, false));
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.MAIN_HAND);
        }
    }

    public static double getDamage(Vec3d pos, Entity target) {
        Entity entity = target == null ? mc.player : target;
        float damage = 6.0F;
        float f3 = damage * 2.0F;

        if (!entity.isImmuneToExplosions()) {
            double d12 = entity.getDistance(pos.x, pos.y, pos.z) / (double) f3;

            if (d12 <= 1.0D) {
                double d5 = entity.posX - pos.x;
                double d7 = entity.posY + (double) entity.getEyeHeight() - pos.y;
                double d9 = entity.posZ - pos.z;
                double d13 = MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

                if (d13 != 0.0D) {
                    double d14 = mc.world.getBlockDensity(pos, entity.getEntityBoundingBox());
                    double d10 = (1.0D - d12) * d14;
                    return (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f3 + 1.0D));
                }
            }
        }
        return 0;
    }

}
