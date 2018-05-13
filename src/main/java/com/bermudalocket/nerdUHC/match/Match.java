package com.bermudalocket.nerdUHC.match;

import com.bermudalocket.nerdUHC.Configuration;
import com.bermudalocket.nerdUHC.thread.CountdownThread;
import com.bermudalocket.nerdUHC.thread.DeathmatchThread;
import com.bermudalocket.nerdUHC.thread.PvpThread;
import com.bermudalocket.nerdUHC.thread.RecycleMatchThread;
import com.bermudalocket.nerdUHC.thread.ScoreboardThread;
import com.bermudalocket.nerdUHC.listeners.MatchListener;
import com.bermudalocket.nerdUHC.thread.WorldBorderThread;
import com.bermudalocket.nerdUHC.util.MatchMode;
import com.bermudalocket.nerdUHC.util.UHCLibrary;
import com.bermudalocket.nerdUHC.util.MatchState;
import com.bermudalocket.nerdUHC.util.UHCSounds;

import com.bermudalocket.nerdUHC.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import static com.bermudalocket.nerdUHC.NerdUHC.GUI_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.PLAYER_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.SCOREBOARD_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.THREAD_HANDLER;

public class Match {

	private MatchState _state = MatchState.PREGAME;

	private final MatchMode _mode;

	private final World _world;

	private int _duration;

	private final MatchListener matchListener;

	public Match(MatchMode matchMode, int duration, World world) {
		_mode = (matchMode == null) ? Configuration.UHC_GAME_MODE : matchMode;
		_world = (world == null) ? Configuration.WORLD : world;
		_duration = (duration == 0) ? Configuration.MATCH_DURATION : duration;

		matchListener = new MatchListener();

		SCOREBOARD_HANDLER.requestScoreboard();
		GUI_HANDLER.startListening();
	}

	public Match() {
		this(null, 0, null);
	}

	// -------------------------------------------------------------------------

	private void schedule() {
		int pvpDelay = (_duration < 20) ? _duration/2 : 20;
		THREAD_HANDLER.schedule(Util.minsToTicks(pvpDelay), new PvpThread(this));
		THREAD_HANDLER.schedule(Util.minsToTicks(_duration), new DeathmatchThread(this));
		THREAD_HANDLER.schedule(Util.minsToTicks(_duration/2), new WorldBorderThread(this, 500, _duration/2));
		THREAD_HANDLER.scheduleRepeating(0L, 20L, new ScoreboardThread(this));
	}

	// -------------------------------------------------------------------------

	public int getDuration() {
		return _duration;
	}

	public void setDuration(int minutes) {
		_duration = minutes;
	}

	public MatchState getMatchState() {
		return _state;
	}

	public boolean inState(MatchState matchState) {
		return matchState.equals(_state);
	}

	public World getWorld() {
		return _world;
	}

	public MatchMode getGameMode() {
		return _mode;
	}

	// ----------------------------------------------------------------
	// MATCH STAGES
	// ----------------------------------------------------------------

	public void startCountdown() {
		if (!THREAD_HANDLER.threadExists(CountdownThread.class)) {
			THREAD_HANDLER.scheduleRepeating(0L, 20L, new CountdownThread(this, 10));
		} else {
			THREAD_HANDLER.removeThread(CountdownThread.class);
		}
	}

	public void beginMatch() {
		_state = MatchState.INPROGRESS;

		schedule();

		GUI_HANDLER.stopListening();
		SCOREBOARD_HANDLER.pruneTeams();
		PLAYER_HANDLER.spreadPlayers();

		_world.setTime(100);
		_world.setStorm(false);
		_world.setPVP(false);

		UHCSounds.MATCHSTART.playSound();
	}

	public void beginDeathmatch() {
		_state = MatchState.DEATHMATCH;

		UHCLibrary.DEATHMATCH.sendAsTitle();
		UHCSounds.DEATHMATCHSTART.playSound();

		PLAYER_HANDLER.spreadPlayers();
	}

	public void checkForMatchEnd() {
		switch(_mode) {
			case SOLO:
				if (SCOREBOARD_HANDLER.getTotalPlayers() == 1) {
					String lastPlayer = SCOREBOARD_HANDLER.getRegisteredPlayers().toArray()[0].toString();
					transitionToEnd(lastPlayer + " wins!");
				}
				break;
			case TEAM:
				if (SCOREBOARD_HANDLER.getTotalTeams() == 1) {
					Team lastTeam = (Team) SCOREBOARD_HANDLER.getRegisteredTeams().toArray()[0];
					transitionToEnd("The " + lastTeam.getDisplayName() + " win!");
				} else if (SCOREBOARD_HANDLER.getTotalTeams() == 0) {
					transitionToEnd("TIE!");
				}
				break;
			default:
				break;
		}
	}

	public void transitionToEnd(String message) {
		_state = MatchState.END;
		UHCSounds.MATCHEND.playSound();
		matchListener.stopListening();
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(message, null, 20, 120, 20);
			p.sendMessage(ChatColor.AQUA + "Next match in 20 seconds...");
		}
		
		THREAD_HANDLER.schedule(Util.secToTicks(20), new RecycleMatchThread());
	}

}
