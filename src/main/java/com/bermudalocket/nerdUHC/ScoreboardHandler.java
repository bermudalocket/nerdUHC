package com.bermudalocket.nerdUHC;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import com.bermudalocket.nerdUHC.NerdUHC.UHCGameMode;

public class ScoreboardHandler {
	
	private NerdUHC plugin;
	private ScoreboardManager manager;
	private Scoreboard board;
	private Objective objDeaths;
	private Objective objKills;
	private Objective objHealth;
	private Objective objHealthOverhead;
	
	// teams for SOLO mode
	private Team teamAlive;
	private Team teamDead;
	
	// map holding user-defined teams for TEAM mode
	public Map<String, Team> TEAMS = new HashMap<String, Team>();
	
	public ScoreboardHandler(NerdUHC plugin) {
		this.plugin = plugin;
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
	}
	
	public boolean teamExists(String team) {
		plugin.getLogger().info("teamExists("+team+")");
		try {
			board.getTeam(team);
		} catch (Exception f) {
			return false;
		}
		return true;
	}
	
	public void removePlayerTeam(Player player) {
		board.getEntryTeam(player.getName()).removeEntry(player.getName());
	}
	
	public void setPlayerTeam(Player player, String team) {
		board.getTeam(team).addEntry(player.getName());
	}
	
	public Team getPlayerTeam(Player player) {
		try {
			return board.getEntryTeam(player.getName());
		} catch (Exception f) {
			return null;
		}
	}
	
	public boolean chooseTeamForPlayer(Player player) {
		
		Optional<Team> foundteam = board.getTeams().stream().filter(team -> team.getSize() < plugin.CONFIG.MAX_TEAM_SIZE).findFirst();
		
		if (foundteam.orElse(null) != null) {
			setPlayerTeam(player, foundteam.get().getName());
			return true;
		} else {
			// no teams need players
			return false;
		}
		
	}
	
	public void setPlayerBoard(Player player) {
		try {
			player.setScoreboard(board);
			plugin.getLogger().info("Set board for player");
		} catch (IllegalArgumentException f) {
			// board doesnt exist
		} catch (IllegalStateException g) {
			// player doesnt exist
		}
	}
	
	public void unsetPlayerBoard(Player player) {
		try {
			player.setScoreboard(manager.getNewScoreboard());
		} catch (IllegalArgumentException f) {
			// board doesnt exist, which shouldnt happen in this case
		} catch (IllegalStateException g) {
			// player doesnt exist
		}
	}
	
	public int getPlayerScore(Player player, String objective) {
		return board.getObjective(objective).getScore(player.getName()).getScore();
	}
	
	public void setPlayerScore(Player player, String objective, int score) {
		board.getObjective(objective).getScore(player.getName()).setScore(score);
	}
	
	public void clearBoards() {
		board.getEntries().forEach(entry -> board.resetScores(entry));
		board.getTeams().forEach(team -> team.unregister());
		board.getObjectives().forEach(objective -> objective.unregister());
	}
	
	public void configureScoreboards(UHCGameMode gameMode) {
		
		objDeaths = board.registerNewObjective("Deaths","deathCount");
		objKills = board.registerNewObjective("Kills","playerKillCount");
		objHealth = board.registerNewObjective("Health","health");
		objHealthOverhead = board.registerNewObjective("HealthOverhead", "health");
		
		objHealth.setDisplaySlot(plugin.CONFIG.HEALTH_DISPLAY_SLOT);
		objKills.setDisplaySlot(plugin.CONFIG.KILLS_DISPLAY_SLOT);
		if (plugin.CONFIG.DISPLAY_HEALTH_BELOW_NAME) objHealthOverhead.setDisplaySlot(DisplaySlot.BELOW_NAME);
		
		switch (gameMode) {
			default:
			case SOLO:
				teamAlive = board.registerNewTeam("Alive");
				teamDead = board.registerNewTeam("Dead");
				break;
			case TEAM:
				plugin.CONFIG.rawteamlist.forEach(team -> {
					board.registerNewTeam(team);
					TEAMS.put(team, board.getTeam(team));
				});
				break;
		}

	} // end configureScoreboards()

} //ScoreboardHandler
