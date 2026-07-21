package com.ji.zoomcinematic.config;

import com.ji.zoomcinematic.JiZoomCinematic;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private ModConfig editConfig;

    private boolean waitingForKey = false;
    private long keyWaitStartTime = 0;
    private int tempKey1 = -1;
    private boolean waitingForZoomKey = false;
    private Button zoomKeyBtnRef;

    private KeyEvent pendingZoomKeyInput = null;
    private boolean pendingZoomKeyEscape = false;
    private boolean pendingZoomKeyActive = false;

    private Button keyBindButton;
    private Button reportButton;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("config.ji_zoom_cinematic.title"));
        this.parent = parent;
        this.editConfig = new ModConfig();
        this.editConfig.copyFrom(ConfigManager.getConfig());
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int yLeft = 85;
        int yRight = 85;
        int widgetWidth = 135;
        int entryHeight = 26;

        int col1X = centerX - 140;
        int col2X = centerX + 5;

        this.addRenderableWidget(Button.builder(
                Component.translatable("config.ji_zoom_cinematic.borders").append(Component.literal(": ")).append(getOnOffText(editConfig.bordersEnabled)),
                button -> {
                    editConfig.bordersEnabled = !editConfig.bordersEnabled;
                    button.setMessage(Component.translatable("config.ji_zoom_cinematic.borders").append(Component.literal(": ")).append(getOnOffText(editConfig.bordersEnabled)));
                }
        ).bounds(col2X, yRight, widgetWidth, 20).build());
        yRight += entryHeight;

        this.addRenderableWidget(new AbstractSliderButton(
                col1X, yLeft, widgetWidth, 20,
                Component.translatable("config.ji_zoom_cinematic.borders").append(Component.literal(": \u00A7f" + String.format("%.0f%%", editConfig.barsPercent))),
                editConfig.barsPercent / 30.0
        ) {
            @Override
            protected void updateMessage() {
                float val = (float) (this.value * 30.0);
                this.setMessage(val == 0 ? Component.translatable("config.ji_zoom_cinematic.borders").append(Component.literal(": ")).append(Component.translatable("config.ji_zoom_cinematic.disabled")) : Component.translatable("config.ji_zoom_cinematic.borders").append(Component.literal(": \u00A7f" + String.format("%.0f%%", val))));
            }
            @Override
            protected void applyValue() {
                editConfig.barsPercent = (float) (this.value * 30.0);
            }
        });
        yLeft += entryHeight;

        float currentMul1 = 1f / editConfig.baseZoomLevel;
        double initVal1 = (currentMul1 - 1f) / 49f;
        if (initVal1 < 0.0) initVal1 = 0.0;
        if (initVal1 > 1.0) initVal1 = 1.0;

        this.addRenderableWidget(new AbstractSliderButton(
                col1X, yLeft, widgetWidth, 20,
                Component.translatable("config.ji_zoom_cinematic.zoom_default").append(Component.literal(": \u00A7f" + Math.round(1f / editConfig.baseZoomLevel) + "x")),
                initVal1
        ) {
            @Override
            protected void updateMessage() {
                float mul = 1f + (float)(this.value * 49f);
                this.setMessage(Component.translatable("config.ji_zoom_cinematic.zoom_default").append(Component.literal(": \u00A7f" + Math.round(mul) + "x")));
            }
            @Override
            protected void applyValue() {
                float mul = 1f + (float)(this.value * 49f);
                editConfig.baseZoomLevel = 1f / mul;
            }
        });
        yLeft += entryHeight;

        float currentMul2 = 1f / editConfig.maxZoomLevel;
        double initVal2 = (currentMul2 - 1f) / 99f;
        if (initVal2 < 0.0) initVal2 = 0.0;
        if (initVal2 > 1.0) initVal2 = 1.0;

        this.addRenderableWidget(new AbstractSliderButton(
                col1X, yLeft, widgetWidth, 20,
                Component.translatable("config.ji_zoom_cinematic.max_zoom").append(Component.literal(": \u00A7f" + Math.round(1f / editConfig.maxZoomLevel) + "x")),
                initVal2
        ) {
            @Override
            protected void updateMessage() {
                float mul = 1f + (float)(this.value * 99f);
                this.setMessage(Component.translatable("config.ji_zoom_cinematic.max_zoom").append(Component.literal(": \u00A7f" + Math.round(mul) + "x")));
            }
            @Override
            protected void applyValue() {
                float mul = 1f + (float)(this.value * 99f);
                editConfig.maxZoomLevel = 1f / mul;
            }
        });
        yLeft += entryHeight;

        this.addRenderableWidget(Button.builder(
                Component.translatable("config.ji_zoom_cinematic.smooth_camera").append(Component.literal(": ")).append(getOnOffText(editConfig.enableCinematicCamera)),
                button -> {
                    editConfig.enableCinematicCamera = !editConfig.enableCinematicCamera;
                    button.setMessage(Component.translatable("config.ji_zoom_cinematic.smooth_camera").append(Component.literal(": ")).append(getOnOffText(editConfig.enableCinematicCamera)));
                }
        ).bounds(col2X, yRight, widgetWidth, 20).build());
        yRight += entryHeight;

        this.addRenderableWidget(Button.builder(
                Component.translatable("config.ji_zoom_cinematic.mouse_wheel").append(Component.literal(": ")).append(getOnOffText(editConfig.mouseWheelEnabled)),
                button -> {
                    editConfig.mouseWheelEnabled = !editConfig.mouseWheelEnabled;
                    button.setMessage(Component.translatable("config.ji_zoom_cinematic.mouse_wheel").append(Component.literal(": ")).append(getOnOffText(editConfig.mouseWheelEnabled)));
                }
        ).bounds(col2X, yRight, widgetWidth, 20).build());
        yRight += entryHeight;

        int centerStartY = Math.max(yLeft, yRight) + 15;

        keyBindButton = Button.builder(
                Component.translatable("config.ji_zoom_cinematic.open_menu").append(Component.literal(": \u00A7e" + getKeyName(editConfig.menuKey1) + " + \u00A7e" + getKeyName(editConfig.menuKey2))),
                button -> {
                    waitingForKey = true;
                    tempKey1 = -1;
                    button.setMessage(Component.translatable("config.ji_zoom_cinematic.press_first_key"));
                }
        ).bounds(centerX - widgetWidth / 2, centerStartY, widgetWidth, 20).build();
        this.addRenderableWidget(keyBindButton);
        centerStartY += entryHeight;

        Button zoomInfoBtn = Button.builder(
                getZoomKeyText(),
                button -> {
                    waitingForZoomKey = true;
                    button.setMessage(Component.translatable("config.ji_zoom_cinematic.press_zoom_key"));
                }
        ).bounds(centerX - widgetWidth / 2, centerStartY, widgetWidth, 20).build();
        this.addRenderableWidget(zoomInfoBtn);
        this.zoomKeyBtnRef = zoomInfoBtn;

        this.reportButton = Button.builder(
                Component.literal("\u00A7e\u26A0"),
                ConfirmLinkScreen.confirmLink(this, "https://discord.gg/sE27D5SNaq")
        ).bounds(this.width - 35, this.height - 35, 30, 30).build();
        this.addRenderableWidget(this.reportButton);

        int bottomY = this.height - 35;

        this.addRenderableWidget(Button.builder(
                Component.literal("\u00A7e\u21BA ").append(Component.translatable("config.ji_zoom_cinematic.reset")),
                button -> {
                    editConfig.barsPercent = 15.0f;
                    editConfig.baseZoomLevel = 0.3333f;
                    editConfig.maxZoomLevel = 0.0666f;
                    editConfig.bordersEnabled = true;
                    editConfig.enableCinematicCamera = true;
                    editConfig.mouseWheelEnabled = true;
                    editConfig.menuKey1 = GLFW.GLFW_KEY_F7;
                    editConfig.menuKey2 = GLFW.GLFW_KEY_Z;
                    editConfig.zoomKeyCode = GLFW.GLFW_KEY_C;
                    pendingZoomKeyActive = false;
                    pendingZoomKeyEscape = false;
                    pendingZoomKeyInput = null;
                    this.rebuildWidgets();
                }
        ).bounds(centerX - 205, bottomY, 130, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("\u00A7a\u2714 ").append(Component.translatable("config.ji_zoom_cinematic.save_exit")),
                button -> {
                    editConfig.zoomKeyCode = resolveZoomKeyCode();
                    ConfigManager.setConfig(editConfig);
                    com.ji.zoomcinematic.JiZoomCinematic.syncZoomKeyFromConfig();
                    Minecraft.getInstance().options.save();
                    KeyMapping.resetMapping();
                    this.onClose();
                }
        ).bounds(centerX - 65, bottomY, 130, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("\u00A7cx ").append(Component.translatable("config.ji_zoom_cinematic.cancel")),
                button -> this.onClose()
        ).bounds(centerX + 75, bottomY, 130, 20).build());
    }

    private int resolveZoomKeyCode() {
        if (pendingZoomKeyActive) {
            if (pendingZoomKeyEscape || pendingZoomKeyInput == null) {
                return -1;
            }
            return pendingZoomKeyInput.key();
        }
        return ConfigManager.getConfig().zoomKeyCode;
    }

    private Component getZoomKeyText() {
        if (pendingZoomKeyActive) {
            if (pendingZoomKeyEscape) return Component.translatable("config.ji_zoom_cinematic.zoom_key").append(Component.literal(": \u00A7eNONE"));
            String label = getKeyLabel(pendingZoomKeyInput);
            return Component.translatable("config.ji_zoom_cinematic.zoom_key").append(Component.literal(": \u00A7e" + label));
        }
        String label = getKeyName(ConfigManager.getConfig().zoomKeyCode);
        return Component.translatable("config.ji_zoom_cinematic.zoom_key").append(Component.literal(": \u00A7e" + label));
    }

    private String getKeyLabel(KeyEvent keyEvent) {
        if (keyEvent == null) return "NONE";
        return getKeyName(keyEvent.key());
    }

    private Component getOnOffText(boolean value) {
        if (value) return Component.translatable("config.ji_zoom_cinematic.on");
        return Component.translatable("config.ji_zoom_cinematic.off");
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);

        if (this.reportButton != null) {
            long time = System.currentTimeMillis() / 800;
            int phase = (int) (time % 3);
            if (phase == 0) this.reportButton.setMessage(Component.literal("\u00A7e\u26A0"));
            else if (phase == 1) this.reportButton.setMessage(Component.literal("\u00A7b\u2666"));
            else this.reportButton.setMessage(Component.literal("\u00A7a\u2709"));
        }

        context.centeredText(this.font, Component.literal("\u00A76\u00A7lJi Zoom Cinematic"), this.width / 2, 55, 0xFFFFFFFF);
        context.centeredText(this.font, Component.literal("\u00A75by: jiory_"), this.width / 2, 65, 0xFFFFFFFF);
    }

    @Override
    public void tick() {
        super.tick();
        if (waitingForKey && tempKey1 != -1) {
            if (System.currentTimeMillis() - keyWaitStartTime > 2000) {
                waitingForKey = false;
                tempKey1 = -1;
                keyBindButton.setMessage(Component.translatable("config.ji_zoom_cinematic.open_menu").append(Component.literal(": \u00A7e" + getKeyName(editConfig.menuKey1) + " + \u00A7e" + getKeyName(editConfig.menuKey2))));
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean wasHandled) {
        if (waitingForZoomKey) {
            waitingForZoomKey = false;
            if (this.zoomKeyBtnRef != null) {
                this.zoomKeyBtnRef.setMessage(getZoomKeyText());
            }
            return true;
        }

        if (waitingForKey) {
            waitingForKey = false;
            tempKey1 = -1;
            keyBindButton.setMessage(Component.translatable("config.ji_zoom_cinematic.open_menu").append(Component.literal(": \u00A7e" + getKeyName(editConfig.menuKey1) + " + \u00A7e" + getKeyName(editConfig.menuKey2))));
            return true;
        }
        return super.mouseClicked(event, wasHandled);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.key();
        if (waitingForZoomKey) {
            pendingZoomKeyActive = true;
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                pendingZoomKeyEscape = true;
                pendingZoomKeyInput = null;
            } else {
                pendingZoomKeyEscape = false;
                pendingZoomKeyInput = keyEvent;
            }
            waitingForZoomKey = false;
            if (this.zoomKeyBtnRef != null) {
                this.zoomKeyBtnRef.setMessage(getZoomKeyText());
            }
            return true;
        }

        if (waitingForKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                editConfig.menuKey1 = -1;
                editConfig.menuKey2 = -1;
                waitingForKey = false;
                keyBindButton.setMessage(Component.translatable("config.ji_zoom_cinematic.open_menu").append(Component.literal(": NONE")));
                return true;
            }
            if (isKeyReserved(keyCode)) return true;

            if (tempKey1 == -1) {
                tempKey1 = keyCode;
                keyWaitStartTime = System.currentTimeMillis();
                keyBindButton.setMessage(Component.translatable("config.ji_zoom_cinematic.press_second_key"));
            } else {
                if (keyCode != tempKey1) {
                    editConfig.menuKey1 = tempKey1;
                    editConfig.menuKey2 = keyCode;
                    waitingForKey = false;
                    keyBindButton.setMessage(Component.translatable("config.ji_zoom_cinematic.open_menu").append(Component.literal(": \u00A7e" + getKeyName(editConfig.menuKey1) + " + \u00A7e" + getKeyName(editConfig.menuKey2))));
                }
            }
            return true;
        }
        return super.keyPressed(keyEvent);
    }

    @Override
    public void onClose() {
        if (parent != null) {
            com.ji.zoomcinematic.util.ReflectionHelper.setScreen(Minecraft.getInstance(), parent);
        } else {
            super.onClose();
        }
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
