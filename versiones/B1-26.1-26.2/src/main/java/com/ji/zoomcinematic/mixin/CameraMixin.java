package com.ji.zoomcinematic.mixin;

import com.ji.zoomcinematic.ZoomManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void cinematiczoom$applyZoom(float partialTicks, CallbackInfoReturnable<Float> cir) {
        ZoomManager.frameUpdate();
        Minecraft client = Minecraft.getInstance();
        if (com.ji.zoomcinematic.util.ReflectionHelper.getScreen(client) != null) return;
        double mul = ZoomManager.getCurrentFovMul();
        if (mul == 1.0) return;
        cir.setReturnValue((float) (cir.getReturnValue() * mul));
    }
}
