package com.ji.zoomcinematic.input;

import com.ji.zoomcinematic.config.ConfigManager;
import com.ji.zoomcinematic.config.ModConfig;
import com.ji.zoomcinematic.config.ConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {
    public static void checkKeys(MinecraftClient client) {
        ModConfig config = ConfigManager.getConfig();
        if (client.currentScreen != null) return;
        
        long window = client.getWindow().getHandle();
        
        // Zoom functionality
        boolean isZoomDown = com.ji.zoomcinematic.JiZoomCinematic.ZOOM_KEY.isPressed();
        com.ji.zoomcinematic.ZoomManager.tick(client, isZoomDown);
        

    }
}
