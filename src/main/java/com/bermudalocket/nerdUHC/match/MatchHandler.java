package com.bermudalocket.nerdUHC.match;

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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.events.MatchStateChangeEvent;
import com.bermudalocket.nerdUHC.events.PlayerChangeTeamEvent;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;
import com.bermudalocket.nerdUHC.modules.UHCTeam;

public class MatchHandler implements Listener {
	
	private NerdUHC plugin;
	private ConsoleCommandSender console = Bukkit.getConsoleSender();
	
	private UHCGameMode mode;
	private long timeend;
	private boolean playersfrozen;
	private World world;
	private Location spawn;
	
	private HashMap<UUID, UHCPlayer> playerlist = new HashMap<UUID, UHCPlayer>();
	private HashMap<String, UHCTeam> teamlist = new HashMap<String, UHCTeam>();
	private UHCTeam spectator;
	
	private UHCMatchState state;
	
	public MatchHandler(NerdUHC plugin, UHCGameMode uhcgamemode) {
		this.plugin = plugin;
		this.world = Bukkit.getServer().getWorld("world");
		this.mode = uhcgamemode;
		spawn = new Location(world, plugin.CONFIG.SPAWN_X, plugin.CONFIG.SPAWN_Y, plugin.CONFIG.SPAWN_Z);
		createTeams();
	}
	
	@EventHandler
	public void onMatchStateChange(MatchStateChangeEvent e) {
		this.state = e.getState();
		if (state.equals(UHCMatchState.PREGAME)) {
			
		} else if (state.equals(UHCMatchState.INPROGRESS) || e.getLastState().equals(UHCMatchState.PREGAME)) {
			startUHC();
		} 
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
			Bukkit.getPlayer(player).setGameMode(GameMode.SURVIVAL);
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
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!plugin.match.isGameStarted()) return;
		if (playerExists(e.getEntity().getUniqueId())) {
			UHCPlayer p = getPlayer(e.getEntity().getUniqueId());
			p.setAlive(false);
			e.getEntity().setGameMode(GameMode.SPECTATOR);
		}
	}
	
	@EventHandler
	public void onMatchStateChangeScoreboard(MatchStateChangeEvent e) {
		if (e.getState().equals(UHCMatchState.DEATHMATCH)) {
			startDeathmatch();
		}
	}
	
	public UHCMatchState getMatchState() {
		return state;
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
		return (this.state.equals(UHCMatchState.INPROGRESS) || this.state.equals(UHCMatchState.DEATHMATCH)) ? true : false;
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
		String target = "";
		for (UHCPlayer p : playerlist.values()) {
			if (p.isAlive()) target += p.getName() + " ";
		}
		
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
	
	public void startUHC() {
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
			
			for (UHCPlayer p : playerlist.values()) {
				p.bukkitPlayer().setHealth(20);
				p.bukkitPlayer().setSaturation(20);
			}

			BukkitRunnable StartUHCTask = new BukkitRunnable() {
		        
				int countfrom = 10;
			
	            @Override
	            public void run() {
	            		if (countfrom == 0) {
	            			plugin.call(new MatchStateChangeEvent(UHCMatchState.INPROGRESS));
	            		    plugin.getServer().dispatchCommand(console, spreadplayerscmd);
	            		    Bukkit.getOnlinePlayers().forEach(player -> 
        		    				player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10, 1));
	            			this.cancel();
	            		} else {
	            			Bukkit.getOnlinePlayers().forEach(player -> 
	            				player.sendTitle(ChatColor.RED + "The UHC starts in", countfrom + " seconds", 2, 16, 2));
	            		}
	            		countfrom--;
	            }
	        };
	        StartUHCTask.runTaskTimer(plugin, 1, 20);
	}

}
