package me.alpha432.oyvey.features.modules.combat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Surround
        extends Module {
    public static boolean isPlacing = false;
    private final Setting<Integer> blocksPerTick = register(new Setting<Integer>("BlocksPerTick", 12, 1, 20));
    private final Setting<Integer> delay = register(new Setting<Integer>("Delay", 0, 0, 250));
    private final Setting<Boolean> noGhost = register(new Setting<Boolean>("Packet", false));
    private final Setting<Boolean> center = register(new Setting<Boolean>("TPCenter", false));
    private final Setting<Boolean> rotate = register(new Setting<Boolean>("Rotate", true));
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    private final Set<Vec3d> extendingBlocks = new HashSet<Vec3d>();
    private final Map<BlockPos, Integer> retries = new HashMap<BlockPos, Integer>();
    private int isSafe;
    private BlockPos startPos;
    private boolean didPlace = false;
    private boolean switchedItem;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements = 0;
    private int extenders = 1;
    private int obbySlot = -1;
    private boolean offHand = false;

    public Surround() {
        super("Surround", "Surrounds you with soma", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (Surround.fullNullCheck()) {
            disable();
        }
        lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        startPos = EntityUtil.getRoundedBlockPos(Surround.mc.player);
        if (center.getValue().booleanValue()) {
            OyVey.positionManager.setPositionPacket((double) startPos.getX() + 0.5, startPos.getY(), (double) startPos.getZ() + 0.5, true, true, true);
        }
        retries.clear();
        retryTimer.reset();
    }

    @Override
    public void onTick() {
        doFeetPlace();
    }

    @Override
    public void onDisable() {
        if (Surround.nullCheck()) {
            return;
        }
        isPlacing = false;
        isSneaking = EntityUtil.stopSneaking(isSneaking);
    }

    @Override
    public String getDisplayInfo() {
        switch (isSafe) {
            case 0: {
                return ChatFormatting.RED + "U NOT GOOD";
            }
            case 1: {
                return ChatFormatting.YELLOW + "U GOOD";
            }
        }
        return ChatFormatting.GREEN + "U VERRY GOOD UR SAFE";
    }

    private void doFeetPlace() {
        if (check()) {
            return;
        }
        if (!EntityUtil.isSafe(Surround.mc.player, 0, true, false)) {
            isSafe = 0;
            placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray(Surround.mc.player, 0, true), true, false, false);
        } else if (!EntityUtil.isSafe(Surround.mc.player, -1, false, false)) {
            isSafe = 1;
            placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray(Surround.mc.player, -1, false), false, false, true);
        } else {
            isSafe = 2;
        }
        processExtendingBlocks();
        if (didPlace) {
            timer.reset();
        }
    }

    private void processExtendingBlocks() {
        if (extendingBlocks.size() == 2 && extenders < 1) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;
            Iterator<Vec3d> iterator = extendingBlocks.iterator();
            while (iterator.hasNext()) {
                Vec3d vec3d;
                array[i] = vec3d = iterator.next();
                ++i;
            }
            int placementsBefore = placements;
            if (areClose(array) != null) {
                placeBlocks(areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(areClose(array), 0, true), true, false, true);
            }
            if (placementsBefore < placements) {
                extendingBlocks.clear();
            }
        } else if (extendingBlocks.size() > 2 || extenders >= 1) {
            extendingBlocks.clear();
        }
    }

    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        for (Vec3d vec3d : vec3ds) {
            for (Vec3d pos : EntityUtil.getUnsafeBlockArray(Surround.mc.player, 0, true)) {
                if (!vec3d.equals(pos)) continue;
                ++matches;
            }
        }
        if (matches == 2) {
            return Surround.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        }
        return null;
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
        boolean gotHelp = true;
        block5:
        for (Vec3d vec3d : vec3ds) {
            gotHelp = true;
            BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position, false)) {
                case 1: {
                    if (retries.get(position) == null || retries.get(position) < 4) {
                        placeBlock(position);
                        retries.put(position, retries.get(position) == null ? 1 : retries.get(position) + 1);
                        retryTimer.reset();
                        continue block5;
                    }
                    if (OyVey.speedManager.getSpeedKpH() != 0.0 || isExtending || extenders >= 1) continue block5;
                    placeBlocks(Surround.mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(Surround.mc.player.getPositionVector().add(vec3d), 0, true), hasHelpingBlocks, false, true);
                    extendingBlocks.add(vec3d);
                    ++extenders;
                    continue block5;
                }
                case 2: {
                    if (!hasHelpingBlocks) continue block5;
                    gotHelp = placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                }
                case 3: {
                    if (gotHelp) {
                        placeBlock(position);
                    }
                    if (!isHelping) continue block5;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean check() {
        if (Surround.nullCheck()) {
            return true;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && eChestSot == -1) {
            toggle();
        }
        offHand = InventoryUtil.isBlock(Surround.mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
        isPlacing = false;
        didPlace = false;
        extenders = 1;
        placements = 0;
        obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (isOff()) {
            return true;
        }
        if (retryTimer.passedMs(2500L)) {
            retries.clear();
            retryTimer.reset();
        }
        if (obbySlot == -1 && !offHand && echestSlot == -1) {
            Command.sendMessage("<" + getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
            disable();
            return true;
        }
        isSneaking = EntityUtil.stopSneaking(isSneaking);
        if (Surround.mc.player.inventory.currentItem != lastHotbarSlot && Surround.mc.player.inventory.currentItem != obbySlot && Surround.mc.player.inventory.currentItem != echestSlot) {
            lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        }
        if (!startPos.equals(EntityUtil.getRoundedBlockPos(Surround.mc.player))) {
            disable();
            return true;
        }
        return !timer.passedMs(delay.getValue().intValue());
    }

    private void placeBlock(BlockPos pos) {
        if (placements < blocksPerTick.getValue()) {
            int originalSlot = Surround.mc.player.inventory.currentItem;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                toggle();
            }
            isPlacing = true;
            Surround.mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
            Surround.mc.playerController.updateController();
            isSneaking = BlockUtil.placeBlock(pos, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, rotate.getValue(), noGhost.getValue(), isSneaking);
            Surround.mc.player.inventory.currentItem = originalSlot;
            Surround.mc.playerController.updateController();
            didPlace = true;
            ++placements;
        }
    }
}