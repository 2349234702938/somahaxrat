package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.EventStage;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockDestructionEvent extends EventStage {
    BlockPos block;
    public BlockDestructionEvent(BlockPos block){
        super();
        block = block;
    }

    public BlockPos getBlockPos(){
        return block;
    }
}