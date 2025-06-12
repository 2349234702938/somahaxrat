package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class hoodjusticeSpeech extends Module {
    private static hoodjusticeSpeech INSTANCE = new hoodjusticeSpeech();
    Setting<Boolean> lowercase = register(new Setting("Lowercase", false));

    public hoodjusticeSpeech() {
        super("hoodjusticSpeech", "speak like hoodjustice", Module.Category.MISC, false, false, false);
        this.setInstance();
    }

    public String changeCaps(String text) {
        if (!isOn()) return text;
        if (!lowercase.getValue()) {
            return text.toUpperCase();
        } else {
            return text.toLowerCase();
        }
    }

    public static hoodjusticeSpeech getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new hoodjusticeSpeech();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
