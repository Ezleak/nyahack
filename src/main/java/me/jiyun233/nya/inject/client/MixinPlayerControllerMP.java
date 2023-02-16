package me.jiyun233.nya.inject.client;

import me.jiyun233.nya.event.events.player.BlockEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {PlayerControllerMP.class})
public class MixinPlayerControllerMP {

    @Inject(method = {"clickBlock"}, at = {@At(value = "HEAD")})
    private void clickBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
        BlockEvent event = new BlockEvent(0, pos, face);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = {"onPlayerDamageBlock"}, at = {@At(value = "HEAD")})
    private void onPlayerDamageBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
        BlockEvent event = new BlockEvent(1, pos, face);
        MinecraftForge.EVENT_BUS.post(event);
    }


}
