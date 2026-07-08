package com.ji.zoomcinematic;

import com.ji.zoomcinematic.config.ConfigManager;
import com.ji.zoomcinematic.input.KeybindManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class JiZoomCinematic implements ClientModInitializer {
    public static final String MOD_ID = "ji_zoom_cinematic";

    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeybindManager.checkKeys(client);
        });
    }
}
