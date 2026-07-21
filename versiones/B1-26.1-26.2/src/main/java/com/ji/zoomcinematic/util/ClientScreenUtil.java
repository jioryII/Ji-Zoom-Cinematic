package com.ji.zoomcinematic.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Field;

public final class ClientScreenUtil {
    private static Field cachedScreenField = null;
    private static boolean lookupDone = false;

    private ClientScreenUtil() {}

    public static Screen getCurrentScreen(Minecraft client) {
        if (client == null) return null;
        try {
            if (!lookupDone) {
                cachedScreenField = findScreenField(client.getClass());
                if (cachedScreenField != null) {
                    cachedScreenField.setAccessible(true);
                }
                lookupDone = true;
            }
            if (cachedScreenField != null) {
                Object value = cachedScreenField.get(client);
                return value instanceof Screen ? (Screen) value : null;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static Field findScreenField(Class<?> clazz) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (Screen.class.isAssignableFrom(f.getType())) {
                    return f;
                }
            }
        }
        return null;
    }
}
