package com.ji.zoomcinematic.config;

import org.lwjgl.glfw.GLFW;

public class ModConfig {
    // Tecla para hacer zoom (por defecto C)
    public int zoomKeyCode = GLFW.GLFW_KEY_C;
    
    // Combinación de 2 teclas para abrir menú (F7 + Z)
    public int menuKey1 = GLFW.GLFW_KEY_F7;
    public int menuKey2 = GLFW.GLFW_KEY_Z;
    
    // Configuraciones de visualización
    public float barsPercent = 15.0f; // 0 a 30
    public float baseZoomLevel = 0.25f; // 0.05 a 1.0 (multiplier)
    public int smoothMs = 300;
    
    // Toggles
    public boolean mouseWheelEnabled = true;
    public boolean enableCinematicCamera = true;
    
    public void copyFrom(ModConfig other) {
        this.zoomKeyCode = other.zoomKeyCode;
        this.menuKey1 = other.menuKey1;
        this.menuKey2 = other.menuKey2;
        this.barsPercent = other.barsPercent;
        this.baseZoomLevel = other.baseZoomLevel;
        this.smoothMs = other.smoothMs;
        this.mouseWheelEnabled = other.mouseWheelEnabled;
        this.enableCinematicCamera = other.enableCinematicCamera;
    }
}
