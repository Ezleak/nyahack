package me.jiyun233.nya.inject.client;

import kotlin.random.Random;
import me.jiyun233.nya.NyaHack;
import me.jiyun233.nya.event.events.client.DisplayGuiScreenEvent;
import me.jiyun233.nya.font.UnicodeFontRenderer;
import me.jiyun233.nya.guis.MainMenu;
import me.jiyun233.nya.module.modules.client.CustomMainMenu;
import me.jiyun233.nya.utils.render.Render2DUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.Objects;

@Mixin(value = Minecraft.class, priority = 10001)
public class MixinMinecraft {
    @Redirect(method = {"run"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void onCrashReport(Minecraft minecraft, CrashReport crashReport) {
        this.saveNekoConfiguration();
    }

    @Inject(method = {"shutdown"}, at = @At(value = "HEAD"))
    public void shutdown(CallbackInfo info) {
        this.saveNekoConfiguration();
    }

    @Inject(method = "displayGuiScreen", at = @At("HEAD"), cancellable = true)
    public void displayGuiScreen(GuiScreen guiScreenIn, CallbackInfo info) {
        DisplayGuiScreenEvent.Closed closeEvent = new DisplayGuiScreenEvent.Closed(Minecraft.getMinecraft().currentScreen);
        MinecraftForge.EVENT_BUS.post(closeEvent);
        DisplayGuiScreenEvent.Displayed displayEvent = new DisplayGuiScreenEvent.Displayed(guiScreenIn);
        MinecraftForge.EVENT_BUS.post(displayEvent);
        if (closeEvent.isCanceled() || displayEvent.isCanceled()) {
            info.cancel();
        }
    }

    public void saveNekoConfiguration() {
        NyaHack.logger.warn("Saving configuration please wait...");
        Objects.requireNonNull(NyaHack.configManager).saveAll();
        NyaHack.logger.warn("Configuration saved!");
    }

    MainMenu mainMenuInstance;

    @Inject(method = "displayGuiScreen", at = @At("HEAD"), cancellable = true)
    public void displayGUIHook(GuiScreen guiScreenIn, CallbackInfo callbackInfo) {
        if ((guiScreenIn instanceof GuiMainMenu || guiScreenIn == null) && CustomMainMenu.INSTANCE.isEnabled()) {
            if (Minecraft.getMinecraft().player == null) {
                if(mainMenuInstance == null) mainMenuInstance = new MainMenu();
                Minecraft.getMinecraft().displayGuiScreen(mainMenuInstance);
                callbackInfo.cancel();
            }
        }
    }

    @Inject(method = "getLimitFramerate", at = @At("HEAD"), cancellable = true)
    public void mainMenuFpsLimitCrack(CallbackInfoReturnable<Integer> cir) {
        CustomMainMenu.fpsLimit(cir);
    }
}
