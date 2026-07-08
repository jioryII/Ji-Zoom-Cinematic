package com.ji.zoomcinematic.mixin;

import com.ji.zoomcinematic.config.ConfigManager;
import com.ji.zoomcinematic.config.ModConfig;
import com.ji.zoomcinematic.config.ConfigScreen;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true, require = 0)
    private void cinematiczoom$onKey(long window, int action, KeyInput keyInput, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS) {
            ModConfig config = ConfigManager.getConfig();
            if (config.menuKey1 != -1 && config.menuKey2 != -1) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (keyInput.key() == config.menuKey2
                        && InputUtil.isKeyPressed(client.getWindow(), config.menuKey1)) {
                    if (client.currentScreen == null) {
                        client.setScreen(new ConfigScreen(null));
                        ci.cancel();
                    }
                }
            }
        }
    }
}
