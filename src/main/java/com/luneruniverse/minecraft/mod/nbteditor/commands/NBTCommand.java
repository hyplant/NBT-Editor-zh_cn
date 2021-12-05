package com.luneruniverse.minecraft.mod.nbteditor.commands;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.luneruniverse.minecraft.mod.nbteditor.screens.NBTEditorScreen;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class NBTCommand implements RegisterableCommand {
	
	@Override
	public LiteralCommandNode<FabricClientCommandSource> register(boolean dedicated) {
		return ClientCommandManager.DISPATCHER.register(literal("nbteditor").executes(context -> {
			ClientPlayerEntity player = context.getSource().getPlayer();
			Map.Entry<Hand, ItemStack> heldItem = MainUtil.getHeldItem(player);
			Hand hand = heldItem.getKey();
			ItemStack item = heldItem.getValue();
			
			MinecraftClient.getInstance().setScreen(new NBTEditorScreen(item, hand));
			
			return Command.SINGLE_SUCCESS;
		}));
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("nbt", "edit");
	}
	
	@Override
	public EnvType getSide() {
		return EnvType.CLIENT;
	}
	
}