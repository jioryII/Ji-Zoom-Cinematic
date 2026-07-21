package com.ji.zoomcinematic.mixin;

import com.ji.zoomcinematic.ZoomManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {
    // 26.1.2 Signature
    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void cinematiczoom$hideHud(GuiGraphicsExtractor context, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (com.ji.zoomcinematic.util.ClientScreenUtil.getCurrentScreen(Minecraft.getInstance()) != null) return;
        if (ZoomManager.isZoomHeld()) {
            ZoomManager.renderBars(context);
            ci.cancel();
        }
    }

    private static java.lang.reflect.Field cachedGameRenderStateField = null;

    // 26.2 Signature
    @Inject(method = "extractRenderState(Lnet/minecraft/client/DeltaTracker;ZZ)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void cinematiczoom$hideHudRender(DeltaTracker deltaTracker, boolean renderCrosshair, boolean renderChat, CallbackInfo ci) {
        if (com.ji.zoomcinematic.util.ClientScreenUtil.getCurrentScreen(Minecraft.getInstance()) != null) return;
        if (ZoomManager.isZoomHeld()) {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            try {
                if (cachedGameRenderStateField == null) {
                    for (java.lang.reflect.Field f : mc.gameRenderer.getClass().getDeclaredFields()) {
                        if (f.getType() == net.minecraft.client.renderer.state.GameRenderState.class) {
                            f.setAccessible(true);
                            cachedGameRenderStateField = f;
                            break;
                        }
                    }
                }

                if (cachedGameRenderStateField != null) {
                    net.minecraft.client.renderer.state.GameRenderState renderStateContainer =
                        (net.minecraft.client.renderer.state.GameRenderState) cachedGameRenderStateField.get(mc.gameRenderer);

                    if (renderStateContainer != null && renderStateContainer.guiRenderState != null) {
                        GuiGraphicsExtractor context = new GuiGraphicsExtractor(
                            mc,
                            renderStateContainer.guiRenderState,
                            mc.getWindow().getGuiScaledWidth(),
                            mc.getWindow().getGuiScaledHeight()
                        );
                        ZoomManager.renderBars(context);
                    }
                }
            } catch (Exception e) {
                // Ignore gracefully if it completely fails
            }
            // We cancel the vanilla HUD extraction in 26.2.
            ci.cancel();
        }
    }

    // 26.1.2 Signature (for rendering bars when HUD is hidden by F1 but zoom is held, if needed)
    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V", at = @At("TAIL"), require = 0)
    private void cinematiczoom$renderBars(GuiGraphicsExtractor context, DeltaTracker deltaTracker, CallbackInfo ci) {
        // We already render the bars in HEAD if zoom is held.
    }
}
