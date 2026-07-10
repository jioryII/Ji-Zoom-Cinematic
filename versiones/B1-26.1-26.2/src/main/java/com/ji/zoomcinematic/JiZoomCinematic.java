package com.ji.zoomcinematic;

import com.ji.zoomcinematic.config.ConfigManager;
import com.ji.zoomcinematic.input.KeybindManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class JiZoomCinematic implements ClientModInitializer {
    public static final String MOD_ID = "ji_zoom_cinematic";

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath(MOD_ID, "keys")
    );

    public static final KeyMapping ZOOM_KEY = new KeyMapping(
        "key.ji_zoom_cinematic.zoom",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_C,
        CATEGORY
    );

    @Override
    public void onInitializeClient() {
        KeyMapping dummy = ZOOM_KEY;
        ConfigManager.loadConfig();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeybindManager.checkKeys(client);
        });
    }

    public static void syncZoomKeyFromConfig() {
        int code = ConfigManager.getConfig().zoomKeyCode;
        if (code <= 0) {
            ZOOM_KEY.setKey(InputConstants.UNKNOWN);
        } else {
            ZOOM_KEY.setKey(InputConstants.Type.KEYSYM.getOrCreate(code));
        }
    }
}
