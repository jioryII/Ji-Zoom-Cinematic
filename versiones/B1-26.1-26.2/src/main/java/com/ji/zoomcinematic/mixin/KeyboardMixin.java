package com.ji.zoomcinematic.mixin;

import com.ji.zoomcinematic.JiZoomCinematic;
import com.ji.zoomcinematic.config.ConfigManager;
import com.ji.zoomcinematic.config.ModConfig;
import com.ji.zoomcinematic.config.ConfigScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true, require = 0)
    private void cinematiczoom$onKey(long window, int action, KeyEvent keyEvent, CallbackInfo ci) {
        ModConfig config = ConfigManager.getConfig();
        Minecraft client = Minecraft.getInstance();
        int code = keyEvent.key();

        int zoomCode = config.zoomKeyCode;
        if (zoomCode > 0 && code == zoomCode && com.ji.zoomcinematic.util.ReflectionHelper.getScreen(client) == null) {
            JiZoomCinematic.ZOOM_KEY.setDown(action != GLFW.GLFW_RELEASE);
        }

        if (action == GLFW.GLFW_PRESS) {
            if (config.menuKey1 != -1 && config.menuKey2 != -1) {
                if (code == config.menuKey2
                        && InputConstants.isKeyDown(client.getWindow(), config.menuKey1)) {
                    if (com.ji.zoomcinematic.util.ReflectionHelper.getScreen(client) == null) {
                        com.ji.zoomcinematic.util.ReflectionHelper.setScreen(client, new ConfigScreen(null));
                        ci.cancel();
                    }
                }
            }
        }
    }
}
