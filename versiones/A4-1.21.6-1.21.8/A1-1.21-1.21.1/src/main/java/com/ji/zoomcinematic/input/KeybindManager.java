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
        boolean isZoomDown = InputUtil.isKeyPressed(window, config.zoomKeyCode);
        com.ji.zoomcinematic.ZoomManager.tick(client, isZoomDown);
        
        // Menu functionality (F7 + Z combo)
        if (config.menuKey1 != -1 && config.menuKey2 != -1) {
            boolean key1Down = InputUtil.isKeyPressed(window, config.menuKey1);
            boolean key2Down = InputUtil.isKeyPressed(window, config.menuKey2);
            if (key1Down && key2Down) {
                client.setScreen(new ConfigScreen(null));
            }
        }
    }
}
