package me.jiyun233.nya.event.events.player;

import me.jiyun233.nya.event.events.EventStage;
import net.minecraft.entity.MoverType;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class MovementEvent extends EventStage {
    public MoverType type;
    public double X;
    public double Y;
    public double Z;
    
    public MovementEvent(final int stage, final MoverType type, final double x, final double y, final double z) {
        super(stage);
        this.type = type;
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
    
    public MoverType getType() {
        return this.type;
    }
    
    public void setType(final MoverType type) {
        this.type = type;
    }
    
    public double getX() {
        return this.X;
    }
    
    public double getY() {
        return this.Y;
    }
    
    public double getZ() {
        return this.Z;
    }
    
    public void setX(final double x) {
        this.X = x;
    }
    
    public void setY(final double y) {
        this.Y = y;
    }
    
    public void setZ(final double z) {
        this.Z = z;
    }
}
