package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;

public class StartStopExecutor extends CommandHandler {
	
	private CommandSender console = NerdUHC.PLUGIN.getServer().getConsoleSender();
	
	public StartStopExecutor() {
		super("uhc", "start", "stop", "help");
	}
	
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
					
					System.out.println(spreadplayers);
					
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
			    					sender.sendMessage("UHC started!");
			            			this.cancel();
			            			
			            		} else {
			            			
			            			Bukkit.getOnlinePlayers().forEach(player -> 
			            				player.sendTitle("The UHC will commence in ", countfrom + " seconds", 2, 16, 2));
			            		}
			            		
			            		countfrom--;
			            }
			        };
			        task.runTaskTimer(NerdUHC.PLUGIN, 1, 20);
				} else {
					sender.sendMessage("There's already a UHC running!");
				}
			} else if (args[0].equalsIgnoreCase("stop")) {
				NerdUHC.setGameStarted(false);
			}
		}
		return true;
	}
}

