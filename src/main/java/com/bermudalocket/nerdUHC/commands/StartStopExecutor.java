package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;

/////////////////////////////////////////////////////////////////////////////
//
//	Start Stop Executor
//
//

public class StartStopExecutor extends CommandHandler {
	
	// ********************************************
	// register subcommands
	// ********************************************
	public StartStopExecutor() {
		super("uhc", "start", "stop", "help");
	}
	
	// ********************************************
	// 		/uhc [start|stop]
	// ********************************************
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("start")) {
				if (!NerdUHC.isGameStarted()) {
					String target = "@a[x=" + NerdUHC.CONFIG.SPAWN_X + 
							",y=" + NerdUHC.CONFIG.SPAWN_Y + 
							",z=" + NerdUHC.CONFIG.SPAWN_Z + 
							",r=" + NerdUHC.CONFIG.SPAWN_BARRIER_RADIUS + "]";
					
					String spreadplayers = "spreadplayers " + 
											NerdUHC.CONFIG.SPAWN_X + " " + 
											NerdUHC.CONFIG.SPAWN_Z + " " + 
											NerdUHC.CONFIG.SPREAD_DIST_BTWN_PLAYERS + " " +
											NerdUHC.CONFIG.SPREAD_DIST_FROM_SPAWN + " ";
					if (NerdUHC.getGameMode().equals(NerdUHC.UHCGameMode.TEAM)) {
						spreadplayers = spreadplayers + NerdUHC.CONFIG.SPREAD_RESPECT_TEAMS + " ";
					} else {
						spreadplayers = spreadplayers + !NerdUHC.CONFIG.SPREAD_RESPECT_TEAMS + " ";
					}
					final String spreadplayerscmd = spreadplayers + target;
					
					String saturation = "effect " + target + " 23 1 4";
					String fullhealth = "effect " + target + " 6 1 4";
					
					BukkitRunnable task = new BukkitRunnable() {
				        
						int countfrom = 5;
					
			            @Override
			            public void run() {
			            	
			            		if (countfrom == 0) {
			            			
			            		    NerdUHC.PLUGIN.getServer().dispatchCommand(console, spreadplayerscmd);
			    					NerdUHC.PLUGIN.getServer().dispatchCommand(console, saturation);
			    					NerdUHC.PLUGIN.getServer().dispatchCommand(console, fullhealth);
			    					
			            		    Bukkit.getOnlinePlayers().forEach(player -> 
	            		    				player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10, 1));
			            		    
			    					NerdUHC.setGameStarted(true);
			    					sender.sendMessage(ChatColor.GRAY + "UHC started!");
			            			this.cancel();
			            			
			            		} else {
			            			
			            			Bukkit.getOnlinePlayers().forEach(player -> 
			            				player.sendTitle(ChatColor.RED + "The UHC will commence in ", countfrom + " seconds", 2, 16, 2));
			            		}
			            		
			            		countfrom--;
			            }
			        };
			        task.runTaskTimer(NerdUHC.PLUGIN, 1, 20);
				} else {
					sender.sendMessage(ChatColor.RED + "There's already a UHC running!");
				}
			} else if (args[0].equalsIgnoreCase("stop")) {
				NerdUHC.setGameStarted(false);
				sender.sendMessage(ChatColor.RED + "UHC stopped!");
			}
		}
		return true;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Fields
	//
	//
	
	// ********************************************
	// gets the server's console
	// ********************************************
	private CommandSender console = NerdUHC.PLUGIN.getServer().getConsoleSender();
}

