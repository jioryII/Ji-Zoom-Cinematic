package com.ji.zoomcinematic.config;

import org.lwjgl.glfw.GLFW;

public class ModConfig {
    // Tecla para hacer zoom (por defecto C)
        
    // CombinaciÃ³n de 2 teclas para abrir menÃº (F7 + Z)
    public int menuKey1 = GLFW.GLFW_KEY_F7;
    public int menuKey2 = GLFW.GLFW_KEY_Z;
    public int zoomKeyCode = GLFW.GLFW_KEY_C;
    
    // Configuraciones de visualizaciÃ³n
    public float barsPercent = 15.0f; // 0 a 30
    public float baseZoomLevel = 0.3333f; // 0.01 a 1.0 (multiplier)
    public float maxZoomLevel = 0.0666f;
    public int smoothMs = 300;
    
    // Toggles
    public boolean mouseWheelEnabled = true;
    public boolean enableCinematicCamera = true;
    public boolean bordersEnabled = true;
    
    public void copyFrom(ModConfig other) {
                this.menuKey1 = other.menuKey1;
        this.menuKey2 = other.menuKey2;
        this.zoomKeyCode = other.zoomKeyCode;
        this.barsPercent = other.barsPercent;
        this.baseZoomLevel = other.baseZoomLevel;
        this.maxZoomLevel = other.maxZoomLevel;
        this.smoothMs = other.smoothMs;
        this.mouseWheelEnabled = other.mouseWheelEnabled;
        this.enableCinematicCamera = other.enableCinematicCamera;
        this.bordersEnabled = other.bordersEnabled;
    }
}



