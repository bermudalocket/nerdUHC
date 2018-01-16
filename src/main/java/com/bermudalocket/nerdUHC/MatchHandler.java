package com.bermudalocket.nerdUHC;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.events.PlayerChangeTeamEvent;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;
import com.bermudalocket.nerdUHC.modules.UHCTeam;

public class MatchHandler implements Listener {
	
	private NerdUHC plugin;
	private ConsoleCommandSender console = Bukkit.getConsoleSender();
	
	private UHCGameMode mode;
	private boolean inprogress = false;
	private long timeend;
	private boolean playersfrozen;
	private World world;
	private int timelimit;
	private Location spawn;
	
	private HashMap<UUID, UHCPlayer> playerlist = new HashMap<UUID, UHCPlayer>();
	private HashMap<String, UHCTeam> teamlist = new HashMap<String, UHCTeam>();
	private UHCTeam spectator;
	
	public MatchHandler(NerdUHC plugin, UHCGameMode uhcgamemode, Integer timelimit) {
		this.plugin = plugin;
		this.world = Bukkit.getServer().getWorld("world");
		this.mode = uhcgamemode;
		this.timelimit = timelimit;
		spawn = new Location(world, plugin.CONFIG.SPAWN_X, plugin.CONFIG.SPAWN_Y, plugin.CONFIG.SPAWN_Z);
		createTeams();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		UUID player = e.getPlayer().getUniqueId();
		if (playerExists(player)) {
			UHCPlayer p = getPlayer(player);
			Player pl = Bukkit.getPlayer(player);
			pl.setPlayerListName(p.getColor() + p.getName());
			pl.setDisplayName(p.getColor() + p.getName() + ChatColor.WHITE);
		} else {
			registerPlayer(player);
		}
	}
	
	@EventHandler
	public void onPlayerChangeTeam(PlayerChangeTeamEvent e) {
		UHCPlayer p = e.getPlayer();
		UHCTeam newteam = e.getTeam();
		
		e.getPlayer().setTeam(newteam);
		newteam.add(p);
		
		if (e.getTeam().getName().equalsIgnoreCase("SPECTATOR")) {
			p.bukkitPlayer().setGameMode(GameMode.SPECTATOR);
		}
		
		if (e.getOldTeam() != null) {
			e.getOldTeam().remove(p);
			
			if (e.getOldTeam().getName().equalsIgnoreCase("SPECTATOR")) { 
				p.bukkitPlayer().setGameMode(GameMode.SURVIVAL);
			}
		}
		p.bukkitPlayer().sendMessage("You joined the " + newteam.getName() + " team!");
		plugin.scoreboardHandler.update();
	}
	
	private void createTeams() {
		switch (this.mode) {
			default:
			case SOLO:
				UHCTeam alive = new UHCTeam(this, plugin.CONFIG.ALIVE_TEAM_NAME, ChatColor.WHITE, plugin.CONFIG.MAX_TEAM_SIZE, plugin.CONFIG.ALLOW_FRIENDLY_FIRE);
				UHCTeam dead = new UHCTeam(this, plugin.CONFIG.DEAD_TEAM_NAME, ChatColor.GRAY, plugin.CONFIG.MAX_TEAM_SIZE, false);
				teamlist.put(plugin.CONFIG.ALIVE_TEAM_NAME, alive);
				teamlist.put(plugin.CONFIG.DEAD_TEAM_NAME, dead);
				break;
			case TEAM:
				plugin.CONFIG.getRawTeamList().forEach(team -> {
					String teamname = team.get("name").toString().toUpperCase();
					String teamcolor = team.get("color").toString();
					ChatColor color;
					plugin.getLogger().info("Creating " + teamname);
					try {
						color = ChatColor.valueOf(teamcolor);
					} catch (Exception f) {
						color = ChatColor.STRIKETHROUGH;
						plugin.getLogger().info("Config error: Invalid color option for team " + teamname);
					}
					teamlist.put(teamname, new UHCTeam(this, teamname, color, plugin.CONFIG.MAX_TEAM_SIZE, plugin.CONFIG.ALLOW_FRIENDLY_FIRE));
				});
				break;
		}
		this.spectator = new UHCTeam(this, "SPECTATOR", ChatColor.GRAY, 999, false);
		teamlist.put("SPECTATOR", spectator);
	}
	
	/*
	 * 		Players
	 */
	
	public void registerPlayer(UUID player) {
		playerlist.put(player, new UHCPlayer(player));
	}
	
	public void registerPlayer(UUID player, UHCTeam team) {
		playerlist.put(player, new UHCPlayer(player, team));
	}
	
	public void unregisterPlayer(UUID player) {
		playerlist.remove(player);
	}
	
	public boolean playerExists(UUID player) {
		return playerlist.containsKey(player);
	}
	
	public UHCPlayer getPlayer(UUID player) {
		return playerlist.get(player);
	}
	
	public UHCPlayer getPlayer(String player) {
		return playerlist.values().stream().filter(p -> p.getName().equalsIgnoreCase(player)).collect(Collectors.toList()).get(0);
	}
	
	public void freezePlayers(boolean state) {
		if (state) {
			playersfrozen = true;
		} else {
			playersfrozen = false;
		}
	}
	
	public boolean arePlayersFrozen() {
		return playersfrozen;
	}
	
	/*
	 * 		Gamerules and other match parameters
	 */
	
	public Location getSpawn() {
		return spawn;
	}
	
	public void setGameRules() {
		plugin.CONFIG.GAMERULES.forEach(gamerule -> {
			String rule = gamerule.keySet().toArray()[0].toString();
			String value = gamerule.values().toArray()[0].toString();
			world.setGameRuleValue(rule, value);
		});
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public void setGameMode(UHCGameMode mode) {
		this.mode = mode;
	}
	
	public UHCGameMode getGameMode() {
		return mode;
	}
	
	public boolean isGameStarted() { 
		return inprogress;
	}
	
	public void setGameStarted(Boolean bool) {
		inprogress = bool;
		if (inprogress) { 
			timeend = System.currentTimeMillis() + plugin.CONFIG.MATCH_DURATION*60*1000;
			plugin.scoreboardHandler.MatchTimer.runTaskTimer(plugin, 0, 20);
		}
	}
	
	public long getTimeEnd() {
		return timeend;
	}
	
	public void extendTime(int sec) {
		timeend += sec*1000;
	}
	
	/*
	 * 		Teams
	 */
	
	public UHCTeam getTeam(String team) {
		return teamlist.get(team.toUpperCase());
	}
	
	public Collection<UHCTeam> getTeams() {
		return teamlist.values();
	}
	
	public boolean teamExists(String team) {
		for (UHCTeam t : teamlist.values()) {
			if (t.getName().equalsIgnoreCase(team)) return true;
		}
		return false;
	}
	
	public boolean allowTeams() {
		if (mode.equals(UHCGameMode.TEAM)) {
			return true;
		} else {
			return false;
		}
	}
	
	public UHCTeam getSpectatorTeam() {
		return this.spectator;
	}
	
	/*
	 * Start, stop
	 */
	
	public void startDeathmatch() {
		String target = "@a";
		
		String spreadplayers = "spreadplayers " + 
								plugin.CONFIG.SPAWN_X + " " + 
								plugin.CONFIG.SPAWN_Z + " " + 
								plugin.CONFIG.DEATHMATCH_DIST_BTWN_PLAYERS + " " +
								plugin.CONFIG.DEATHMATCH_SPREAD_DIST_FROM_SPAWN + " ";
		if (plugin.match.getGameMode().equals(UHCGameMode.TEAM)) {
			spreadplayers = spreadplayers + plugin.CONFIG.SPREAD_RESPECT_TEAMS + " ";
		} else {
			spreadplayers = spreadplayers + !plugin.CONFIG.SPREAD_RESPECT_TEAMS + " ";
		}
		final String spreadplayerscmd = spreadplayers + target;
		
		plugin.getServer().dispatchCommand(console, spreadplayerscmd);
		
		Bukkit.getOnlinePlayers().forEach(player -> 
		player.sendTitle(ChatColor.RED + "Deathmatch!", null, 15, 60, 15));
		
	}
	
	public boolean stopUHC() {
		if (!plugin.match.isGameStarted()) {
			return false;
		} else {
				BukkitRunnable task = new BukkitRunnable() {
		        
				int countfrom = 10;
				ChatColor color = ChatColor.WHITE;
			
	            @Override
	            public void run() {
	            		if (countfrom == 0) {
	            			//plugin.match.setPVP(false);
	            			//plugin.match.freezePlayers(true);
	            		    Bukkit.getOnlinePlayers().forEach(player -> 
        		    				player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 10, 1));
	            		    if (plugin.CONFIG.DO_DEATHMATCH) {
	            		    		startDeathmatch();
	            		    } else {
	            		    		// cleanUp();
	            		    }
	    					plugin.match.setGameStarted(false);
	            			this.cancel();
	            		} else {
	            			if (countfrom <= 3) color = ChatColor.RED;
	            			Bukkit.getOnlinePlayers().forEach(player -> 
	            				player.sendTitle(color + "" + countfrom, null, 10, 0, 10));
	            		}
	            		countfrom--;
	            }
	        };
	        task.runTaskTimer(plugin, 1, 20);
	        return true;
		}
	}
	
	public boolean startUHC() {
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
			
			plugin.scoreboardHandler.hideSidebar();
			
			BukkitRunnable task = new BukkitRunnable() {
		        
				int countfrom = 10;
			
	            @Override
	            public void run() {
	            		if (countfrom == 0) {
	            		    plugin.getServer().dispatchCommand(console, spreadplayerscmd);
	    					plugin.getServer().dispatchCommand(console, saturation);
	    					plugin.getServer().dispatchCommand(console, fullhealth);
	            		    Bukkit.getOnlinePlayers().forEach(player -> 
        		    				player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10, 1));
	            		    plugin.match.setGameStarted(true);
	            		    plugin.scoreboardHandler.showSidebar();
	            		    plugin.scoreboardHandler.showTeamsKillsAndTimer();
	            			this.cancel();
	            		} else {
	            			Bukkit.getOnlinePlayers().forEach(player -> 
	            				player.sendTitle(ChatColor.RED + "The UHC starts in", countfrom + " seconds", 2, 16, 2));
	            		}
	            		countfrom--;
	            }
	        };
	        task.runTaskTimer(plugin, 1, 20);
	        return true;
		} else {
			return false;
		}
	}

}
