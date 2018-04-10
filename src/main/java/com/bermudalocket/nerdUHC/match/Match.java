package com.bermudalocket.nerdUHC.match;

import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import com.bermudalocket.nerdUHC.modules.UHCSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import org.bukkit.scoreboard.Team;

import java.io.Serializable;

public class Match implements Serializable {

	private MatchState _state;
	private final MatchMode _mode;
	private final World _world;
	private long _duration;

	public Match() {
		// match properties
		_state = MatchState.PREGAME;
		_mode = NerdUHC.CONFIG.UHC_GAME_MODE;
		_world = NerdUHC.CONFIG.WORLD;
		_duration = NerdUHC.CONFIG.MATCH_DURATION * 60;
	}

	// ----------------------------------------------------------------

	public World getWorld() {
		return _world;
	}

	public Location getSpawn() {
		return _world.getSpawnLocation();
	}

	public MatchMode getGameMode() {
		return _mode;
	}

	public MatchState getMatchState() {
		return _state;
	}
	
	public void setDuration(int minutes) {
		_duration = minutes * 60;
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
		_state = MatchState.INPROGRESS;

		_world.setTime(100);
		_world.setStorm(false);
		_world.setPVP(false);

		NerdUHC.SCOREBOARD_HANDLER.pruneTeams();
		NerdUHC.SCOREBOARD_HANDLER.spreadPlayers();
		UHCSound.MATCHSTART.playSound();

		matchTimer.runTaskTimer(NerdUHC.PLUGIN, 0, 20);
	}
	
	// ----------------------------------------------------------------

	public void beginDeathmatch() {
		_state = MatchState.DEATHMATCH;
		UHCLibrary.DEATHMATCH.sendAsTitle();
		UHCSound.DEATHMATCHSTART.playSound();
		NerdUHC.PLAYER_HANDLER.spreadPlayers();
	}
	
	// ----------------------------------------------------------------

	public void checkForMatchEnd() {
		if (_mode == MatchMode.SOLO) {
			if (scoreboard.getEntries().size() == 1) {
				String lastPlayer = scoreboard.getEntries().toArray()[0].toString();
				transitionToEnd(lastPlayer + " wins!");
			}
		} else if (_mode == MatchMode.TEAM) {
			if (scoreboard.getTeams().size() == 1) {
				Team lastteam = (Team) scoreboard.getTeams().toArray()[0];
				transitionToEnd("The " + lastteam.getDisplayName() + " win!");
			}
		}
	}

	// ----------------------------------------------------------------
	
	public void transitionToEnd(String winmsg) {
		_state = MatchState.END;
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
		//endMatchTask.runTaskLater(PLUGIN, 400);
	}
	
	// ----------------------------------------------------------------
	
	public void endMatch() {
		if (!matchTimer.isCancelled()) {
			matchTimer.cancel();
		}
		NerdUHC.MATCH_HANDLER.getNewMatch();
		NerdUHC.PLAYER_HANDLER.migratePlayers();
	}

}
