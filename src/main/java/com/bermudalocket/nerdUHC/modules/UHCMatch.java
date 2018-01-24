package com.bermudalocket.nerdUHC.modules;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.CombatLogger;
import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.listeners.GameListener;
import com.bermudalocket.nerdUHC.listeners.PregameListener;
import com.bermudalocket.nerdUHC.match.MatchEndTimer;
import com.bermudalocket.nerdUHC.match.MatchStartCountdownTimer;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardTimer;

public class UHCMatch {

	private NerdUHC plugin;
	private UHCMatchState state;
	private UHCGameMode mode;
	private World world;
	
	private Set<UUID> players;
	private Scoreboard scoreboard;
	
	private long duration;
	private boolean frozen;
	private boolean active;
	private boolean allowPVP;
	
	private CombatLogger combatLogger;
	private UHCUtils util;
	
	private PregameListener pregameListener;
	private GameListener gameListener;
	
	private MatchStartCountdownTimer matchStartCountdownTimer;
	private ScoreboardTimer scoreboardTimer;
	private MatchEndTimer matchEndTimer;
	
	public UHCMatch(NerdUHC plugin, UHCMatch previousmatch) {
		this.plugin = plugin;
		this.state = UHCMatchState.PREGAME;
		this.mode = plugin.CONFIG.UHC_GAME_MODE;
		this.world = plugin.CONFIG.WORLD; // tba
		
		players = new HashSet<UUID>();
		if (previousmatch != null) {
			this.active = false;
		} else {
			this.active = true;
		}
		plugin.scoreboardHandler.createScoreboard(this);
		
		this.duration = 0;
		this.frozen = false;
		this.allowPVP = false;
		
		this.combatLogger = new CombatLogger(plugin, this);
		this.util = new UHCUtils(plugin, this);
		
		pregameListener = new PregameListener(plugin);
		plugin.getServer().getPluginManager().registerEvents(pregameListener, plugin);
	}
	
	public UHCMatch(NerdUHC plugin) {
		this(plugin, null);
	}
	
	public CombatLogger getCombatLogger() {
		return combatLogger;
	}
	
	public UHCUtils getBarrier() {
		return util;
	}
	
	// Mode and state
	
	public void setActive() {
		this.active = true;
		plugin.scoreboardHandler.showTeamCountCapacity(this);
		plugin.scoreboardHandler.forceHealthUpdates();
	}
	
	public boolean isActive() {
		return active;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Location getSpawn() {
		return plugin.CONFIG.SPAWN;
	}
	
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	public void setScoreboard(Scoreboard board) {
		this.scoreboard = board;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public void extendTime(int sec) {
		duration += sec*1000;
	}
	
	public void changeGameMode(UHCGameMode newmode) {
		mode = newmode;
	}
	
	public UHCGameMode getGameMode() {
		return mode;
	}
	
	public UHCMatchState getMatchState() {
		return state;
	}
	
	public void freeze() {
		if (frozen) {
			frozen = false;
			allowPVP = true;
		} else {
			frozen = true;
			allowPVP = false;
		}
	}
	
	public boolean isFrozen() {
		return frozen;
	}
	
	public boolean allowPVP() {
		return allowPVP;
	}
	
	// Teams and players

	public void addPlayer(Player p) {
		players.add(p.getUniqueId());
	}
	
	public boolean isPlayerInMatch(UUID p) {
		return players.contains(p);
	}
	
	public Team getTeamForPlayer(Player p) {
		for (Team t : getScoreboard().getTeams()) {
			for (String s : t.getEntries()) {
				if (s.equalsIgnoreCase(p.getName())) return t;
			}
		}
		return null;
	}
	
	public Set<UUID> getPlayers() {
		return players;
	}

	public int numberOfAlivePlayers() {
		int i = 0;
		for (UUID uuid : players) {
			Player p = Bukkit.getPlayer(uuid);
			if (!p.isDead() && !p.getGameMode().equals(GameMode.SPECTATOR)) i++;
		}
		return i;
	}
	
	// Deprecated method: Team#getPlayers
	@SuppressWarnings("deprecation")
	public Team getLastTeamStanding() {
		Team last = null;
		for (Team t : scoreboard.getTeams()) {
			int i = 0;
			for (OfflinePlayer player : t.getPlayers()) {
				if (!player.isOnline()) continue;
				Player p = (Player) player;
				if (!p.isDead() && !p.getGameMode().equals(GameMode.SPECTATOR)) i++;
			}
			if (i > 0) {
				if (last == null) {
					return t;
				}
				last = t;
			}
		}
		return null;
	}
	
	// Starters and stoppers for different phases of the match
	
	public void beginMatchStartCountdown() {
		this.state = UHCMatchState.TRANSITION;
		HandlerList.unregisterAll(pregameListener);
		gameListener = new GameListener(plugin, this);
		plugin.getServer().getPluginManager().registerEvents(gameListener, plugin);
		matchStartCountdownTimer = new MatchStartCountdownTimer(this);
		matchStartCountdownTimer.runTaskTimer(plugin, 1, 20);
	}
	
	public void beginMatch() {
		this.state = UHCMatchState.INPROGRESS;
		this.duration = System.currentTimeMillis() + plugin.CONFIG.MATCH_DURATION*60000;
		matchStartCountdownTimer = null;
		
		plugin.scoreboardHandler.pruneTeams(this);

		String spreadplayers = "spreadplayers " + getSpawn().getX();
		spreadplayers += " " + getSpawn().getZ();
		spreadplayers += " " + plugin.CONFIG.SPREAD_DIST_BTWN_PLAYERS;
		spreadplayers += " " + plugin.CONFIG.SPREAD_DIST_FROM_SPAWN;
		if (mode == UHCGameMode.TEAM) {
			spreadplayers +=  " " + plugin.CONFIG.SPREAD_RESPECT_TEAMS;
		} else {
			spreadplayers += !plugin.CONFIG.SPREAD_RESPECT_TEAMS;
		}
		spreadplayers += " ";

		for (UUID player : players) {
			Player p = Bukkit.getPlayer(player);
			if (p == null) continue;
			
			if (getTeamForPlayer(p) != null) {
				spreadplayers += p.getName() + " ";
			} else {
				p.setGameMode(GameMode.SPECTATOR);
			}
			p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20 * 1, 100, true));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 1, 100, true));
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60, 100, true));
		}

		plugin.CONFIG.WORLD.setTime(0);
		plugin.CONFIG.WORLD.setStorm(false);
		plugin.CONFIG.WORLD.setDifficulty(Difficulty.HARD);
		
		allowPVP = true;

		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), spreadplayers);
		
		UHCSound.MATCHSTART.playSound();
		
		util.drawBarrier(false);
		
		scoreboardTimer = new ScoreboardTimer(this);
		scoreboardTimer.runTaskTimer(plugin, 0, 20);

		plugin.scoreboardHandler.showKills(this);
	}
	
	public void beginMatchEndTransition() {
		this.state = UHCMatchState.TRANSITION;
		freeze();
		allowPVP = false;
		
		scoreboardTimer.cancel();
		if (numberOfAlivePlayers() > 1) {
			if (plugin.CONFIG.DO_DEATHMATCH) {
				beginDeathmatch();
			}
		} else {
			endMatch();
		}
	}
	
	public void beginDeathmatch() {
		this.state = UHCMatchState.DEATHMATCH;
		
		allowPVP = true;
		
		String target = "";
		for (UUID player : players) {
			Player p = Bukkit.getPlayer(player);
			if (!p.isDead()) {
				target += p.getName() + " ";
			}
		}

		String spreadplayers = "spreadplayers " + plugin.CONFIG.SPAWN_X;
		spreadplayers += " " + plugin.CONFIG.SPAWN_Z;
		spreadplayers += " " + plugin.CONFIG.DEATHMATCH_DIST_BTWN_PLAYERS;
		spreadplayers += " " + plugin.CONFIG.DEATHMATCH_SPREAD_DIST_FROM_SPAWN;
		if (mode == UHCGameMode.TEAM) {
			spreadplayers +=  " " + plugin.CONFIG.SPREAD_RESPECT_TEAMS;
		} else {
			spreadplayers += !plugin.CONFIG.SPREAD_RESPECT_TEAMS;
		}

		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), spreadplayers + target);
		scoreboard.getObjective("main").setDisplayName(ChatColor.BOLD + "" + ChatColor.RED + "Deathmatch");
		Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + "Deathmatch!", null, 15, 60, 15));
	}
	
	public void endMatch() {
		this.state = UHCMatchState.END;
		matchEndTimer = new MatchEndTimer(plugin, this);
		allowPVP = false;
		
		Set<Player> winners = new HashSet<Player>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.isDead() && !p.getGameMode().equals(GameMode.SPECTATOR)) {
				winners.add(p);
			}
		}
		
		matchEndTimer.run(winners);
	}
	
	public void next() {
		plugin.matchHandler.nextMatch();
		for (UUID player : players) {
			Player p = Bukkit.getPlayer(player);
			if (p.isOnline()) {
				plugin.matchHandler.getNextMatchByPosition(this).addPlayer(p);
				p.teleport(plugin.matchHandler.getNextMatchByPosition(this).getSpawn());
				p.setScoreboard(plugin.matchHandler.getNextMatchByPosition(this).getScoreboard());
				p.getInventory().clear();
				p.setExp(0);
				p.setGameMode(GameMode.SURVIVAL);
			}
			players.remove(player);
		}
		HandlerList.unregisterAll(gameListener);
		this.active = false;
		plugin.matchHandler.rotate(this);
	}
	
	
	
}
