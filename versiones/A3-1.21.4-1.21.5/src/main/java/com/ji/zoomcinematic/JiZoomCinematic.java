package com.ji.zoomcinematic;

import com.ji.zoomcinematic.config.ConfigManager;
import com.ji.zoomcinematic.input.KeybindManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class JiZoomCinematic implements ClientModInitializer {
    public static final String MOD_ID = "ji_zoom_cinematic";
    
    public static final KeyBinding ZOOM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.ji_zoom_cinematic.zoom",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_C,
        "category.ji_zoom_cinematic"
    ));

    @Override
    public void onInitializeClient() {
        KeyBinding dummy = ZOOM_KEY; // Wake up static initializer
        ConfigManager.loadConfig();
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeybindManager.checkKeys(client);
        });
    }
}
