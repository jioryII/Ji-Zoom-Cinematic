package com.ji.zoomcinematic.config;

import org.lwjgl.glfw.GLFW;

public class ModConfig {
    public int menuKey1 = GLFW.GLFW_KEY_F7;
    public int menuKey2 = GLFW.GLFW_KEY_Z;
    public int zoomKeyCode = GLFW.GLFW_KEY_C;

    public float barsPercent = 15.0f;
    public float baseZoomLevel = 0.3333f;
    public float maxZoomLevel = 0.0666f;
    public int smoothMs = 300;

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
