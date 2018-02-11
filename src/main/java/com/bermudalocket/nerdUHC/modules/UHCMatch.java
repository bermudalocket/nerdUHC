package com.bermudalocket.nerdUHC.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.listeners.GameListener;
import com.bermudalocket.nerdUHC.listeners.PregameListener;
import com.bermudalocket.nerdUHC.match.MatchStartCountdownTimer;
import com.bermudalocket.nerdUHC.match.CombatLogger;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardHandler;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardTimer;

public class UHCMatch {

	private NerdUHC plugin;
	private UHCMatchState state;
	private UHCGameMode mode;
	private World world;
	private long duration;

	private Scoreboard scoreboard;

	private ScoreboardHandler scoreboardHandler;
	private CombatLogger combatLogger;
	private UHCUtils util;
	
	private UHCInventoryMenu menuGUI;

	private PregameListener pregameListener;
	private GameListener gameListener;

	private MatchStartCountdownTimer matchStartCountdownTimer;
	private ScoreboardTimer scoreboardTimer;

	public UHCMatch(NerdUHC plugin) {
		this.plugin = plugin;
		this.state = UHCMatchState.PREGAME;
		this.mode = plugin.CONFIG.UHC_GAME_MODE;
		this.world = plugin.CONFIG.WORLD;
		this.duration = plugin.CONFIG.MATCH_DURATION * 60;

		this.scoreboardHandler = new ScoreboardHandler(plugin, this);
		this.scoreboardHandler.createScoreboard();
		
		this.combatLogger = new CombatLogger(plugin);
		this.util = new UHCUtils(plugin, this);
		
		this.menuGUI = new UHCInventoryMenu(plugin, this);
		plugin.getServer().getPluginManager().registerEvents(menuGUI, plugin);

		pregameListener = new PregameListener(plugin);
		plugin.getServer().getPluginManager().registerEvents(pregameListener, plugin);
	}
	
	// ----------------------------------------------------------------
	
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
	
	public UHCInventoryMenu getGUI() {
		return menuGUI;
	}

	// ----------------------------------------------------------------

	public World getWorld() {
		return world;
	}

	public Location getSpawn() {
		return plugin.CONFIG.SPAWN;
	}
	
	public void setDuration(int d) {
		this.duration = d * 60;
	}
	
	// SCOREBOARD

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public void setScoreboard(Scoreboard board) {
		this.scoreboard = board;
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
			resetPlayer(player);
			player.setScoreboard(scoreboard);
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, null));
		});
	}
	
	public void resetPlayer(Player p) {
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
		p.setSaturation(20);
		p.getInventory().clear();
		p.setExp(0);
	}
	
	public void heal(Player p, boolean givedamageresistance) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20 * 1, 100, true));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 1, 100, true));
		if (givedamageresistance) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 100, true));
		}
		scoreboardHandler.forceHealthUpdates();
	}

	public void freeze() {
		if (state == UHCMatchState.FROZEN) {
			
			plugin.CONFIG.WORLD.setPVP(true);
		} else {
			plugin.CONFIG.WORLD.setPVP(false);
		}
	}

	public boolean isFrozen() {
		return state == UHCMatchState.FROZEN;
	}

	// Starters and stoppers for different phases of the match

	public void beginMatchStartCountdown() {
		HandlerList.unregisterAll(pregameListener);
		HandlerList.unregisterAll(menuGUI);
		
		gameListener = new GameListener(this);
		plugin.getServer().getPluginManager().registerEvents(gameListener, plugin);
		
		matchStartCountdownTimer = new MatchStartCountdownTimer(this);
		matchStartCountdownTimer.runTaskTimer(plugin, 1, 20);
	}

	@SuppressWarnings("deprecation")
	public void beginMatch() {
		this.state = UHCMatchState.INPROGRESS;
		matchStartCountdownTimer = null;

		scoreboardHandler.pruneTeams();

		String spreadplayers = "spreadplayers " + getSpawn().getX();
		spreadplayers += " " + getSpawn().getZ();
		spreadplayers += " " + plugin.CONFIG.SPREAD_DIST_BTWN_PLAYERS;
		spreadplayers += " " + plugin.CONFIG.SPREAD_DIST_FROM_SPAWN;
		spreadplayers += " " + plugin.CONFIG.SPREAD_RESPECT_TEAMS;

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (scoreboard.getPlayerTeam(p) != null) {
				spreadplayers += " " + p.getName();
				resetPlayer(p);
			} else {
				p.setGameMode(GameMode.SPECTATOR);
			}
		}

		plugin.CONFIG.WORLD.setTime(0);
		plugin.CONFIG.WORLD.setStorm(false);
		plugin.CONFIG.WORLD.setDifficulty(Difficulty.HARD);
		plugin.CONFIG.WORLD.setPVP(true);

		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), spreadplayers);

		UHCSound.MATCHSTART.playSound();

		util.drawBarrier(false);

		scoreboardTimer = new ScoreboardTimer(this, this.duration);
		scoreboardTimer.runTaskTimer(plugin, 0, 20);

		scoreboardHandler.refresh();
	}

	public void beginMatchEndTransition() {
		if (!scoreboardTimer.isCancelled()) scoreboardTimer.cancel();
		beginDeathmatch();
	}

	@SuppressWarnings("deprecation")
	public void beginDeathmatch() {
		this.state = UHCMatchState.DEATHMATCH;

		String spreadplayers = "spreadplayers " + plugin.CONFIG.SPAWN_X;
		spreadplayers += " " + plugin.CONFIG.SPAWN_Z;
		spreadplayers += " " + plugin.CONFIG.DEATHMATCH_DIST_BTWN_PLAYERS;
		spreadplayers += " " + plugin.CONFIG.DEATHMATCH_SPREAD_DIST_FROM_SPAWN;
		spreadplayers += " " + plugin.CONFIG.SPREAD_RESPECT_TEAMS;
		spreadplayers += " ";

		for (OfflinePlayer op : scoreboard.getPlayers()) {
			if (op.isOnline()) {
				spreadplayers += op.getName() + " ";
			}
		}

		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), spreadplayers);
		
		Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + "Deathmatch!", null, 15, 60, 15));
		
		scoreboardHandler.refresh();
	}
	
	public void transitionToEnd(String winmsg) {
		this.state = UHCMatchState.END;
		
		freeze();
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(winmsg, null, 20, 120, 20);
			p.sendMessage(ChatColor.AQUA + "Next match in 20 seconds...");
		}
		
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
