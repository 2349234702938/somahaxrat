package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class SwingEdit extends Module {
    private static SwingEdit INSTANCE = new SwingEdit();

    public enum SwingMode {Mainhand, Offhand, Offhandc, None, Default}
    public Setting<SwingMode> swingmode = register(new Setting("Mode", SwingMode.Mainhand));
    public Setting<Float> swingspeed = register(new Setting("Speed", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(1.0f)));

    public SwingEdit() {
        super("Swing", "options for swinging", Module.Category.PLAYER, false, false, false);
        this.setInstance();
    }

    public static SwingEdit getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new SwingEdit();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public String getDisplayInfo() {
        return this.swingmode.currentEnumName();
    }
}
