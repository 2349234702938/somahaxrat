package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;

public class FastPlace
        extends Module {
    public FastPlace() {
        super("FastPlace", "Fast everything.", Module.Category.PLAYER, true, false, false);
    }

    private final Setting<Boolean> xp = register(new Setting<>("XP", true, "Reset when Using Xp."));
    private final Setting<Boolean> crystal = register(new Setting<>("Crystal", true, "Reset when placing Crystals."));
    private final Setting<Boolean> Blocks = register(new Setting<>("Blocks", true, "Reset when placing Blocks."));

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (shouldReset()) {
            if (mc.rightClickDelayTimer > 0) {
                mc.rightClickDelayTimer = 0;
            }
        }
    }

    private Boolean shouldReset() {
        if (xp.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE) {
            return true;
        }
        if (crystal.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            return true;
        }
        return Blocks.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock;
    }
}

