package com.potato;

import com.potato.block.BlockDispenserBlock;
import net.fabricmc.api.ModInitializer;


import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.block.Blocks.*;

public class DispenserMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
        public static final Logger LOGGER = LoggerFactory.getLogger("dispensermod");
	public static int DISPENSER_LIMIT = 12;
	public static final Block BLOCK_DISPENSER = new BlockDispenserBlock(FabricBlockSettings.copyOf(OAK_PLANKS));
	public void regBlock(String BlockName, Block block, RegistryKey<ItemGroup> group) {
		Registry.register(Registries.BLOCK, new Identifier("dispensermod", BlockName), block);
		Item item = new BlockItem(block, new Item.Settings());
		Registry.register(Registries.ITEM, new Identifier("dispensermod", BlockName), item);
		ItemGroupEvents.modifyEntriesEvent(group).register(itemGroup -> itemGroup.add(item));
	}
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		regBlock("block_dispenser", BLOCK_DISPENSER, ItemGroups.REDSTONE);
		LOGGER.info("Hello Fabric world!");
	}
}
