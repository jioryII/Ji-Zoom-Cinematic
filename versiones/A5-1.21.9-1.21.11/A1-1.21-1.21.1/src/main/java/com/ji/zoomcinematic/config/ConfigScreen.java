package com.ji.zoomcinematic.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private ModConfig editConfig;
    
    private boolean waitingForKey = false;
    private long keyWaitStartTime = 0;
    private int tempKey1 = -1;
    
    private boolean waitingForZoomKey = false;

    private ButtonWidget keyBindButton;
    private ButtonWidget zoomKeyBindButton;
    private ButtonWidget reportButton;

    public ConfigScreen(Screen parent) {
        super(Text.literal("ConfiguraciÃ³n Ji Zoom Cinematic"));
        this.parent = parent;
        this.editConfig = new ModConfig();
        this.editConfig.copyFrom(ConfigManager.getConfig());
    }

    @Override
    public boolean shouldPause() {
        return true;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int yLeft = 85;
        int yRight = 85;
        int widgetWidth = 135;
        int entryHeight = 26;

        int col1X = centerX - 140;
        int col2X = centerX + 5;

        // --- Left Column ---
        
        // Bars Percent slider
        this.addDrawableChild(new SliderWidget(
                col1X, yLeft, widgetWidth, 20,
                Text.literal("\u00A77Barras: \u00A7f" + String.format("%.0f%%", editConfig.barsPercent)),
                editConfig.barsPercent / 30.0
        ) {
            @Override
            protected void updateMessage() {
                float val = (float) (this.value * 30.0);
                this.setMessage(Text.literal(val == 0 ? "\u00A77Barras: \u00A7cDesactivado" : "\u00A77Barras: \u00A7f" + String.format("%.0f%%", val)));
            }
            @Override
            protected void applyValue() {
                editConfig.barsPercent = (float) (this.value * 30.0);
            }
        });
        yLeft += entryHeight;

        // Base Zoom Level slider
        this.addDrawableChild(new SliderWidget(
                col1X, yLeft, widgetWidth, 20,
                Text.literal("\u00A77Zoom Default: \u00A7f" + String.format("%.2fx", editConfig.baseZoomLevel)),
                (editConfig.baseZoomLevel - 0.05) / 0.95
        ) {
            @Override
            protected void updateMessage() {
                float val = 0.05f + (float) (this.value * 0.95);
                this.setMessage(Text.literal("\u00A77Zoom Default: \u00A7f" + String.format("%.2fx", val)));
            }
            @Override
            protected void applyValue() {
                editConfig.baseZoomLevel = 0.05f + (float) (this.value * 0.95);
            }
        });
        yLeft += entryHeight;

        // --- Right Column ---
        
        // Cinematic Camera Toggle
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("\u00A77C\u00E1mara Suave: ").append(getOnOffText(editConfig.enableCinematicCamera)),
                button -> {
                    editConfig.enableCinematicCamera = !editConfig.enableCinematicCamera;
                    button.setMessage(Text.literal("\u00A77C\u00E1mara Suave: ").append(getOnOffText(editConfig.enableCinematicCamera)));
                }
        ).dimensions(col2X, yRight, widgetWidth, 20).build());
        yRight += entryHeight;

        // Mouse Wheel Toggle
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("\u00A77Scroll Mouse: ").append(getOnOffText(editConfig.mouseWheelEnabled)),
                button -> {
                    editConfig.mouseWheelEnabled = !editConfig.mouseWheelEnabled;
                    button.setMessage(Text.literal("\u00A77Scroll Mouse: ").append(getOnOffText(editConfig.mouseWheelEnabled)));
                }
        ).dimensions(col2X, yRight, widgetWidth, 20).build());
        yRight += entryHeight;


        // --- Center Section ---
        int centerStartY = Math.max(yLeft, yRight) + 15;
        
        // Config Menu Keybind button
        keyBindButton = ButtonWidget.builder(
                Text.literal("Abrir MenÃº: " + getKeyName(editConfig.menuKey1) + " + " + getKeyName(editConfig.menuKey2)),
                button -> {
                    waitingForKey = true;
                    tempKey1 = -1;
                    button.setMessage(Text.literal("Presiona 1ra tecla..."));
                }
        ).dimensions(centerX - widgetWidth / 2, centerStartY, widgetWidth, 20).build();
        this.addDrawableChild(keyBindButton);
        centerStartY += entryHeight;

        // Zoom Keybind button
        zoomKeyBindButton = ButtonWidget.builder(
                Text.literal("Tecla Zoom: " + getKeyName(editConfig.zoomKeyCode)),
                button -> {
                    waitingForZoomKey = true;
                    button.setMessage(Text.literal("Presiona tecla..."));
                }
        ).dimensions(centerX - widgetWidth / 2, centerStartY, widgetWidth, 20).build();
        this.addDrawableChild(zoomKeyBindButton);

        // --- Bottom Centered Buttons ---
        
        // Report Button (Discord)
        this.reportButton = ButtonWidget.builder(
                Text.literal("\u00A7e\u26A0"),
                button -> { net.minecraft.util.Util.getOperatingSystem().open("https://discord.gg/sE27D5SNaq"); }
        ).dimensions(this.width - 35, this.height - 35, 30, 30).build();
        this.addDrawableChild(this.reportButton);

        int bottomY = this.height - 35;
        
        // Save & Done button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Guardar"),
                button -> {
                    ConfigManager.setConfig(editConfig);
                    this.close();
                }
        ).dimensions(centerX - 105, bottomY, 100, 20).build());

        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Cancelar"),
                button -> this.close()
        ).dimensions(centerX + 5, bottomY, 100, 20).build());
    }

    private Text getOnOffText(boolean value) {
        if (value) return Text.literal("\u00A7aON");
        return Text.literal("\u00A7cOFF");
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (this.reportButton != null) {
            long time = System.currentTimeMillis() / 800;
            int phase = (int) (time % 3);
            if (phase == 0) this.reportButton.setMessage(Text.literal("\u00A7e\u26A0"));
            else if (phase == 1) this.reportButton.setMessage(Text.literal("\u00A7b\u2666"));
            else this.reportButton.setMessage(Text.literal("\u00A7a\u2709"));
        }

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00A76\u00A7lJi Zoom Cinematic"), this.width / 2, 55, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00A75By jiory_"), this.width / 2, 65, 0xFFFFFFFF);
    }

    @Override
    public void tick() {
        super.tick();
        if (waitingForKey && tempKey1 != -1) {
            if (System.currentTimeMillis() - keyWaitStartTime > 2000) {
                // Timeout 2 seconds
                waitingForKey = false;
                tempKey1 = -1;
                keyBindButton.setMessage(Text.literal("Abrir MenÃº: " + getKeyName(editConfig.menuKey1) + " + " + getKeyName(editConfig.menuKey2)));
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (waitingForKey || waitingForZoomKey) {
            waitingForKey = false;
            waitingForZoomKey = false;
            tempKey1 = -1;
            keyBindButton.setMessage(Text.literal("Abrir MenÃº: " + getKeyName(editConfig.menuKey1) + " + " + getKeyName(editConfig.menuKey2)));
            zoomKeyBindButton.setMessage(Text.literal("Tecla Zoom: " + getKeyName(editConfig.zoomKeyCode)));
            return true; // Cancelled by clicking outside
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForZoomKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                editConfig.zoomKeyCode = -1;
            } else {
                editConfig.zoomKeyCode = keyCode;
            }
            waitingForZoomKey = false;
            zoomKeyBindButton.setMessage(Text.literal("Tecla Zoom: " + getKeyName(editConfig.zoomKeyCode)));
            return true;
        }

        if (waitingForKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                editConfig.menuKey1 = -1;
                editConfig.menuKey2 = -1;
                waitingForKey = false;
                keyBindButton.setMessage(Text.literal("Abrir MenÃº: NONE"));
                return true;
            }
            if (isKeyReserved(keyCode)) return true;

            if (tempKey1 == -1) {
                tempKey1 = keyCode;
                keyWaitStartTime = System.currentTimeMillis();
                keyBindButton.setMessage(Text.literal("Presiona 2da tecla..."));
            } else {
                if (keyCode != tempKey1) {
                    editConfig.menuKey1 = tempKey1;
                    editConfig.menuKey2 = keyCode;
                    waitingForKey = false;
                    keyBindButton.setMessage(Text.literal("Abrir MenÃº: " + getKeyName(editConfig.menuKey1) + " + " + getKeyName(editConfig.menuKey2)));
                }
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    private static String getKeyName(int keyCode) {
        if (keyCode == -1) return "NONE";
        String name = GLFW.glfwGetKeyName(keyCode, 0);
        if (name != null) return name.toUpperCase();
        
        return switch (keyCode) {
            case GLFW.GLFW_KEY_F1 -> "F1";
            case GLFW.GLFW_KEY_F2 -> "F2";
            case GLFW.GLFW_KEY_F3 -> "F3";
            case GLFW.GLFW_KEY_F4 -> "F4";
            case GLFW.GLFW_KEY_F5 -> "F5";
            case GLFW.GLFW_KEY_F6 -> "F6";
            case GLFW.GLFW_KEY_F7 -> "F7";
            case GLFW.GLFW_KEY_F8 -> "F8";
            case GLFW.GLFW_KEY_F9 -> "F9";
            case GLFW.GLFW_KEY_F10 -> "F10";
            case GLFW.GLFW_KEY_F11 -> "F11";
            case GLFW.GLFW_KEY_F12 -> "F12";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "L-Shift";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "R-Shift";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "L-Ctrl";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "R-Ctrl";
            case GLFW.GLFW_KEY_LEFT_ALT -> "L-Alt";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "R-Alt";
            case GLFW.GLFW_KEY_TAB -> "Tab";
            case GLFW.GLFW_KEY_SPACE -> "Space";
            case GLFW.GLFW_KEY_ENTER -> "Enter";
            case GLFW.GLFW_KEY_BACKSPACE -> "Backspace";
            case GLFW.GLFW_KEY_ESCAPE -> "Escape";
            default -> "Key " + keyCode;
        };
    }

    private boolean isKeyReserved(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_F3 || keyCode == GLFW.GLFW_KEY_F11;
    }
}

