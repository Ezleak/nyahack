package me.jiyun233.nya.module.modules.movement;

import me.jiyun233.nya.event.events.player.JumpEvent;
import me.jiyun233.nya.event.events.player.MoveEvent;
import me.jiyun233.nya.event.events.player.UpdateWalkingPlayerEvent;
import me.jiyun233.nya.module.Category;
import me.jiyun233.nya.module.Module;
import me.jiyun233.nya.module.ModuleInfo;
import me.jiyun233.nya.settings.ModeSetting;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@ModuleInfo(name = "Strafe", category = Category.MOVEMENT, descriptions = "Strafe movement on sky")
public class Strafe extends Module {
    private final ModeSetting<mode> Mode = registerSetting("Mode", mode.STRICT);
    public boolean antiShake;
    int stage;
    private double lastDist;
    private double moveSpeed;

    public Strafe() {
        this.stage = 1;
        this.antiShake = false;
    }

    @SubscribeEvent
    public void Pre(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            this.lastDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
        }
    }

    @SubscribeEvent
    public void move(MoveEvent event) {
        final double motionY = 0;
        double n2;
        double n3;
        double n4;
        double n5;
        if (!mc.player.isInWater() && !mc.player.isInLava()) {
            if (mc.player.onGround) {
                this.stage = 2;
            }
            switch (this.stage) {
                case 0: {
                    ++this.stage;
                    this.lastDist = 0.0;
                    break;
                }
                case 2: {
                    if (mc.player.onGround && mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                            event.setY(mc.player.motionY = motionY);
                            this.moveSpeed *= (this.Mode.getValue().equals(mode.NORMAL) ? 1.67 : 2.149);
                        }
                    }
                    break;
                }
                case 3: {
                    this.moveSpeed = this.lastDist - (this.Mode.getValue().equals(mode.NORMAL) ? 0.6896 : 0.795) * (this.lastDist - this.getBaseMoveSpeed());
                    break;
                }
                default: {
                    if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && this.stage > 0) {
                        this.stage = ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) ? 1 : 0);
                    }
                    this.moveSpeed = this.lastDist - this.lastDist / (this.Mode.getValue().equals(mode.NORMAL) ? 730.0 : 159.0);
                    break;
                }
            }
            if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.player.onGround) {
                this.moveSpeed = this.getBaseMoveSpeed();
            } else {
                this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
            }
            n2 = mc.player.movementInput.moveForward;
            n3 = mc.player.movementInput.moveStrafe;
            n4 = mc.player.rotationYaw;
            if (n2 == 0.0 && n3 == 0.0) {
                event.setX(0.0);
                event.setZ(0.0);
            } else if (n2 != 0.0 && n3 != 0.0) {
                n2 *= Math.sin(0.7853981633974483);
                n3 *= Math.cos(0.7853981633974483);
            }
            n5 = (this.Mode.getValue().equals(mode.NORMAL) ? 0.993 : 0.99);
            event.setX((n2 * this.moveSpeed * -Math.sin(Math.toRadians(n4)) + n3 * this.moveSpeed * Math.cos(Math.toRadians(n4))) * n5);
            event.setZ((n2 * this.moveSpeed * Math.cos(Math.toRadians(n4)) - n3 * this.moveSpeed * -Math.sin(Math.toRadians(n4))) * n5);
            ++this.stage;
        }
    }

    public void onDisable() {
        this.antiShake = false;
    }

    @Override
    public String getHudInfo() {
        return this.Mode.getValue().equals(mode.NORMAL) ? "Normal" : "Strict";
    }

    public double getBaseMoveSpeed() {
        double n = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            n *= 1.0 + 0.2 * (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1);
        }
        return n;
    }

    public enum mode {
        STRICT,
        NORMAL
    }
}
