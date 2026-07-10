package com.ji.zoomcinematic.mixin;

import com.ji.zoomcinematic.ZoomManager;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true, require = 0)
    private void cinematiczoom$hideHand(CallbackInfo ci) {
        if (ZoomManager.isZoomHeld()) {
            ci.cancel();
        }
    }
}
