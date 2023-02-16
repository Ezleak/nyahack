package me.jiyun233.nya.inject.client;

import me.jiyun233.nya.event.events.client.AspectRatioEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Redirect(method = {"setupCameraTransform"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(final float fovy, final float aspect, final float zNear, final float zFar) {
        AspectRatioEvent event = new AspectRatioEvent(0, Minecraft.getMinecraft().displayWidth / (float) Minecraft.getMinecraft().displayHeight);
        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspectRatio(), zNear, zFar);
    }


    @Redirect(
            method = "renderWorldPass",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(final float fovy, final float aspect, final float zNear, final float zFar) {
        AspectRatioEvent event = new AspectRatioEvent(0, Minecraft.getMinecraft().displayWidth / (float) Minecraft.getMinecraft().displayHeight);
        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspectRatio(), zNear, zFar);
    }

    @Redirect(
            method = "renderCloudsCheck",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(final float fovy, final float aspect, final float zNear, final float zFar) {
        AspectRatioEvent event = new AspectRatioEvent(0, Minecraft.getMinecraft().displayWidth / (float) Minecraft.getMinecraft().displayHeight);
        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspectRatio(), zNear, zFar);
    }
}
