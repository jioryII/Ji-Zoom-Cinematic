package com.ji.zoomcinematic.input;

import com.ji.zoomcinematic.ZoomManager;
import com.ji.zoomcinematic.config.ConfigManager;
import com.ji.zoomcinematic.config.ModConfig;
import net.minecraft.client.Minecraft;

public class KeybindManager {
    public static void checkKeys(Minecraft client) {
        if (ConfigManager.getConfig() == null) return;

        boolean isZoomDown = (com.ji.zoomcinematic.util.ClientScreenUtil.getCurrentScreen(client) == null)
            && com.ji.zoomcinematic.JiZoomCinematic.ZOOM_KEY.isDown();
        ZoomManager.tick(client, isZoomDown);
    }
}
