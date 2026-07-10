package com.ji.zoomcinematic.util;

import net.minecraft.client.Options;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionHelper {

    public static Object getScreen(net.minecraft.client.Minecraft client) {
        try {
            Field screenField = findField(client, "screen", "currentScreen", "field_1755", "activeScreen", "displayedScreen");
            if (screenField != null) {
                return screenField.get(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setScreen(net.minecraft.client.Minecraft client, net.minecraft.client.gui.screens.Screen screen) {
        try {
            Method setScreenMethod = null;
            for (Method m : client.getClass().getMethods()) {
                if (m.getParameterCount() == 1 && m.getParameterTypes()[0].isAssignableFrom(net.minecraft.client.gui.screens.Screen.class)) {
                    if (m.getName().equals("setScreen") || m.getName().equals("setScreenAndShow") || m.getName().equals("displayGuiScreen")) {
                        setScreenMethod = m;
                        break;
                    }
                }
            }
            if (setScreenMethod != null) {
                setScreenMethod.invoke(client, screen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean getSmoothCamera(Options options) {
        try {
            Field smoothCamField = findField(options, "smoothCameraEnabled", "smoothCamera", "field_1914");
            if (smoothCamField != null) {
                Object val = smoothCamField.get(options);
                if (val instanceof Boolean) {
                    return (Boolean) val;
                } else if (val != null) {
                    Method getValue = val.getClass().getMethod("getValue");
                    return (Boolean) getValue.invoke(val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setSmoothCamera(Options options, boolean value) {
        try {
            Field smoothCamField = findField(options, "smoothCameraEnabled", "smoothCamera", "field_1914");
            if (smoothCamField != null) {
                Object val = smoothCamField.get(options);
                if (val instanceof Boolean) {
                    smoothCamField.set(options, value);
                } else if (val != null) {
                    Method setValue = val.getClass().getMethod("setValue", Object.class);
                    setValue.invoke(val, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Field findField(Object options, String... names) {
        for (String name : names) {
            try {
                Field f = options.getClass().getField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) {
            }
        }
        try {
            for (Field f : options.getClass().getDeclaredFields()) {
                if (f.getType().getName().contains("OptionInstance") || f.getType().getName().contains("SimpleOption")) {
                    f.setAccessible(true);
                    Object val = f.get(options);
                    if (val != null) {
                        try {
                            Method getValue = val.getClass().getMethod("getValue");
                            Object current = getValue.invoke(val);
                            if (current instanceof Boolean) {
                                return f;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
