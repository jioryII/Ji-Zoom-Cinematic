package com.ji.zoomcinematic.input;

import com.ji.zoomcinematic.config.ConfigManager;
import net.minecraft.client.MinecraftClient;

public class KeybindManager {
    public static void checkKeys(MinecraftClient client) {
        if (ConfigManager.getConfig() == null) return;

        boolean isZoomDown = (client.currentScreen == null)
            && com.ji.zoomcinematic.JiZoomCinematic.ZOOM_KEY.isPressed();
        com.ji.zoomcinematic.ZoomManager.tick(client, isZoomDown);
    }
}

