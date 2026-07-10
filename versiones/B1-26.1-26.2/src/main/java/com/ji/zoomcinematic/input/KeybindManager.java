package com.ji.zoomcinematic.input;

import com.ji.zoomcinematic.ZoomManager;
import com.ji.zoomcinematic.config.ConfigManager;
import com.ji.zoomcinematic.config.ModConfig;
import net.minecraft.client.Minecraft;

public class KeybindManager {
    public static void checkKeys(Minecraft client) {
        ModConfig config = ConfigManager.getConfig();
        if (com.ji.zoomcinematic.util.ReflectionHelper.getScreen(client) != null) return;

        boolean isZoomDown = com.ji.zoomcinematic.JiZoomCinematic.ZOOM_KEY.isDown();
        ZoomManager.tick(client, isZoomDown);
    }
}
