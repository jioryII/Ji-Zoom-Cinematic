package com.ji.zoomcinematic.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.option.KeyBinding;
import com.ji.zoomcinematic.JiZoomCinematic;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private ModConfig editConfig;
    
    private boolean waitingForKey = false;
    private long keyWaitStartTime = 0;
    private int tempKey1 = -1;
    private boolean waitingForZoomKey = false;
    private ButtonWidget zoomKeyBtnRef;
    
    private int pendingZoomKeyCode = -1;
    private int pendingZoomScanCode = -1;
    
    private ButtonWidget keyBindButton;
    private ButtonWidget reportButton;

    public ConfigScreen(Screen parent) {
        super(Text.translatable("config.ji_zoom_cinematic.title"));
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

        // Toggle Bordes
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("config.ji_zoom_cinematic.borders").append(Text.literal(": ")).append(getOnOffText(editConfig.bordersEnabled)),
                button -> {
                    editConfig.bordersEnabled = !editConfig.bordersEnabled;
                    button.setMessage(Text.translatable("config.ji_zoom_cinematic.borders").append(Text.literal(": ")).append(getOnOffText(editConfig.bordersEnabled)));
                }
        ).dimensions(col2X, yRight, widgetWidth, 20).build());
        yRight += entryHeight;

        // Bars Percent slider
        this.addDrawableChild(new SliderWidget(
                col1X, yLeft, widgetWidth, 20,
                Text.translatable("config.ji_zoom_cinematic.borders").append(Text.literal(": \u00A7f" + String.format("%.0f%%", editConfig.barsPercent))),
                editConfig.barsPercent / 30.0
        ) {
            @Override
            protected void updateMessage() {
                float val = (float) (this.value * 30.0);
                this.setMessage(val == 0 ? Text.translatable("config.ji_zoom_cinematic.borders").append(Text.literal(": ")).append(Text.translatable("config.ji_zoom_cinematic.disabled")) : Text.translatable("config.ji_zoom_cinematic.borders").append(Text.literal(": \u00A7f" + String.format("%.0f%%", val))));
            }
            @Override
            protected void applyValue() {
                editConfig.barsPercent = (float) (this.value * 30.0);
            }
        });
        yLeft += entryHeight;

        // Base Zoom Level slider (default zoom)
        float currentMul1 = 1f / editConfig.baseZoomLevel;
        double initVal1 = (currentMul1 - 1f) / 49f;
        if (initVal1 < 0.0) initVal1 = 0.0;
        if (initVal1 > 1.0) initVal1 = 1.0;
        
        this.addDrawableChild(new SliderWidget(
                col1X, yLeft, widgetWidth, 20,
                Text.translatable("config.ji_zoom_cinematic.zoom_default").append(Text.literal(": \u00A7f" + Math.round(1f / editConfig.baseZoomLevel) + "x")),
                initVal1
        ) {
            @Override
            protected void updateMessage() {
                float mul = 1f + (float)(this.value * 49f);
                this.setMessage(Text.translatable("config.ji_zoom_cinematic.zoom_default").append(Text.literal(": \u00A7f" + Math.round(mul) + "x")));
            }
            @Override
            protected void applyValue() {
                float mul = 1f + (float)(this.value * 49f);
                editConfig.baseZoomLevel = 1f / mul;
            }
        });
        yLeft += entryHeight;

        // Max Zoom Level slider
        float currentMul2 = 1f / editConfig.maxZoomLevel;
        double initVal2 = (currentMul2 - 1f) / 99f;
        if (initVal2 < 0.0) initVal2 = 0.0;
        if (initVal2 > 1.0) initVal2 = 1.0;
        
        this.addDrawableChild(new SliderWidget(
                col1X, yLeft, widgetWidth, 20,
                Text.translatable("config.ji_zoom_cinematic.max_zoom").append(Text.literal(": \u00A7f" + Math.round(1f / editConfig.maxZoomLevel) + "x")),
                initVal2
        ) {
            @Override
            protected void updateMessage() {
                float mul = 1f + (float)(this.value * 99f);
                this.setMessage(Text.translatable("config.ji_zoom_cinematic.max_zoom").append(Text.literal(": \u00A7f" + Math.round(mul) + "x")));
            }
            @Override
            protected void applyValue() {
                float mul = 1f + (float)(this.value * 99f);
                editConfig.maxZoomLevel = 1f / mul;
            }
        });
        yLeft += entryHeight;

        // Cinematic Camera Toggle
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("config.ji_zoom_cinematic.smooth_camera").append(Text.literal(": ")).append(getOnOffText(editConfig.enableCinematicCamera)),
                button -> {
                    editConfig.enableCinematicCamera = !editConfig.enableCinematicCamera;
                    button.setMessage(Text.translatable("config.ji_zoom_cinematic.smooth_camera").append(Text.literal(": ")).append(getOnOffText(editConfig.enableCinematicCamera)));
                }
        ).dimensions(col2X, yRight, widgetWidth, 20).build());
        yRight += entryHeight;

        // Mouse Wheel Toggle
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("config.ji_zoom_cinematic.mouse_wheel").append(Text.literal(": ")).append(getOnOffText(editConfig.mouseWheelEnabled)),
                button -> {
                    editConfig.mouseWheelEnabled = !editConfig.mouseWheelEnabled;
                    button.setMessage(Text.translatable("config.ji_zoom_cinematic.mouse_wheel").append(Text.literal(": ")).append(getOnOffText(editConfig.mouseWheelEnabled)));
                }
        ).dimensions(col2X, yRight, widgetWidth, 20).build());
        yRight += entryHeight;

        int centerStartY = Math.max(yLeft, yRight) + 15;
        
        // Config Menuu Keybind button
        keyBindButton = ButtonWidget.builder(
                Text.translatable("config.ji_zoom_cinematic.open_menu").append(Text.literal(": \u00A7e" + getKeyName(editConfig.menuKey1) + " + \u00A7e" + getKeyName(editConfig.menuKey2))),
                button -> {
                    waitingForKey = true;
                    tempKey1 = -1;
                    button.setMessage(Text.translatable("config.ji_zoom_cinematic.press_first_key"));
                }
        ).dimensions(centerX - widgetWidth / 2, centerStartY, widgetWidth, 20).build();
        this.addDrawableChild(keyBindButton);
        centerStartY += entryHeight;

        ButtonWidget zoomInfoBtn = ButtonWidget.builder(
                getZoomKeyText(),
                button -> {
                    waitingForZoomKey = true;
                    button.setMessage(Text.translatable("config.ji_zoom_cinematic.press_zoom_key"));
                }
        ).dimensions(centerX - widgetWidth / 2, centerStartY, widgetWidth, 20).build();
        this.addDrawableChild(zoomInfoBtn);
        this.zoomKeyBtnRef = zoomInfoBtn;

        // Report Button (Discord)
        this.reportButton = ButtonWidget.builder(
                Text.literal("\u00A7e\u26A0"),
                button -> { net.minecraft.util.Util.getOperatingSystem().open("https://discord.gg/sE27D5SNaq"); }
        ).dimensions(this.width - 35, this.height - 35, 30, 30).build();
        this.addDrawableChild(this.reportButton);

        int bottomY = this.height - 35;
        
        // Reset button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("\u00A7e\u21BA ").append(Text.translatable("config.ji_zoom_cinematic.reset")),
                button -> {
                    editConfig.barsPercent = 15.0f;
                    editConfig.baseZoomLevel = 0.3333f;
                    editConfig.maxZoomLevel = 0.0333f;
                    editConfig.bordersEnabled = true;
                    editConfig.enableCinematicCamera = true;
                    editConfig.mouseWheelEnabled = true;
                    editConfig.menuKey1 = GLFW.GLFW_KEY_F7;
                    editConfig.menuKey2 = GLFW.GLFW_KEY_Z;
                    pendingZoomKeyCode = GLFW.GLFW_KEY_C;
                    pendingZoomScanCode = 0;
                    this.clearChildren();
                    this.init();
                }
        ).dimensions(centerX - 205, bottomY, 130, 20).build());

        // Save & Done button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("\u00A7a\u2714 ").append(Text.translatable("config.ji_zoom_cinematic.save_exit")),
                button -> {
                    ConfigManager.setConfig(editConfig);
                    if (pendingZoomKeyCode != -1) {
                        if (pendingZoomKeyCode == GLFW.GLFW_KEY_ESCAPE) {
                            JiZoomCinematic.ZOOM_KEY.setBoundKey(InputUtil.UNKNOWN_KEY);
                        } else {
                            JiZoomCinematic.ZOOM_KEY.setBoundKey(InputUtil.fromKeyCode(pendingZoomKeyCode, pendingZoomScanCode));
                        }
                        MinecraftClient.getInstance().options.write();
                        KeyBinding.updateKeysByCode();
                    }
                    this.close();
                }
        ).dimensions(centerX - 65, bottomY, 130, 20).build());

        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("\u00A7cx ").append(Text.translatable("config.ji_zoom_cinematic.cancel")),
                button -> this.close()
        ).dimensions(centerX + 75, bottomY, 130, 20).build());
    }

    private Text getZoomKeyText() {
        if (pendingZoomKeyCode != -1) {
            if (pendingZoomKeyCode == GLFW.GLFW_KEY_ESCAPE) return Text.translatable("config.ji_zoom_cinematic.zoom_key").append(Text.literal(": \u00A7eNONE"));
            return Text.translatable("config.ji_zoom_cinematic.zoom_key").append(Text.literal(": \u00A7e" + InputUtil.fromKeyCode(pendingZoomKeyCode, pendingZoomScanCode).getLocalizedText().getString().toUpperCase()));
        }
        return Text.translatable("config.ji_zoom_cinematic.zoom_key").append(Text.literal(": \u00A7e" + JiZoomCinematic.ZOOM_KEY.getBoundKeyLocalizedText().getString().toUpperCase()));
    }

    private Text getOnOffText(boolean value) {
        if (value) return Text.translatable("config.ji_zoom_cinematic.on");
        return Text.translatable("config.ji_zoom_cinematic.off");
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
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00A75by: jiory_"), this.width / 2, 65, 0xFFFFFFFF);
    }

    @Override
    public void tick() {
        super.tick();
        if (waitingForKey && tempKey1 != -1) {
            if (System.currentTimeMillis() - keyWaitStartTime > 2000) {
                waitingForKey = false;
                tempKey1 = -1;
                keyBindButton.setMessage(Text.translatable("config.ji_zoom_cinematic.open_menu").append(Text.literal(": \u00A7e" + getKeyName(editConfig.menuKey1) + " + \u00A7e" + getKeyName(editConfig.menuKey2))));
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
            keyBindButton.setMessage(Text.translatable("config.ji_zoom_cinematic.open_menu").append(Text.literal(": \u00A7e" + getKeyName(editConfig.menuKey1) + " + \u00A7e" + getKeyName(editConfig.menuKey2))));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForZoomKey) {
            pendingZoomKeyCode = keyCode;
            pendingZoomScanCode = scanCode;
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
                keyBindButton.setMessage(Text.translatable("config.ji_zoom_cinematic.open_menu").append(Text.literal(": NONE")));
                return true;
            }
            if (isKeyReserved(keyCode)) return true;

            if (tempKey1 == -1) {
                tempKey1 = keyCode;
                keyWaitStartTime = System.currentTimeMillis();
                keyBindButton.setMessage(Text.translatable("config.ji_zoom_cinematic.press_second_key"));
            } else {
                if (keyCode != tempKey1) {
                    editConfig.menuKey1 = tempKey1;
                    editConfig.menuKey2 = keyCode;
                    waitingForKey = false;
                    keyBindButton.setMessage(Text.translatable("config.ji_zoom_cinematic.open_menu").append(Text.literal(": \u00A7e" + getKeyName(editConfig.menuKey1) + " + \u00A7e" + getKeyName(editConfig.menuKey2))));
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
