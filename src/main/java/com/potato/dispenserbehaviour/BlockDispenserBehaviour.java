package com.potato.dispenserbehaviour;


import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static net.minecraft.block.DispenserBlock.FACING;

public class BlockDispenserBehaviour extends ItemDispenserBehavior {
    public BlockDispenserBehaviour(){}
    public static BlockPos getOffset(BlockPointer pointer){
        Direction direction = pointer.state().get(FACING);
        return new BlockPos(direction.getOffsetX(), direction.getOffsetY(),direction.getOffsetZ());
    }
    public static BlockPos getOutputLocation(BlockPointer pointer) {
        return pointer.pos().add(getOffset(pointer));
    }
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        World world = pointer.world();
        BlockPos new_pos = getOutputLocation(pointer);
        BlockState block = ((BlockItem)(stack.getItem())).getBlock().getDefaultState();
        world.setBlockState(new_pos, block);
        stack.decrement(1);
        return stack;
    }
}
