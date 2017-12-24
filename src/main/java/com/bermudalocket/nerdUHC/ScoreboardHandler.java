package com.bermudalocket.nerdUHC;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class ScoreboardHandler {
	
	public nerdUHC plugin;
	public ScoreboardManager manager;
	public Scoreboard board;
	public Objective objDeaths;
	public Objective objKills;
	public Objective objHealth;
	public Objective objHealthOverhead;
	public Team teamAlive;
	public Team teamDead;
	public Team teamRed;
	public Team teamOrange;
	public Team teamYellow;
	public Team teamGreen;
	public Team teamBlue;
	public Team teamPurple;
	
	public ScoreboardHandler(nerdUHC plugin) {
		this.plugin = plugin;
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
	}
	
	public boolean TeamExists(String team) {
		return board.getTeam(team)==null ? true : false;
	}
	
	public void RemovePlayerTeam(Player player) {
		board.getEntryTeam(player.getName()).removeEntry(player.getName());
	}
	
	public void SetPlayerTeam(Player player, String team) {
		board.getTeam(team).addEntry(player.getName());
	}
	
	public void SetPlayerBoard(Player player) {
		player.setScoreboard(board);
	}
	
	public int GetPlayerScore(Player player, String objective) {
		return board.getObjective(objective).getScore(player.getName()).getScore();
	}
	
	public void SetPlayerScore(Player player, String objective, int score) {
		board.getObjective(objective).getScore(player.getName()).setScore(score);
	}
	
	public void ClearBoards() {
		if (!board.getEntries().isEmpty()) {
			board.getEntries().forEach(entry -> board.resetScores(entry));
		}
		if (!board.getTeams().isEmpty()) {
			board.getTeams().forEach(team -> team.unregister());
		}
		if (!board.getObjectives().isEmpty()) {
			board.getObjectives().forEach(objective -> objective.unregister());
		}
	}
	
	public void configureScoreboards(int gameMode) {
		
		objDeaths = board.registerNewObjective("Deaths","deathCount");
		objKills = board.registerNewObjective("Kills","playerKillCount");
		objHealth = board.registerNewObjective("Health","health");
		objHealthOverhead = board.registerNewObjective("HealthOverhead", "health");
		
		objHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		objKills.setDisplaySlot(DisplaySlot.SIDEBAR);
		objHealthOverhead.setDisplaySlot(DisplaySlot.BELOW_NAME);
	
		switch (gameMode) {
	
			default:
			case 0:
				teamAlive = board.registerNewTeam("Alive");
				teamDead = board.registerNewTeam("Dead");
				break;
			case 1:
				teamRed = board.registerNewTeam("Red");
				teamOrange = board.registerNewTeam("Orange");
				teamYellow = board.registerNewTeam("Yellow");
				teamGreen = board.registerNewTeam("Green");
				teamBlue = board.registerNewTeam("Blue");
				teamPurple = board.registerNewTeam("Purple");
				break;
		
		} // end switch

	} // end configureScoreboards()

} //ScoreboardHandler
