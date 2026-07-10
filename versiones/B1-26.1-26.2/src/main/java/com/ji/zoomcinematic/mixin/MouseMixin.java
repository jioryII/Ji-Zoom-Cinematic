package com.ji.zoomcinematic.mixin;

import com.ji.zoomcinematic.ZoomManager;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MouseHandler.class)
public class MouseMixin {
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true, require = 0)
    private void cinematiczoom$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ZoomManager.onWheel(vertical)) {
            ci.cancel();
        }
    }
}
