package com.bermudalocket.nerdUHC.modules;

import com.bermudalocket.nerdUHC.gui.GUIHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import com.bermudalocket.nerdUHC.listeners.MatchListener;
import com.bermudalocket.nerdUHC.listeners.PreMatchListener;
import com.bermudalocket.nerdUHC.match.MatchStartCountdownTimer;
import com.bermudalocket.nerdUHC.match.CombatLogger;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardHandler;
import com.bermudalocket.nerdUHC.match.MatchTimer;

public class UHCMatch {

	private final NerdUHC plugin;
	private UHCMatchState state;
	private final UHCGameMode mode;
	private final World world;
	private long duration;

	private final UHCWorldBorder worldBorder;

	private Scoreboard scoreboard;

	private final ScoreboardHandler scoreboardHandler;
	private final CombatLogger combatLogger;
	
	private final GUIHandler menuGUI;

	private final PreMatchListener pregameListener;
	private final MatchListener gameListener;

	private final MatchStartCountdownTimer matchStartCountdownTimer;
	private final MatchTimer matchTimer;

	public UHCMatch(NerdUHC plugin) {
		this.plugin = plugin;
		this.state = UHCMatchState.PREGAME;
		this.mode = plugin.CONFIG.UHC_GAME_MODE;
		this.world = plugin.CONFIG.WORLD;
		this.duration = plugin.CONFIG.MATCH_DURATION * 60;

		this.worldBorder = new UHCWorldBorder(this);

		this.scoreboardHandler = new ScoreboardHandler(plugin, this);
		this.scoreboardHandler.createScoreboard();
		
		matchStartCountdownTimer = new MatchStartCountdownTimer(this);
		matchTimer = new MatchTimer(this, this.duration);
		
		this.combatLogger = new CombatLogger(plugin);
		
		this.menuGUI = new GUIHandler(plugin, this);
		plugin.getServer().getPluginManager().registerEvents(menuGUI, plugin);

		pregameListener = new PreMatchListener(plugin);
		plugin.getServer().getPluginManager().registerEvents(pregameListener, plugin);
		
		gameListener = new MatchListener(this);
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
	
	public MatchTimer getMatchTimer() {
		return matchTimer;
	}
	
	public GUIHandler getGUI() {
		return menuGUI;
	}

	// ----------------------------------------------------------------

	public World getWorld() {
		return world;
	}

	public UHCWorldBorder getWorldBorder() { return worldBorder; }

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public void setScoreboard(Scoreboard board) {
		this.scoreboard = board;
	}

	public UHCGameMode getGameMode() {
		return mode;
	}

	public UHCMatchState getMatchState() {
		return state;
	}
	
	public void setDuration(int minutes) {
		this.duration = minutes * 60;
	}
	
	// ----------------------------------------------------------------
	
	public void migratePlayers() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			resetPlayer(player, false);
			player.setScoreboard(scoreboard);
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, null));
		});
		plugin.matchHandler.getMatch().getScoreboardHandler().refresh();
	}
	
	public void resetPlayer(Player p, boolean givedmgresist) {
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
		p.setSaturation(20);
		p.getInventory().clear();
		p.setExp(0);
		if (givedmgresist) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 100, true));
		}
		scoreboardHandler.forceHealthUpdates();
	}

	// ----------------------------------------------------------------

	public void beginMatchStartCountdown() {
		if (matchStartCountdownTimer.isRunning()) {
			matchStartCountdownTimer.attemptStop();
		} else {
			matchStartCountdownTimer.runTaskTimer(plugin, 1, 20);
		}
	}
	
	// ----------------------------------------------------------------

	public void beginMatch() {
		this.state = UHCMatchState.INPROGRESS;
		UHCSound.MATCHSTART.playSound();
		
		HandlerList.unregisterAll(pregameListener);
		HandlerList.unregisterAll(menuGUI);
		
		plugin.getServer().getPluginManager().registerEvents(gameListener, plugin);

		plugin.CONFIG.WORLD.setTime(100);
		plugin.CONFIG.WORLD.setStorm(false);
		plugin.CONFIG.WORLD.setPVP(false);

		scoreboardHandler.pruneTeams();
		scoreboardHandler.spreadPlayers();

		matchTimer.runTaskTimer(plugin, 0, 20);
	}
	
	// ----------------------------------------------------------------

	public void beginMatchEndTransition() {
		if (!matchTimer.isCancelled()) {
			matchTimer.cancel();
		}
		beginDeathmatch();
	}
	
	// ----------------------------------------------------------------

	private void beginDeathmatch() {
		this.state = UHCMatchState.DEATHMATCH;
		UHCSound.DEATHMATCHSTART.playSound();

		scoreboardHandler.spreadPlayers();

		UHCLibrary.DEATHMATCH.sendAsTitle();
	}
	
	// ----------------------------------------------------------------
	
	public void transitionToEnd(String winmsg) {
		this.state = UHCMatchState.END;
		UHCSound.MATCHEND.playSound();
		
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
	
	// ----------------------------------------------------------------
	
	public void endMatch() {
		if (!matchTimer.isCancelled()) {
			matchTimer.cancel();
		}
		HandlerList.unregisterAll(gameListener);
		plugin.matchHandler.getNewMatch();
		migratePlayers();
	}

}
