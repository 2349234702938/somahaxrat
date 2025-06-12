package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ClientEvent;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.Timer;
import me.alpha432.oyvey.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.*;

public class HUD extends Module {
    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    private static HUD INSTANCE = new HUD();
    private final Setting<Boolean> grayNess = register(new Setting("Gray", true));
    private final Setting<Boolean> renderingUp = register(new Setting("RenderingUp", false, "Orientation of the HUD-Elements."));
    private final Setting<Boolean> waterMark = register(new Setting("Watermark", false, "displays watermark"));
    private final Setting<Boolean> arrayList = register(new Setting("ActiveModules", false, "Lists the active modules."));
    private final Setting<Boolean> coords = register(new Setting("Coords", false, "Your current coordinates"));
    private final Setting<Boolean> direction = register(new Setting("Direction", false, "The Direction you are facing."));
    private final Setting<Boolean> armor = register(new Setting("Armor", false, "ArmorHUD"));
    private final Setting<Boolean> totems = register(new Setting("Totems", false, "TotemHUD"));
    private final Setting<Boolean> greeter = register(new Setting("Welcomer", false, "The time"));
    private final Setting<Boolean> speed = register(new Setting("Speed", false, "Your Speed"));
    private final Setting<Boolean> potions = register(new Setting("Potions", false, "Your Speed"));
    private final Setting<Boolean> ping = register(new Setting("Ping", false, "Your response time to the server."));
    private final Setting<Boolean> tps = register(new Setting("TPS", false, "Ticks per second of the server."));
    private final Setting<Boolean> fps = register(new Setting("FPS", false, "Your frames per second."));
    private final Setting<Boolean> lag = register(new Setting("LagNotifier", false, "The time"));
    private final Setting<Boolean> textRadar = register(new Setting("TextRadar", false, "Shows all the Players names around you"));
    private final Timer timer = new Timer();
    private final Map<String, Integer> players = new HashMap<>();
    public Setting<String> name = register(new Setting("Name", "somahax.sex"));
    public Setting<TextUtil.Color> bracketColor = register(new Setting("BracketColor", TextUtil.Color.BLUE));
    public Setting<TextUtil.Color> commandColor = register(new Setting("NameColor", TextUtil.Color.BLUE));
    public Setting<String> commandBracket = register(new Setting("Bracket", "<"));
    public Setting<String> commandBracket2 = register(new Setting("Bracket2", ">"));
    public Setting<Boolean> notifyToggles = register(new Setting("ChatNotify", Boolean.valueOf(false), "notifys in chat"));
    public Setting<Boolean> magenDavid = register(new Setting("MagenDavid", Boolean.valueOf(false), "draws magen david"));
    public Setting<Integer> animationHorizontalTime = register(new Setting("AnimationHTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> arrayList.getValue().booleanValue()));
    public Setting<Integer> animationVerticalTime = register(new Setting("AnimationVTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), v -> arrayList.getValue().booleanValue()));
    public Setting<RenderingMode> renderingMode = register(new Setting("Ordering", RenderingMode.ABC));
    public Setting<Integer> waterMarkY = register(new Setting("WatermarkPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(20), v -> waterMark.getValue().booleanValue()));
    public Setting<Boolean> time = register(new Setting("Time", Boolean.valueOf(false), "The time"));
    public Setting<Integer> lagTime = register(new Setting("LagTime", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(2000)));
    private int color;
    private boolean shouldIncrement;
    private int hitMarkerTimer;

    public HUD() {
        super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
        setInstance();
    }

    public static HUD getInstance() {
        if (INSTANCE == null)
            INSTANCE = new HUD();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onUpdate() {
        if (shouldIncrement)
            hitMarkerTimer++;
        if (hitMarkerTimer == 10) {
            hitMarkerTimer = 0;
            shouldIncrement = false;
        }
    }

    public void onRender2D(Render2DEvent event) {
        if (fullNullCheck())
            return;
        int width = renderer.scaledWidth;
        int height = renderer.scaledHeight;
        color = ColorUtil.toRGBA((ClickGui.getInstance()).red.getValue().intValue(), (ClickGui.getInstance()).green.getValue().intValue(), (ClickGui.getInstance()).blue.getValue().intValue());
        if (waterMark.getValue().booleanValue()) {
            String string = name.getPlannedValue() + " 0.69";
            if ((ClickGui.getInstance()).rainbow.getValue().booleanValue()) {
                if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    renderer.drawString(string, 2.0F, waterMarkY.getValue().intValue(), ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = {1};
                    char[] stringToCharArray = string.toCharArray();
                    float f = 0.0F;
                    for (char c : stringToCharArray) {
                        renderer.drawString(String.valueOf(c), 2.0F + f, waterMarkY.getValue().intValue(), ColorUtil.rainbow(arrayOfInt[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                        f += renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                renderer.drawString(string, 2.0F, waterMarkY.getValue().intValue(), color, true);
            }
        }
        int[] counter1 = {1};
        int j = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat && !renderingUp.getValue().booleanValue()) ? 14 : 0;
        if (arrayList.getValue().booleanValue())
            if (renderingUp.getValue().booleanValue()) {
                if (renderingMode.getValue() == RenderingMode.ABC) {
                    for (int k = 0; k < OyVey.moduleManager.sortedModulesABC.size(); k++) {
                        String str = OyVey.moduleManager.sortedModulesABC.get(k);
                        renderer.drawString(str, (width - 2 - renderer.getStringWidth(str)), (2 + j * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                        j++;
                        counter1[0] = counter1[0] + 1;
                    }
                } else {
                    for (int k = 0; k < OyVey.moduleManager.sortedModules.size(); k++) {
                        Module module = OyVey.moduleManager.sortedModules.get(k);
                        String str = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
                        renderer.drawString(str, (width - 2 - renderer.getStringWidth(str)), (2 + j * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                        j++;
                        counter1[0] = counter1[0] + 1;
                    }
                }
            } else if (renderingMode.getValue() == RenderingMode.ABC) {
                for (int k = 0; k < OyVey.moduleManager.sortedModulesABC.size(); k++) {
                    String str = OyVey.moduleManager.sortedModulesABC.get(k);
                    j += 10;
                    renderer.drawString(str, (width - 2 - renderer.getStringWidth(str)), (height - j), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                for (int k = 0; k < OyVey.moduleManager.sortedModules.size(); k++) {
                    Module module = OyVey.moduleManager.sortedModules.get(k);
                    String str = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
                    j += 10;
                    renderer.drawString(str, (width - 2 - renderer.getStringWidth(str)), (height - j), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        String grayString = grayNess.getValue().booleanValue() ? String.valueOf(ChatFormatting.GRAY) : "";
        int i = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat && renderingUp.getValue().booleanValue()) ? 13 : (renderingUp.getValue().booleanValue() ? -2 : 0);
        if (renderingUp.getValue().booleanValue()) {
            if (potions.getValue().booleanValue()) {
                List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str = OyVey.potionManager.getColoredPotionString(potionEffect);
                    i += 10;
                    renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (height - 2 - i), potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (speed.getValue().booleanValue()) {
                String str = grayString + "Speed " + ChatFormatting.WHITE + OyVey.speedManager.getSpeedKpH() + " km/h";
                i += 10;
                renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (time.getValue().booleanValue()) {
                String str = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                i += 10;
                renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (tps.getValue().booleanValue()) {
                String str = grayString + "TPS " + ChatFormatting.WHITE + OyVey.serverManager.getTPS();
                i += 10;
                renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                counter1[0] = counter1[0] + 1;
            }
            String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            String str1 = grayString + "Ping " + ChatFormatting.WHITE + OyVey.serverManager.getPing();
            if (renderer.getStringWidth(str1) > renderer.getStringWidth(fpsText)) {
                if (ping.getValue().booleanValue()) {
                    i += 10;
                    renderer.drawString(str1, (width - renderer.getStringWidth(str1) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (fps.getValue().booleanValue()) {
                    i += 10;
                    renderer.drawString(fpsText, (width - renderer.getStringWidth(fpsText) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (fps.getValue().booleanValue()) {
                    i += 10;
                    renderer.drawString(fpsText, (width - renderer.getStringWidth(fpsText) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (ping.getValue().booleanValue()) {
                    i += 10;
                    renderer.drawString(str1, (width - renderer.getStringWidth(str1) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            if (potions.getValue().booleanValue()) {
                List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str = OyVey.potionManager.getColoredPotionString(potionEffect);
                    renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (2 + i++ * 10), potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (speed.getValue().booleanValue()) {
                String str = grayString + "Speed " + ChatFormatting.WHITE + OyVey.speedManager.getSpeedKpH() + " km/h";
                renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (time.getValue().booleanValue()) {
                String str = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (tps.getValue().booleanValue()) {
                String str = grayString + "TPS " + ChatFormatting.WHITE + OyVey.serverManager.getTPS();
                renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                counter1[0] = counter1[0] + 1;
            }
            String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            String str1 = grayString + "Ping " + ChatFormatting.WHITE + OyVey.serverManager.getPing();
            if (renderer.getStringWidth(str1) > renderer.getStringWidth(fpsText)) {
                if (ping.getValue().booleanValue()) {
                    renderer.drawString(str1, (width - renderer.getStringWidth(str1) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (fps.getValue().booleanValue()) {
                    renderer.drawString(fpsText, (width - renderer.getStringWidth(fpsText) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (fps.getValue().booleanValue()) {
                    renderer.drawString(fpsText, (width - renderer.getStringWidth(fpsText) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (ping.getValue().booleanValue()) {
                    renderer.drawString(str1, (width - renderer.getStringWidth(str1) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        boolean inHell = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
        int posX = (int) mc.player.posX;
        int posY = (int) mc.player.posY;
        int posZ = (int) mc.player.posZ;
        float nether = !inHell ? 0.125F : 8.0F;
        int hposX = (int) (mc.player.posX * nether);
        int hposZ = (int) (mc.player.posZ * nether);
        i = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? 14 : 0;
        String coordinates = ChatFormatting.WHITE + "XYZ " + ChatFormatting.RESET + (inHell ? (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]" + ChatFormatting.RESET) : (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]"));
        String direction = this.direction.getValue().booleanValue() ? OyVey.rotationManager.getDirection4D(false) : "";
        String coords = this.coords.getValue().booleanValue() ? coordinates : "";
        i += 10;
        if ((ClickGui.getInstance()).rainbow.getValue().booleanValue()) {
            String rainbowCoords = this.coords.getValue().booleanValue() ? ("XYZ " + (inHell ? (posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]") : (posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]"))) : "";
            if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                renderer.drawString(direction, 2.0F, (height - i - 11), ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                renderer.drawString(rainbowCoords, 2.0F, (height - i), ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
            } else {
                int[] counter2 = {1};
                char[] stringToCharArray = direction.toCharArray();
                float s = 0.0F;
                for (char c : stringToCharArray) {
                    renderer.drawString(String.valueOf(c), 2.0F + s, (height - i - 11), ColorUtil.rainbow(counter2[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                    s += renderer.getStringWidth(String.valueOf(c));
                    counter2[0] = counter2[0] + 1;
                }
                int[] counter3 = {1};
                char[] stringToCharArray2 = rainbowCoords.toCharArray();
                float u = 0.0F;
                for (char c : stringToCharArray2) {
                    renderer.drawString(String.valueOf(c), 2.0F + u, (height - i), ColorUtil.rainbow(counter3[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                    u += renderer.getStringWidth(String.valueOf(c));
                    counter3[0] = counter3[0] + 1;
                }
            }
        } else {
            renderer.drawString(direction, 2.0F, (height - i - 11), color, true);
            renderer.drawString(coords, 2.0F, (height - i), color, true);
        }
        if (armor.getValue().booleanValue())
            renderArmorHUD(true);
        if (totems.getValue().booleanValue())
            renderTotemHUD();
        if (greeter.getValue().booleanValue())
            renderGreeter();
        if (lag.getValue().booleanValue())
            renderLag();
        if (textRadar.getValue().booleanValue()) {
            Map<String, Integer> textRadarPlayers = getTextRadarPlayers();
            int yOffset = 0;
            for (Map.Entry<String, Integer> entry : textRadarPlayers.entrySet()) {
                String playerName = entry.getKey();
                int playerDistance = entry.getValue();
                String displayText = playerName + " (" + playerDistance + "m)";
                renderer.drawString(displayText, 2.0F, (height - i - 11 - yOffset), color, true);
                yOffset += 10;
            }
        }
    }

    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }

    public void renderGreeter() {
        int width = renderer.scaledWidth;
        String text = "";
        if (greeter.getValue().booleanValue())
            text = text + MathUtil.getTimeOfDay() + mc.player.getDisplayNameString();
        if ((ClickGui.getInstance()).rainbow.getValue().booleanValue()) {
            if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                renderer.drawString(text, width / 2.0F - renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
            } else {
                int[] counter1 = {1};
                char[] stringToCharArray = text.toCharArray();
                float i = 0.0F;
                for (char c : stringToCharArray) {
                    renderer.drawString(String.valueOf(c), width / 2.0F - renderer.getStringWidth(text) / 2.0F + 2.0F + i, 2.0F, ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                    i += renderer.getStringWidth(String.valueOf(c));
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            renderer.drawString(text, width / 2.0F - renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, color, true);
        }
    }

    public void renderLag() {
        int width = renderer.scaledWidth;
        if (OyVey.serverManager.isServerNotResponding()) {
            String text = ChatFormatting.RED + "FUCK! The server is not responding for " + MathUtil.round((float) OyVey.serverManager.serverRespondingTime() / 1000.0F, 1) + "s.";
            renderer.drawString(text, width / 2.0F - renderer.getStringWidth(text) / 2.0F + 2.0F, 20.0F, color, true);
        }
    }

    public void renderTotemHUD() {
        int width = renderer.scaledWidth;
        int height = renderer.scaledHeight;
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
            totems += mc.player.getHeldItemOffhand().getCount();
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            int iteration = 0;
            int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            renderer.drawStringWithShadow(totems + "", (x + 19 - 2 - renderer.getStringWidth(totems + "")), (y + 9), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    public void renderArmorHUD(boolean percent) {
        int width = renderer.scaledWidth;
        int height = renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
        for (ItemStack is : mc.player.inventory.armorInventory) {
            iteration++;
            if (is.isEmpty())
                continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            renderer.drawStringWithShadow(s, (x + 19 - 2 - renderer.getStringWidth(s)), (y + 9), 16777215);
            if (percent) {
                float green = (is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage();
                float red = 1.0F - green;
                int dmg = 100 - (int) (red * 100.0F);
                renderer.drawStringWithShadow(dmg + "", (x + 8 - renderer.getStringWidth(dmg + "") / 2), (y - 11), ColorUtil.toRGBA((int) (red * 255.0F), (int) (green * 255.0F), 0));
            }
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
    
    private void renderTextRadar() {
        players.clear();
        players.putAll(getTextRadarPlayers());
        if (!players.isEmpty()) {
            int yOffset = 0;
            if (renderer.getFontHeight() * players.size() + 7 > renderer.scaledHeight) {
                yOffset = (renderer.scaledHeight - renderer.getFontHeight() * players.size() - 7) / 2;
            }
            drawTextRadar(yOffset);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(AttackEntityEvent event) {
        shouldIncrement = true;
    }

    public void onLoad() {
        OyVey.commandManager.setClientMessage(getCommandMessage());
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 &&
                equals(event.getSetting().getFeature()))
            OyVey.commandManager.setClientMessage(getCommandMessage());
    }

    public String getCommandMessage() {
        return TextUtil.coloredString(commandBracket.getPlannedValue(), bracketColor.getPlannedValue()) + TextUtil.coloredString(name.getPlannedValue(), commandColor.getPlannedValue()) + TextUtil.coloredString(commandBracket2.getPlannedValue(), bracketColor.getPlannedValue());
    }

    public void drawTextRadar(int yOffset) {
        if (!players.isEmpty()) {
            int y = renderer.getFontHeight() + 7 + yOffset;
            for (Map.Entry<String, Integer> player : players.entrySet()) {
                String text = player.getKey() + " ";
                int textheight = renderer.getFontHeight() + 1;
                renderer.drawString(text, 2.0F, y, color, true);
                y += textheight;
            }
        }
    }

    public enum RenderingMode {
        Length, ABC
    }
}
