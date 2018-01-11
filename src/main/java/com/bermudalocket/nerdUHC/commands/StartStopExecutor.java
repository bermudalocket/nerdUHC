package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.Match.UHCGameMode;

public class StartStopExecutor extends CommandHandler {
	
	private NerdUHC plugin;
	
	private CommandSender console = Bukkit.getConsoleSender();
	
	public StartStopExecutor(NerdUHC plugin) {
		super("uhc", "start", "stop", "help");
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("start")) {
				if (!plugin.match.isGameStarted()) {
					String target = "@a[x=" + plugin.CONFIG.SPAWN_X + 
							",y=" + plugin.CONFIG.SPAWN_Y + 
							",z=" + plugin.CONFIG.SPAWN_Z + 
							",r=" + plugin.CONFIG.SPAWN_BARRIER_RADIUS + "]";
					
					String spreadplayers = "spreadplayers " + 
											plugin.CONFIG.SPAWN_X + " " + 
											plugin.CONFIG.SPAWN_Z + " " + 
											plugin.CONFIG.SPREAD_DIST_BTWN_PLAYERS + " " +
											plugin.CONFIG.SPREAD_DIST_FROM_SPAWN + " ";
					if (plugin.match.getGameMode().equals(UHCGameMode.TEAM)) {
						spreadplayers = spreadplayers + plugin.CONFIG.SPREAD_RESPECT_TEAMS + " ";
					} else {
						spreadplayers = spreadplayers + !plugin.CONFIG.SPREAD_RESPECT_TEAMS + " ";
					}
					final String spreadplayerscmd = spreadplayers + target;
					
					String saturation = "effect " + target + " 23 1 10";
					String fullhealth = "effect " + target + " 6 1 10";
					
					BukkitRunnable task = new BukkitRunnable() {
				        
						int countfrom = 5;
					
			            @Override
			            public void run() {
			            	
			            		if (countfrom == 0) {
			            			
			            		    plugin.getServer().dispatchCommand(console, spreadplayerscmd);
			    					plugin.getServer().dispatchCommand(console, saturation);
			    					plugin.getServer().dispatchCommand(console, fullhealth);
			    					
			            		    Bukkit.getOnlinePlayers().forEach(player -> 
	            		    				player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10, 1));
			            		    
			    					plugin.match.setGameStarted(true);
			    					sender.sendMessage(ChatColor.GRAY + "UHC started!");
			            			this.cancel();
			            			
			            		} else {
			            			
			            			Bukkit.getOnlinePlayers().forEach(player -> 
			            				player.sendTitle(ChatColor.RED + "The UHC will commence in ", countfrom + " seconds", 2, 16, 2));
			            		}
			            		
			            		countfrom--;
			            }
			        };
			        task.runTaskTimer(plugin, 1, 20);
				} else {
					sender.sendMessage(ChatColor.RED + "There's already a UHC running!");
				}
			} else if (args[0].equalsIgnoreCase("stop")) {
				plugin.match.setGameStarted(false);
				sender.sendMessage(ChatColor.RED + "UHC stopped!");
			}
		}
		return true;
	}
	
}

