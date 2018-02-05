package com.bermudalocket.nerdUHC.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import com.bermudalocket.nerdUHC.CombatLogger;
import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.listeners.GameListener;
import com.bermudalocket.nerdUHC.listeners.PregameListener;
import com.bermudalocket.nerdUHC.match.MatchStartCountdownTimer;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardHandler;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardTimer;

public class UHCMatch {

	private NerdUHC plugin;
	private UHCMatchState state;
	private UHCGameMode mode;
	private World world;

	private Scoreboard scoreboard;

	private long duration = 0;
	private boolean frozen = false;

	private ScoreboardHandler scoreboardHandler;
	private CombatLogger combatLogger;
	private UHCUtils util;

	private PregameListener pregameListener;
	private GameListener gameListener;

	private MatchStartCountdownTimer matchStartCountdownTimer;
	private ScoreboardTimer scoreboardTimer;

	public UHCMatch(NerdUHC plugin) {
		this.plugin = plugin;
		this.state = UHCMatchState.PREGAME;
		this.mode = plugin.CONFIG.UHC_GAME_MODE;
		this.world = plugin.CONFIG.WORLD;

		this.scoreboardHandler = new ScoreboardHandler(plugin, this);
		this.scoreboardHandler.createScoreboard();
		
		this.combatLogger = new CombatLogger(plugin);
		this.util = new UHCUtils(plugin, this);

		pregameListener = new PregameListener(plugin);
		plugin.getServer().getPluginManager().registerEvents(pregameListener, plugin);
	}
	
	public NerdUHC getPlugin() {
		return plugin;
	}

	public CombatLogger getCombatLogger() {
		return combatLogger;
	}
	
	public ScoreboardHandler getScoreboardHandler() {
		return scoreboardHandler;
	}

	public UHCUtils getBarrier() {
		return util;
	}
	
	public ScoreboardTimer getScoreboardTimer() {
		return scoreboardTimer;
	}

	// WORLD

	public World getWorld() {
		return world;
	}

	public Location getSpawn() {
		return plugin.CONFIG.SPAWN;
	}
	
	// SCOREBOARD

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public void setScoreboard(Scoreboard board) {
		this.scoreboard = board;
	}
	
	// TIMER

	public long getDuration() {
		return duration;
	}

	public void extendTime(int sec) {
		duration += sec * 1000;
	}
	
	// MATCH MODE AND STATE

	public UHCGameMode getGameMode() {
		return mode;
	}

	public UHCMatchState getMatchState() {
		return state;
	}
	
	// PLAYERS
	
	public void migratePlayers() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.setScoreboard(scoreboard);
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, null));
		});
	}
	
	public void heal(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20 * 1, 100, true));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 1, 100, true));
	}

	public void freeze() {
		if (frozen) {
			frozen = false;
			plugin.CONFIG.WORLD.setPVP(true);
		} else {
			frozen = true;
			plugin.CONFIG.WORLD.setPVP(false);
		}
	}

	public boolean isFrozen() {
		return frozen;
	}

	// Starters and stoppers for different phases of the match

	public void beginMatchStartCountdown() {
		
		HandlerList.unregisterAll(pregameListener);
		
		gameListener = new GameListener(this);
		plugin.getServer().getPluginManager().registerEvents(gameListener, plugin);
		
		matchStartCountdownTimer = new MatchStartCountdownTimer(this);
		matchStartCountdownTimer.runTaskTimer(plugin, 1, 20);
		
	}

	@SuppressWarnings("deprecation")
	public void beginMatch() {
		this.state = UHCMatchState.INPROGRESS;
		this.duration = System.currentTimeMillis() + plugin.CONFIG.MATCH_DURATION * 60000;
		matchStartCountdownTimer = null;

		scoreboardHandler.pruneTeams();

		String spreadplayers = "spreadplayers " + getSpawn().getX();
		spreadplayers += " " + getSpawn().getZ();
		spreadplayers += " " + plugin.CONFIG.SPREAD_DIST_BTWN_PLAYERS;
		spreadplayers += " " + plugin.CONFIG.SPREAD_DIST_FROM_SPAWN;
		if (mode == UHCGameMode.TEAM) {
			spreadplayers += " " + plugin.CONFIG.SPREAD_RESPECT_TEAMS;
		} else {
			spreadplayers += !plugin.CONFIG.SPREAD_RESPECT_TEAMS;
		}
		spreadplayers += " ";

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (scoreboard.getPlayerTeam(p) != null) {
				spreadplayers += p.getName() + " ";
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 100, true));
			} else {
				p.setGameMode(GameMode.SPECTATOR);
			}
			heal(p);
		}

		plugin.CONFIG.WORLD.setTime(0);
		plugin.CONFIG.WORLD.setStorm(false);
		plugin.CONFIG.WORLD.setDifficulty(Difficulty.HARD);
		plugin.CONFIG.WORLD.setPVP(true);

		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), spreadplayers);

		UHCSound.MATCHSTART.playSound();

		util.drawBarrier(false);

		scoreboardTimer = new ScoreboardTimer(this, plugin.CONFIG.MATCH_DURATION);
		scoreboardTimer.runTaskTimer(plugin, 0, 20);

		scoreboardHandler.refresh();
	}

	public void beginMatchEndTransition() {
		this.state = UHCMatchState.TRANSITION;
		if (!scoreboardTimer.isCancelled()) scoreboardTimer.cancel();
		beginDeathmatch();
	}

	@SuppressWarnings("deprecation")
	public void beginDeathmatch() {
		this.state = UHCMatchState.DEATHMATCH;

		String target = " ";
		for (OfflinePlayer op : scoreboard.getPlayers()) {
			if (op.isOnline()) target += op.getName() + " ";
		}

		String spreadplayers = "spreadplayers " + plugin.CONFIG.SPAWN_X;
		spreadplayers += " " + plugin.CONFIG.SPAWN_Z;
		spreadplayers += " " + plugin.CONFIG.DEATHMATCH_DIST_BTWN_PLAYERS;
		spreadplayers += " " + plugin.CONFIG.DEATHMATCH_SPREAD_DIST_FROM_SPAWN;
		
		if (mode == UHCGameMode.TEAM) {
			spreadplayers += " " + plugin.CONFIG.SPREAD_RESPECT_TEAMS;
		} else {
			spreadplayers += " " + !plugin.CONFIG.SPREAD_RESPECT_TEAMS;
		}

		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), spreadplayers + target);
		
		Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + "Deathmatch!", null, 15, 60, 15));
		
		scoreboardHandler.refresh();
	}
	
	public void transitionToEnd() {
		this.state = UHCMatchState.END;
		
		freeze();
		
		for (Player p : Bukkit.getOnlinePlayers()) p.sendMessage(ChatColor.AQUA + "Next match in 20 seconds...");
		
		BukkitRunnable endMatchTask = new BukkitRunnable() {
			@Override
			public void run() {
				endMatch();
			}
		};
		endMatchTask.runTaskLater(plugin, 400);
	}
	
	public void endMatch() {
		if (!scoreboardTimer.isCancelled()) scoreboardTimer.cancel();
		scoreboardTimer = null;
		scoreboardHandler = null;
		combatLogger = null;
		util = null;
		HandlerList.unregisterAll(gameListener);
		plugin.matchHandler.getNewMatch();
		migratePlayers();
	}

}
