package com.ji.zoomcinematic.util;

import net.minecraft.client.option.GameOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionHelper {
    
    public static Boolean getSmoothCamera(GameOptions options) {
        try {
            for (Field f : GameOptions.class.getDeclaredFields()) {
                if (f.getType() == boolean.class) {
                    f.setAccessible(true);
                    // This is risky because there are many booleans. 
                    // Better to find by name "smoothCameraEnabled" or "smoothCamera"
                }
            }
            
            // Try to find smoothCamera (SimpleOption)
            Field smoothCamField = null;
            try {
                smoothCamField = GameOptions.class.getField("smoothCameraEnabled"); // Some mappings
            } catch (NoSuchFieldException e) {
                try {
                    smoothCamField = GameOptions.class.getField("smoothCamera"); // Other mappings
                } catch (NoSuchFieldException e2) {
                    try {
                        smoothCamField = GameOptions.class.getField("field_1914"); // Mojmap/Intermediary
                    } catch (NoSuchFieldException e3) {}
                }
            }
            
            if (smoothCamField != null) {
                Object val = smoothCamField.get(options);
                if (val instanceof Boolean) {
                    return (Boolean) val;
                } else if (val != null) {
                    // It's a SimpleOption
                    Method getValue = val.getClass().getMethod("getValue");
                    return (Boolean) getValue.invoke(val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void setSmoothCamera(GameOptions options, boolean value) {
        try {
            Field smoothCamField = null;
            try {
                smoothCamField = GameOptions.class.getField("smoothCameraEnabled");
            } catch (NoSuchFieldException e) {
                try {
                    smoothCamField = GameOptions.class.getField("smoothCamera");
                } catch (NoSuchFieldException e2) {
                    try {
                        smoothCamField = GameOptions.class.getField("field_1914");
                    } catch (NoSuchFieldException e3) {}
                }
            }
            
            if (smoothCamField != null) {
                Object val = smoothCamField.get(options);
                if (val instanceof Boolean) {
                    smoothCamField.set(options, value);
                } else if (val != null) {
                    // It's a SimpleOption
                    Method setValue = val.getClass().getMethod("setValue", Object.class);
                    setValue.invoke(val, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
