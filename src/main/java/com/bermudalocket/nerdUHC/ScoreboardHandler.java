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


/////////////////////////////////////////////////////////////////////////////
//
//	ScoreboardHandler
//    Handles everything to do with the scoreboards, including:
//    Teams, Players, Scores, and configuration
//

public class ScoreboardHandler {
	
	private ScoreboardManager manager;
	private Scoreboard board;

	public Map<String, Objective> OBJECTIVES = new HashMap<String, Objective>();
	public Map<String, Team> TEAMS = new HashMap<String, Team>();

	/////////////////////////////////////////////////////////////////////////////
	//
	//	Team commands
	//
	//	
	
	public boolean teamExists(String team) {
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
	
	public int getTeamSize(String team) {
		return board.getTeam(team).getSize();
	}
	
	public Team getPlayerTeam(Player player) {
		try {
			return board.getEntryTeam(player.getName());
		} catch (Exception f) {
			return null;
		}
	}
	
	public boolean chooseTeamForPlayer(Player player) {
		
		Optional<Team> foundteam = board.getTeams().stream().filter(team -> team.getSize() < NerdUHC.CONFIG.MAX_TEAM_SIZE).findFirst();
		
		if (foundteam.orElse(null) != null) {
			setPlayerTeam(player, foundteam.get().getName());
			return true;
		} else {
			// no teams need players
			return false;
		}
		
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Player scoreboard commands
	//
	//	
	
	public void setPlayerBoard(Player player) {
		try {
			player.setScoreboard(board);
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

	/////////////////////////////////////////////////////////////////////////////
	//
	//	Score commands
	//	
	//	
	
	
	public int getPlayerScore(Player player, String objective) {
		return board.getObjective(objective).getScore(player.getName()).getScore();
	}
	
	public void setPlayerScore(Player player, String objective, int score) {
		board.getObjective(objective).getScore(player.getName()).setScore(score);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Configuration commands
	//	
	//	

	public void setManager() {
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
	}
	
	public void reloadScoreboards() {
		clearBoards();
		configureScoreboards();
	}
	
	public void clearBoards() {
		board.getEntries().forEach(entry -> board.resetScores(entry));
		board.getTeams().forEach(team -> team.unregister());
		board.getObjectives().forEach(objective -> objective.unregister());
	}
	
	public boolean isValidDisplaySlot(String slot) {
		try {
			DisplaySlot.valueOf(slot);
			return true;
		} catch (Exception f) {
			return false;
		}
	}
	
	public void configureScoreboards() {
		
		UHCGameMode gameMode = NerdUHC.getGameMode();
		
		NerdUHC.CONFIG.rawobjectiveslist.forEach(objective -> {
			String objname = objective.get("name").toString();
			String objcriteria = objective.get("criteria").toString();
			String objdisplayslot = objective.get("displayslot").toString();
			
			if (objcriteria != null && isValidDisplaySlot(objdisplayslot)) {
				board.registerNewObjective(objname.toUpperCase(), objcriteria).setDisplaySlot(DisplaySlot.valueOf(objdisplayslot));
				OBJECTIVES.put(objname.toUpperCase(), board.getObjective(objname.toUpperCase()));
			}
		});
		
		try {
			Objective deaths = (Objective) board.getObjectivesByCriteria("deathCount");
			NerdUHC.CONFIG.DEATH_OBJECTIVE_NAME = deaths.getName();
		} catch (Exception f) {
			board.registerNewObjective("DEATHS", "deathCount");
			OBJECTIVES.put("DEATHS", board.getObjective("DEATHS"));
			NerdUHC.CONFIG.DEATH_OBJECTIVE_NAME = "DEATHS";
		}
		
		switch (gameMode) {
			default:
			case SOLO:
				TEAMS.put(NerdUHC.CONFIG.ALIVE_TEAM_NAME.toUpperCase(), board.registerNewTeam(NerdUHC.CONFIG.ALIVE_TEAM_NAME));
				TEAMS.put(NerdUHC.CONFIG.DEAD_TEAM_NAME.toUpperCase(), board.registerNewTeam(NerdUHC.CONFIG.DEAD_TEAM_NAME));
				break;
			case TEAM:
				NerdUHC.CONFIG.rawteamlist.forEach(team -> {
					board.registerNewTeam(team.toUpperCase());
					TEAMS.put(team.toUpperCase(), board.getTeam(team.toUpperCase()));
				});
				break;
		}

	} // end configureScoreboards()

} //ScoreboardHandler
