package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.manager.ChatManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;

import java.util.HashMap;

public class PopCounter extends Module {
    public static HashMap<String, Integer> TotemPopContainer = new HashMap<>();
    public static PopCounter INSTANCE = new PopCounter();

    public enum ClientName { m0n3yh00k, SomahaxRAINBOW, Somahax, SomahaxCool }
    public final Setting<ClientName> clientname = register(new Setting<>("Client", ClientName.SomahaxRAINBOW));

    private static final int SENDER_ID = 1;

    public PopCounter() {
        super("PopCounter", "Counts other players totem pops.", Category.MISC, true, false, false);
        setInstance();
    }

    public static PopCounter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PopCounter();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        TotemPopContainer.clear();
    }

    public void onTotemPop(EntityPlayer player) {
        if (PopCounter.fullNullCheck()) return;
        if (PopCounter.mc.player.equals(player)) return;

        int count = TotemPopContainer.getOrDefault(player.getName(), 0) + 1;
        TotemPopContainer.put(player.getName(), count);

        ChatManager chatManager = getChatManager();
        if (chatManager == null) return;

        String message = getPopMessage(player.getName(), count);
        ITextComponent component = new TextComponentString(message);
        chatManager.replace(component, player.getName() + "_pop", SENDER_ID, true);
    }
    public void onDeath(EntityPlayer player) {
        if (player == null) return;
        TotemPopContainer.remove(player.getName());
        ChatManager chatManager = getChatManager();
        if (chatManager != null) {
            chatManager.deleteMessage(player.getName() + "_pop", 1);
        }
    }

    private String getPopMessage(String player, int count) {
        switch (clientname.getValue()) {
            case m0n3yh00k:
                return ChatFormatting.GRAY + "[" +
                        ChatFormatting.LIGHT_PURPLE + "m0n3yh00k" +
                        ChatFormatting.WHITE + ".cz" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.WHITE + player + " popped " +
                        ChatFormatting.LIGHT_PURPLE + count +
                        ChatFormatting.WHITE + " totem" + (count == 1 ? "" : "s") + " LELEL";
            case SomahaxRAINBOW:
                return ChatFormatting.GRAY + "[" +
                        ChatFormatting.RED + "s" +
                        ChatFormatting.GOLD + "o" +
                        ChatFormatting.YELLOW + "m" +
                        ChatFormatting.GREEN + "a" +
                        ChatFormatting.AQUA + "h" +
                        ChatFormatting.BLUE + "a" +
                        ChatFormatting.LIGHT_PURPLE + "x" +
                        ChatFormatting.DARK_PURPLE + ".new" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.WHITE + player + " popped " +
                        ChatFormatting.LIGHT_PURPLE + count +
                        ChatFormatting.WHITE + " totem" + (count == 1 ? "" : "s") + " LELEL";
            case Somahax:
                return ChatFormatting.GRAY + "[" +
                        ChatFormatting.WHITE + "soma" +
                        ChatFormatting.DARK_PURPLE + "hax" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.WHITE + player + " popped " +
                        ChatFormatting.DARK_PURPLE + count +
                        ChatFormatting.WHITE + " totem" + (count == 1 ? "" : "s") + " LELEL";
            case SomahaxCool:
                return ChatFormatting.GRAY + "[" +
                        ChatFormatting.LIGHT_PURPLE + "soma" +
                        ChatFormatting.WHITE + "hax" +
                        ChatFormatting.WHITE + ".new"+
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.WHITE + player + " popped " +
                        ChatFormatting.LIGHT_PURPLE + count +
                        ChatFormatting.WHITE + " totem" + (count == 1 ? "" : "s") + " LELEL";
            default:
                return player + " popped " + count + " totem" + (count == 1 ? "" : "s") + "!";
        }
    }

    private ChatManager getChatManager() {
        return me.alpha432.oyvey.OyVey.chatManager;
    }
}