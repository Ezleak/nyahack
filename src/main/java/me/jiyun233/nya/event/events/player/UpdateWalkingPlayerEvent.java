package me.jiyun233.nya.event.events.player;

import me.jiyun233.nya.event.events.EventStage;
import net.minecraft.client.Minecraft;

public class UpdateWalkingPlayerEvent
        extends EventStage {

    protected float yaw;
    protected float pitch;
    protected double x;
    protected double y;
    protected double z;
    protected boolean onGround;

    public UpdateWalkingPlayerEvent(int stage, double posX, double posY, double posZ, float y, float p, boolean pOnGround) {
        super(stage);
        this.x = posX;
        this.y = posY;
        this.z = posZ;
        this.yaw = y;
        this.pitch = p;
        this.onGround = pOnGround;
    }

    public void setRotation(float yaw, float pitch) {
        if (Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.rotationYawHead = yaw;
            Minecraft.getMinecraft().player.renderYawOffset = yaw;
        }
        setYaw(yaw);
        setPitch(pitch);
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = (float) yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = (float) pitch;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double posX) {
        this.x = posX;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double d) {
        this.y = d;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double posZ) {
        this.z = posZ;
    }

    public boolean getOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean b) {
        this.onGround = b;
    }
}

