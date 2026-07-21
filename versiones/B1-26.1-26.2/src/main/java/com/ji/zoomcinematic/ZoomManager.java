package com.ji.zoomcinematic;

import com.ji.zoomcinematic.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.Options;

public class ZoomManager {
    private static boolean zoomHeld = false;
    private static float currentZoomMul = 1.0f;
    private static float targetZoomMul = 1.0f;
    private static float holdZoomMul = 1.0f;
    private static float currentBarsPct = 0.0f;
    private static float targetBarsPct = 0.0f;
    private static long lastNs = 0L;
    private static Boolean prevSmoothCamera = null;

    public static void tick(Minecraft client, boolean isKeyDown) {
        boolean inScreen = com.ji.zoomcinematic.util.ClientScreenUtil.getCurrentScreen(client) != null;
        boolean wantZoom = isKeyDown && !inScreen;
        boolean starting = wantZoom && !zoomHeld;
        boolean ending = !wantZoom && zoomHeld;

        ModConfig config = com.ji.zoomcinematic.config.ConfigManager.getConfig();

        if (starting) {
            holdZoomMul = clamp(config.baseZoomLevel, 0.05f, 1.0f);
            if (config.enableCinematicCamera) {
                prevSmoothCamera = com.ji.zoomcinematic.util.ReflectionHelper.getSmoothCamera(client.options);
                com.ji.zoomcinematic.util.ReflectionHelper.setSmoothCamera(client.options, true);
            }
        }

        if (ending) {
            if (prevSmoothCamera != null) {
                com.ji.zoomcinematic.util.ReflectionHelper.setSmoothCamera(client.options, prevSmoothCamera);
                prevSmoothCamera = null;
            }
        }

        zoomHeld = wantZoom;
        targetZoomMul = zoomHeld ? holdZoomMul : 1.0f;
        targetBarsPct = (zoomHeld && config.bordersEnabled) ? (float) (config.barsPercent / 100.0) : 0.0f;
    }

    public static void frameUpdate() {
        ModConfig config = com.ji.zoomcinematic.config.ConfigManager.getConfig();
        int smooth = config.smoothMs;
        long now = System.nanoTime();

        if (lastNs == 0L) {
            lastNs = now;
            return;
        }

        double dtMs = (double)(now - lastNs) / 1000000.0;
        lastNs = now;

        if (dtMs > 50.0) {
            dtMs = 50.0;
        }

        if (smooth <= 0) {
            currentZoomMul = targetZoomMul;
            currentBarsPct = targetBarsPct;
            return;
        }

        double tau = (double)smooth / 2.302585092994046;
        double alpha = 1.0 - Math.exp(-dtMs / tau);

        currentZoomMul = (float)lerp(currentZoomMul, targetZoomMul, alpha);
        currentBarsPct = (float)lerp(currentBarsPct, targetBarsPct, alpha);

        if (Math.abs(currentZoomMul - targetZoomMul) < 1.0E-4f) {
            currentZoomMul = targetZoomMul;
        }
        if (Math.abs(currentBarsPct - targetBarsPct) < 0.001f) {
            currentBarsPct = targetBarsPct;
        }
    }

    public static boolean isZoomHeld() {
        return zoomHeld;
    }

    public static double getCurrentFovMul() {
        return currentZoomMul;
    }

    public static boolean onWheel(double vertical) {
        ModConfig config = com.ji.zoomcinematic.config.ConfigManager.getConfig();
        if (!zoomHeld) {
            return false;
        }
        if (!config.mouseWheelEnabled) {
            return true;
        }
        if (vertical == 0.0) {
            return true;
        }
        float step = 0.05f;
        if (vertical > 0.0) {
            holdZoomMul -= step;
        } else {
            holdZoomMul += step;
        }
        holdZoomMul = clamp(holdZoomMul, config.maxZoomLevel, 1.0f);
        return true;
    }

    public static void renderBars(GuiGraphicsExtractor ctx) {
        ModConfig config = com.ji.zoomcinematic.config.ConfigManager.getConfig();
        if (!config.bordersEnabled || currentBarsPct <= 1.0E-4f) {
            return;
        }
        int sw = ctx.guiWidth();
        int sh = ctx.guiHeight();
        int h = Math.round((float)sh * currentBarsPct);
        if (h <= 0) {
            return;
        }
        int color = 0xFF000000;
        ctx.fill(0, 0, sw, h, color);
        ctx.fill(0, sh - h, sw, sh, color);
    }

    private static double lerp(double a, double b, double t) {
        if (t <= 0.0) return a;
        if (t >= 1.0) return b;
        return a + (b - a) * t;
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
