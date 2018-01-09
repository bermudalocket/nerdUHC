package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.Util;

public class BarrierExecutor extends CommandHandler {
	
	private NerdUHC plugin;
	private Util util = new Util();
	
	public BarrierExecutor(NerdUHC plugin) {
		super("barrier", "on", "off", "help");
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		Player player = (Player) sender;
		if (args[0].equalsIgnoreCase("on")) {
			player.sendMessage(ChatColor.GRAY + "Drawing barrier, please wait...");
			util.drawBarrier(player.getWorld(), 
					plugin.CONFIG.SPAWN_X,
					plugin.CONFIG.SPAWN_Y,
					plugin.CONFIG.SPAWN_Z,
					plugin.CONFIG.SPAWN_BARRIER_RADIUS,
					plugin.CONFIG.SPAWN_BARRIER_BLOCK,
					Material.AIR);
			player.sendMessage(ChatColor.GRAY + "Barrier drawn!");
		} else if (args[0].equalsIgnoreCase("off")) {
			player.sendMessage(ChatColor.GRAY + "Removing barrier, please wait...");
			util.drawBarrier(player.getWorld(), 
					plugin.CONFIG.SPAWN_X,
					plugin.CONFIG.SPAWN_Y,
					plugin.CONFIG.SPAWN_Z,
					plugin.CONFIG.SPAWN_BARRIER_RADIUS,
					Material.AIR,
					plugin.CONFIG.SPAWN_BARRIER_BLOCK);
			player.sendMessage(ChatColor.GRAY + "Barrier removed!");
		} else {
			showCommandMenu(sender);
		}
		return true;
	}
}

