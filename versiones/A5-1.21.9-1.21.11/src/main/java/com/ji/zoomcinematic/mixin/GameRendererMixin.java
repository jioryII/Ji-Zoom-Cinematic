package com.ji.zoomcinematic.mixin;

import com.ji.zoomcinematic.ZoomManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameRenderer.class)
public class GameRendererMixin {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true, require = 0)
    private void cinematiczoom$applyZoom(CallbackInfoReturnable cir) {
        ZoomManager.frameUpdate();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) return;
        double mul = ZoomManager.getCurrentFovMul();
        if (mul == 1.0) return;
        Object val = cir.getReturnValue();
        if (val instanceof Float) {
            cir.setReturnValue((float)((Float) val * mul));
        } else if (val instanceof Double) {
            cir.setReturnValue((Double) val * mul);
        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true, require = 0)
    private void cinematiczoom$hideHand(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (com.ji.zoomcinematic.ZoomManager.isZoomHeld()) {
            ci.cancel();
        }
    }
}
