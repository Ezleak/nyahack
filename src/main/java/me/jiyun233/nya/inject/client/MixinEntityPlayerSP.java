package me.jiyun233.nya.inject.client;

import me.jiyun233.nya.event.events.player.JumpEvent;
import me.jiyun233.nya.event.events.player.MoveEvent;
import me.jiyun233.nya.event.events.player.MovementEvent;
import me.jiyun233.nya.event.events.player.UpdateWalkingPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {EntityPlayerSP.class}, priority = 9998)
public abstract class MixinEntityPlayerSP
        extends AbstractClientPlayer {

    @Shadow
    @Final
    public NetHandlerPlayClient connection;
    @Shadow
    private boolean serverSprintState;
    @Shadow
    private boolean serverSneakState;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    private int positionUpdateTicks;
    @Shadow
    private boolean autoJumpEnabled = true;
    @Shadow
    private boolean prevOnGround;

    @Shadow
    protected boolean isCurrentViewEntity() {
        return false;
    }

    @Shadow
    protected Minecraft mc;

    public MixinEntityPlayerSP(Minecraft p_i47378_1_, World p_i47378_2_, NetHandlerPlayClient p_i47378_3_, StatisticsManager p_i47378_4_, RecipeBook p_i47378_5_) {
        super(p_i47378_2_, p_i47378_3_.getGameProfile());
    }


    @Redirect(method = {"move"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer instance, MoverType moverType, double x, double y, double z) {
        MoveEvent event = new MoveEvent(0, moverType, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            super.move(event.getType(), event.getX(), event.getY(), event.getZ());
        }
    }

    @Override
    public void jump() {
        JumpEvent event = new JumpEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) super.jump();
    }


    /**
     * @author jiyun233 nya~
     * @reason easy rotation
     */
    @Overwrite
    public void onUpdateWalkingPlayer() {
        boolean flag = isSprinting();
        UpdateWalkingPlayerEvent pre = new UpdateWalkingPlayerEvent(0, posX, posY, posZ, rotationYaw, rotationPitch, onGround);
        MinecraftForge.EVENT_BUS.post(pre);
        if (pre.isCanceled()) {
            UpdateWalkingPlayerEvent post = new UpdateWalkingPlayerEvent(1, pre.getX(), pre.getY(), pre.getZ(), pre.getYaw(), pre.getPitch(), pre.getOnGround());
            MinecraftForge.EVENT_BUS.post(post);
            return;
        }
        if (flag != serverSprintState) {
            if (flag) {
                connection.sendPacket(new CPacketEntityAction(Minecraft.getMinecraft().player, CPacketEntityAction.Action.START_SPRINTING));
            } else {
                connection.sendPacket(new CPacketEntityAction(Minecraft.getMinecraft().player, CPacketEntityAction.Action.STOP_SPRINTING));
            }
            serverSprintState = flag;
        }

        boolean flag1 = isSneaking();

        if (flag1 != serverSneakState) {
            if (flag1) {
                connection.sendPacket(new CPacketEntityAction(Minecraft.getMinecraft().player, CPacketEntityAction.Action.START_SNEAKING));
            } else {
                connection.sendPacket(new CPacketEntityAction(Minecraft.getMinecraft().player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            serverSneakState = flag1;
        }
        if (isCurrentViewEntity()) {
            double d0 = posX - lastReportedPosX;
            double d1 = getEntityBoundingBox().minY - lastReportedPosY;
            double d2 = posZ - lastReportedPosZ;
            double d3 = pre.getYaw() - lastReportedYaw;
            double d4 = pre.getPitch() - lastReportedPitch;
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || positionUpdateTicks >= 20;
            boolean flag3 = d3 != 0.0D || d4 != 0.0D;
            if (getRidingEntity() == null) {
                if (flag2 && flag3) {
                    connection.sendPacket(new CPacketPlayer.PositionRotation(pre.getX(), pre.getY(), pre.getZ(), pre.getYaw(), pre.getPitch(), pre.getOnGround()));
                } else if (flag2) {
                    connection.sendPacket(new CPacketPlayer.Position(pre.getX(), pre.getY(), pre.getZ(), pre.getOnGround()));
                } else if (flag3) {
                    connection.sendPacket(new CPacketPlayer.Rotation(pre.getYaw(), pre.getPitch(), pre.getOnGround()));
                } else {
                    connection.sendPacket(new CPacketPlayer(pre.getOnGround()));
                }
            } else {
                connection.sendPacket(new CPacketPlayer.PositionRotation(motionX, -999.0, motionZ, pre.getYaw(), pre.getPitch(), pre.getOnGround()));
                flag2 = false;
            }

            ++positionUpdateTicks;

            if (flag2) {
                lastReportedPosX = pre.getX();
                lastReportedPosY = pre.getY();
                lastReportedPosZ = pre.getZ();
                positionUpdateTicks = 0;
            }

            if (flag3) {
                lastReportedYaw = pre.getYaw();
                lastReportedPitch = pre.getPitch();
            }
            this.prevOnGround = this.onGround;
            this.autoJumpEnabled = this.mc.gameSettings.autoJump;
            UpdateWalkingPlayerEvent post2 = new UpdateWalkingPlayerEvent(1, pre.getX(), pre.getY(), pre.getZ(), pre.getYaw(), pre.getPitch(), pre.getOnGround());
            MinecraftForge.EVENT_BUS.post(post2);
        }
    }
}
