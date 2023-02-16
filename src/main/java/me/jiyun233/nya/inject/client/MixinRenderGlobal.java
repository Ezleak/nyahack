package me.jiyun233.nya.inject.client;

import me.jiyun233.nya.event.events.world.BlockBreakEvent;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderGlobal.class)
public class MixinRenderGlobal {
    @Inject(method = "sendBlockBreakProgress", at = @At("HEAD"))
    public void onSendingBlockBreakProgressPre(int breakerId, BlockPos pos, int progress, CallbackInfo ci) {
        BlockBreakEvent event = new BlockBreakEvent(breakerId, pos, progress);
        MinecraftForge.EVENT_BUS.post(event);
    }
}

