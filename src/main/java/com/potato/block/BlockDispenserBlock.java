package com.potato.block;

import com.potato.DispenserMod;
import com.potato.dispenserbehaviour.BlockDispenserBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class BlockDispenserBlock extends DropperBlock {
    public BlockDispenserBlock(Settings settings) {
        super(settings);
    }
    private final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();
    public void dispenseItem(ServerWorld world, BlockPointer blockPointer, BlockPos pos, DispenserBlockEntity dispenserBlockEntity, ItemStack itemStack, int i){
        Direction direction = world.getBlockState(pos).get(FACING);
        Inventory inventory = HopperBlockEntity.getInventoryAt(world, pos.offset(direction));
        ItemStack itemStack2;
        if (inventory == null) {
            itemStack2 = BEHAVIOR.dispense(blockPointer, itemStack);
        } else {
            itemStack2 = HopperBlockEntity.transfer(dispenserBlockEntity, inventory, itemStack.copy().split(1), direction.getOpposite());
            if (itemStack2.isEmpty()) {
                itemStack2 = itemStack.copy();
                itemStack2.decrement(1);
            } else {
                itemStack2 = itemStack.copy();
            }
        }
        dispenserBlockEntity.setStack(i, itemStack2);
    }
    @Override
    protected void dispense(ServerWorld world, BlockState state, BlockPos pos) {
        DispenserBlockEntity dispenserBlockEntity = world.getBlockEntity(pos, BlockEntityType.DROPPER).orElse(null);
        if (dispenserBlockEntity == null) {
            DispenserMod.LOGGER.warn("Ignoring dispensing attempt for Dropper without matching block entity at {}", pos);
        } else {
            BlockPointer blockPointer = new BlockPointer(world, pos, state, dispenserBlockEntity);
            int i = dispenserBlockEntity.chooseNonEmptySlot(world.random);
            if (i < 0) {
                world.syncWorldEvent(1001, pos, 0);
            } else {
                ItemStack itemStack = dispenserBlockEntity.getStack(i);
                if(itemStack.isEmpty()) return;
                if(!(itemStack.getItem() instanceof BlockItem)){
                    dispenseItem(world, blockPointer, pos, dispenserBlockEntity, itemStack, i);
                }else if(((BlockItem)itemStack.getItem()).getBlock() instanceof DoorBlock){
                    dispenseItem(world, blockPointer, pos, dispenserBlockEntity, itemStack, i);
                }
                else{
                    BlockDispenserBehaviour dispenserBehavior = new BlockDispenserBehaviour();
                    ArrayList<BlockState> arr = new ArrayList<>();
                    BlockPos position = BlockDispenserBehaviour.getOutputLocation(blockPointer);
                    BlockPos offset = BlockDispenserBehaviour.getOffset(blockPointer);
                    for(int itr = 0; itr < DispenserMod.DISPENSER_LIMIT; itr++){
                        if(world.getBlockState(position).isAir()) break;
                        arr.add(world.getBlockState(position));
                        position = new BlockPos(position.getX() + offset.getX(), position.getY() + offset.getY(), position.getZ() + offset.getZ());
                    }
                    if(arr.size() == DispenserMod.DISPENSER_LIMIT){
                        dispenseItem(world, blockPointer, pos, dispenserBlockEntity, itemStack, i);
                    }else{
                        position = BlockDispenserBehaviour.getOutputLocation(blockPointer);
                        for (BlockState blockState : arr) {
                            position = new BlockPos(position.getX() + offset.getX(), position.getY() + offset.getY(), position.getZ() + offset.getZ());
                            world.setBlockState(position, blockState);
                        }
                        dispenserBlockEntity.setStack(i, dispenserBehavior.dispenseSilently(blockPointer, itemStack));
                    }
                }
            }
        }
    }
}
