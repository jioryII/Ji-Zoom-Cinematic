package com.ji.zoomcinematic.mixin;

import com.ji.zoomcinematic.ZoomManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cinematiczoom$hideHud(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter, CallbackInfo ci) {
        if (ZoomManager.isZoomHeld()) {
            ZoomManager.renderBars(context);
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void cinematiczoom$renderBars(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ZoomManager.isZoomHeld()) {
            ZoomManager.renderBars(context);
        }
    }
}
