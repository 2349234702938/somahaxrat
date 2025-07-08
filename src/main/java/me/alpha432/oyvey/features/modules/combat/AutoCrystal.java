package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.manager.ChatManager;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;

public class AutoCrystal extends Module {

    enum ClientName {m0n3yh00k, SomahaxRAINBOW, Somahax, SomahaxCool}
    Setting<ClientName> clientname = register(new Setting<>("Client", ClientName.m0n3yh00k));
    private static final String UNIQUE_WORD = "CAMessage";
    private static final int SENDER_ID = 0;

    public AutoCrystal() {
        super("Autocrystal", "shows notifications", Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        ChatManager chatManager = getChatManager();
        if (chatManager == null) return;
        String onMessage = getOnMessage();
        ITextComponent component = new TextComponentString(onMessage);
        chatManager.replace(component, UNIQUE_WORD, SENDER_ID, true);
    }

    @Override
    public void onDisable() {
        ChatManager chatManager = getChatManager();
        if (chatManager == null) return;
        String offMessage = getOffMessage();
        ITextComponent component = new TextComponentString(offMessage);
        chatManager.replace(component, UNIQUE_WORD, SENDER_ID, true);
    }

    private String getOnMessage() {
        switch (clientname.getValue()) {
            case m0n3yh00k:
                return  ChatFormatting.GRAY + "[" +
                        ChatFormatting.LIGHT_PURPLE + "m0n3yh00k" +
                        ChatFormatting.WHITE + ".cz" + ChatFormatting.GRAY + "] " +
                        ChatFormatting.GRAY + "AutoCrystal toggled " +
                        ChatFormatting.GREEN + "on" +
                        ChatFormatting.GRAY + ";";
            case SomahaxRAINBOW:
                return  ChatFormatting.GRAY + "[" +
                        ChatFormatting.RED + "s" +
                        ChatFormatting.GOLD + "o" +
                        ChatFormatting.YELLOW + "m" +
                        ChatFormatting.GREEN + "a" +
                        ChatFormatting.AQUA + "h" +
                        ChatFormatting.BLUE + "a" +
                        ChatFormatting.LIGHT_PURPLE + "x" +
                        ChatFormatting.DARK_PURPLE + "." +
                        ChatFormatting.RED + "n" +
                        ChatFormatting.GOLD + "e" +
                        ChatFormatting.YELLOW + "w" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.WHITE + "AutoCrystal toggled " +
                        ChatFormatting.GREEN + "TRUE" +
                        ChatFormatting.GRAY + ";";
            case Somahax:
                return  ChatFormatting.GRAY + "[" +
                        ChatFormatting.WHITE + "soma" +
                        ChatFormatting.DARK_PURPLE + "hax.new" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.GREEN + "AutoCrystal turned ON!";
            case SomahaxCool:
                return  ChatFormatting.GRAY + "[" +
                        ChatFormatting.LIGHT_PURPLE + "soma" +
                        ChatFormatting.WHITE + "hax.new" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.GRAY + "AutoCrystal toggled " +
                        ChatFormatting.GREEN + "TRUE";


            default:
                return "";
        }
    }

    private String getOffMessage() {
        switch (clientname.getValue()) {
            case m0n3yh00k:
                return ChatFormatting.GRAY + "[" +
                       ChatFormatting.LIGHT_PURPLE + "m0n3yh00k" +
                       ChatFormatting.WHITE + ".cz" +
                       ChatFormatting.GRAY + "] " +
                       ChatFormatting.GRAY + "AutoCrystal toggled " +
                       ChatFormatting.RED + "off" +
                       ChatFormatting.GRAY + ";";
            case SomahaxRAINBOW:
                return ChatFormatting.GRAY + "[" +
                        ChatFormatting.RED + "s" +
                        ChatFormatting.GOLD + "o" +
                        ChatFormatting.YELLOW + "m" +
                        ChatFormatting.GREEN + "a" +
                        ChatFormatting.AQUA + "h" +
                        ChatFormatting.BLUE + "a" +
                        ChatFormatting.LIGHT_PURPLE + "x" +
                        ChatFormatting.DARK_PURPLE + "." +
                        ChatFormatting.RED + "n" +
                        ChatFormatting.GOLD + "e" +
                        ChatFormatting.YELLOW + "w" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.WHITE + "AutoCrystal toggled " +
                        ChatFormatting.RED + "FALSE" +
                        ChatFormatting.GRAY + ";";
            case Somahax:
                return ChatFormatting.GRAY + "[" +
                       ChatFormatting.WHITE + "soma" +
                       ChatFormatting.DARK_PURPLE + "hax.new" +
                       ChatFormatting.GRAY + "] " +
                       ChatFormatting.RED + "AutoCrystal turned OFF!";
            case SomahaxCool:
                return ChatFormatting.GRAY + "[" +
                        ChatFormatting.LIGHT_PURPLE + "soma" +
                        ChatFormatting.WHITE + "hax.new" +
                        ChatFormatting.GRAY + "] " +
                        ChatFormatting.GRAY + "AutoCrystal toggled " +
                        ChatFormatting.RED + "FALSE;";
            default:
                return "";
        }
    }

    private ChatManager getChatManager() {
        return me.alpha432.oyvey.OyVey.chatManager;
    }
}