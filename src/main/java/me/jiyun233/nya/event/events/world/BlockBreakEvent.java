package me.jiyun233.nya.event.events.world;

import me.jiyun233.nya.event.events.EventStage;
import net.minecraft.util.math.BlockPos;

public class BlockBreakEvent extends EventStage {

    public int breakerId;

    public BlockPos position;

    public int progress;

    public BlockBreakEvent(int breakerId, BlockPos position, int progress) {
        this.breakerId = breakerId;
        this.position = position;
        this.progress = progress;
    }
}
