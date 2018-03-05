package com.bermudalocket.nerdUHC.modules;

import com.bermudalocket.nerdUHC.gui.GUIHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.listeners.MatchListener;
import com.bermudalocket.nerdUHC.listeners.PreMatchListener;
import com.bermudalocket.nerdUHC.match.MatchStartCountdownTimer;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardHandler;
import com.bermudalocket.nerdUHC.match.MatchTimer;
import org.bukkit.scoreboard.Team;

public class UHCMatch {

	private final NerdUHC plugin;
	private UHCMatchState state;
	private final UHCGameMode mode;
	private final World world;
	private long duration;

	private final UHCWorldBorder worldBorder;

	private Scoreboard scoreboard;

	private final ScoreboardHandler scoreboardHandler;
	
	private final GUIHandler guiHandler;

	private final PreMatchListener prematchListener;
	private final MatchListener matchListener;

	private final MatchStartCountdownTimer matchStartCountdownTimer;
	private final MatchTimer matchTimer;

	public UHCMatch(NerdUHC plugin) {
		this.plugin = plugin;

		// match properties
		this.state = UHCMatchState.PREGAME;
		this.mode = plugin.config.UHC_GAME_MODE;
		this.world = plugin.config.WORLD;
		this.duration = plugin.config.MATCH_DURATION * 60;

		// tools
		this.worldBorder = new UHCWorldBorder(this);
		this.scoreboardHandler = new ScoreboardHandler(this);
		this.guiHandler = new GUIHandler(this);

		// timers
		this.matchStartCountdownTimer = new MatchStartCountdownTimer(this);
		this.matchTimer = new MatchTimer(this, this.duration);

		// listeners
		this.prematchListener = new PreMatchListener(plugin);
		this.matchListener = new MatchListener(this);

		guiHandler.startListening();
		prematchListener.startListening();
	}
	
	// ----------------------------------------------------------------
	
	public ScoreboardHandler getScoreboardHandler() {
		return scoreboardHandler;
	}
	
	public MatchTimer getMatchTimer() {
		return matchTimer;
	}
	
	public GUIHandler getGUI() {
		return guiHandler;
	}

	// ----------------------------------------------------------------

	public World getWorld() {
		return world;
	}

	public UHCWorldBorder getWorldBorder() {
		return worldBorder;
	}

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
			resetPlayer(player);
			player.setScoreboard(scoreboard);
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, null));
		});
		scoreboardHandler.refresh();
	}
	
	public void resetPlayer(Player p) {
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
		p.setSaturation(20);
		p.getInventory().clear();
		p.setExp(0);
		scoreboardHandler.refresh();
	}

	// ----------------------------------------------------------------

	public void beginMatchStartCountdown() {
		if (matchStartCountdownTimer.isRunning()) {
			matchStartCountdownTimer.pause();
		} else {
			try {
				matchStartCountdownTimer.start();
			} catch (IllegalStateException e) {
				matchStartCountdownTimer.resume();
			}
		}
	}
	
	// ----------------------------------------------------------------

	public void beginMatch() {
		this.state = UHCMatchState.INPROGRESS;
		
		prematchListener.stopListening();
		guiHandler.stopListening();
		
		matchListener.startListening();

		world.setTime(100);
		world.setStorm(false);
		world.setPVP(false);

		scoreboardHandler.pruneTeams();
		scoreboardHandler.spreadPlayers();
		UHCSound.MATCHSTART.playSound();

		matchTimer.runTaskTimer(plugin, 0, 20);
	}
	
	// ----------------------------------------------------------------

	public void beginDeathmatch() {
		this.state = UHCMatchState.DEATHMATCH;
		UHCLibrary.DEATHMATCH.sendAsTitle();
		UHCSound.DEATHMATCHSTART.playSound();
		scoreboardHandler.spreadPlayers();
	}
	
	// ----------------------------------------------------------------

	public void checkForMatchEnd() {
		if (mode == UHCGameMode.SOLO) {
			if (scoreboard.getEntries().size() == 1) {
				String lastPlayer = scoreboard.getEntries().toArray()[0].toString();
				transitionToEnd(lastPlayer + " wins!");
			}
		} else if (mode == UHCGameMode.TEAM) {
			if (scoreboard.getTeams().size() == 1) {
				Team lastteam = (Team) scoreboard.getTeams().toArray()[0];
				transitionToEnd("The " + lastteam.getDisplayName() + " win!");
			}
		}
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
		matchListener.stopListening();
		plugin.matchHandler.getNewMatch();
		plugin.matchHandler.getMatch().migratePlayers();
	}

}
