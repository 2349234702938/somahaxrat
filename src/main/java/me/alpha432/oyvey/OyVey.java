package me.alpha432.oyvey;

import me.alpha432.oyvey.manager.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = "somahax.new", name = "somahax.new", version = "1.2.4")
public class OyVey {
    public static final String MODID = "somahax.new";
    public static final String MODNAME = "somahax.new";
    public static final String MODVER = "1.2.4";
    public static final String GITHASH = "+1.c0b1f2d772f3";
    public static final Logger LOGGER = LogManager.getLogger("somahax.gg");
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static HoleManager holeManager;
    public static InventoryManager inventoryManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    @Mod.Instance
    public static OyVey INSTANCE;
    public static ChatManager chatManager;
    private static boolean unloaded;

    static {
        unloaded = false;
    }

    public static void load() {
        LOGGER.info("\n\nLoading somahax");
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        chatManager = new ChatManager();
        textManager = new TextManager();
        commandManager = new CommandManager();
        rotationManager = new RotationManager();
        packetManager = new PacketManager();
        eventManager = new EventManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        serverManager = new ServerManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        positionManager = new PositionManager();
        configManager = new ConfigManager();
        holeManager = new HoleManager();
        LOGGER.info("Managers loaded.");
        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        textManager.init(true);
        moduleManager.onLoad();
        LOGGER.info("somahax successfully loaded!\n");
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading somahax");
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        OyVey.onUnload();
        eventManager = null;
        friendManager = null;
        speedManager = null;
        holeManager = null;
        positionManager = null;
        rotationManager = null;
        configManager = null;
        commandManager = null;
        colorManager = null;
        serverManager = null;
        fileManager = null;
        potionManager = null;
        inventoryManager = null;
        moduleManager = null;
        textManager = null;
        LOGGER.info("somahax unloaded!\n");
    }

    public static void reload() {
        OyVey.unload(false);
        OyVey.load();
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(OyVey.configManager.config.replaceFirst("somahax/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Can i get ratted from downloading src on github? - somsoma1");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        String title = MODID + " | " + MODVER + GITHASH + " | " +
                net.minecraft.client.Minecraft.getMinecraft().getVersion() + " | " +
                net.minecraft.client.Minecraft.getMinecraft().getSession().getUsername();
        org.lwjgl.opengl.Display.setTitle(title);
        OyVey.load();
    }
}

