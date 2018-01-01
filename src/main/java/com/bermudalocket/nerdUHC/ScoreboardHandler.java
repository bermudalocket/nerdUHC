package com.bermudalocket.nerdUHC;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

	/////////////////////////////////////////////////////////////////////////////
	//
	//	Team commands
	//
	//	
	
	// ********************************************
	// checks if a team exists
	// ********************************************
	public boolean teamExists(String team) {
		try {
			board.getTeam(team);
		} catch (Exception f) {
			return false;
		}
		return true;
	}
	
	// ********************************************
	// removes a player from their team
	// ********************************************
	public void removePlayerTeam(Player player) {
		board.getEntryTeam(player.getName()).removeEntry(player.getName());
		player.setDisplayName(ChatColor.WHITE + player.getName());
		player.setPlayerListName(ChatColor.WHITE + player.getName());
	}
	
	// ********************************************
	// assigns a player to a team
	// ********************************************
	public void setPlayerTeam(Player player, String team) {
		ChatColor color = getTeamColor(team);
		board.getTeam(team).addEntry(player.getName());
		player.setDisplayName(color + player.getName());
		player.setPlayerListName(color + player.getName());
	}
	
	// ********************************************
	// returns the # of players on a team
	// ********************************************
	public int getTeamSize(String team) {
		return board.getTeam(team).getSize();
	}
	
	// ********************************************
	// gets the team a player is currently on
	// ********************************************
	public Team getPlayerTeam(Player player) {
		try {
			return board.getEntryTeam(player.getName());
		} catch (Exception f) {
			return null;
		}
	}
	
	// ********************************************
	// chooses a team for a player if the setting
	// LET_PLAYERS_PICK_TEAMS is FALSE
	// ********************************************
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
	
	public ChatColor getTeamColor(String team) {
		return board.getTeam(team).getColor();
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Player scoreboard commands
	//
	//	
	
	// ********************************************
	// sets the player's scoreboard
	// ********************************************
	public void setPlayerBoard(Player player) {
		try {
			player.setScoreboard(board);
		} catch (IllegalArgumentException f) {
			// board doesnt exist
		} catch (IllegalStateException g) {
			// player doesnt exist
		}
	}
	
	// ********************************************
	// unsets the players scoreboard
	// ********************************************
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
	
	// ********************************************
	// returns the player's score for an objective
	// ********************************************
	public int getPlayerScore(Player player, String objective) {
		return board.getObjective(objective).getScore(player.getName()).getScore();
	}
	
	// ********************************************
	// sets the player's score for an objective
	// ********************************************
	public void setPlayerScore(Player player, String objective, int score) {
		board.getObjective(objective).getScore(player.getName()).setScore(score);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Configuration commands
	//	
	//	

	// ********************************************
	// gets server's scoreboard manager and creates
	// a new scoreboard
	// ********************************************
	public void setManager() {
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
	}
	
	// ********************************************
	// 2-in-1 command to clear and reconfigure
	// scoreboard, teams, etc
	// ********************************************
	public void reloadScoreboards() {
		clearBoards();
		configureScoreboards();
	}
	
	// ********************************************
	// clears out the scoreboard entirely
	// ********************************************
	public void clearBoards() {
		board.getEntries().forEach(entry -> board.resetScores(entry));
		board.getTeams().forEach(team -> team.unregister());
		board.getObjectives().forEach(objective -> objective.unregister());
		
		TEAMS.clear();
		OBJECTIVES.clear();
	}
	
	// ********************************************
	// returns if a string is a valid DisplaySlot
	// ********************************************
	public boolean isValidDisplaySlot(String slot) {
		try {
			DisplaySlot.valueOf(slot);
			return true;
		} catch (Exception f) {
			return false;
		}
	}
	
	// ********************************************
	// configures the scoreboard objectives and
	// teams based on CONFIG
	// ********************************************
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
					String teamname = team.get("name").toString().toUpperCase();
					String teamcolor = team.get("color").toString().toUpperCase();
					
					board.registerNewTeam(teamname);
					TEAMS.put(teamname, board.getTeam(teamname));
					ChatColor color;
					
					try {
						color = ChatColor.valueOf(teamcolor);
						board.getTeam(teamname).setColor(color);
					} catch (Exception f) {
						color = ChatColor.STRIKETHROUGH;
						board.getTeam(teamname).setColor(color);
						NerdUHC.PLUGIN.getLogger().info("Config error: Invalid color option for team " + teamname);
					}
				});
				break;
		}

	} // end configureScoreboards()
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Fields
	//
	//
	
	// ********************************************
	// server's scoreboard manager
	// ********************************************
	private ScoreboardManager manager;
	
	// ********************************************
	// current scoreboard instance
	// ********************************************
	private Scoreboard board;
	
	// ********************************************
	// Map: objective name -> Objective
	// ********************************************
	public Map<String, Objective> OBJECTIVES = new HashMap<String, Objective>();
	
	// ********************************************
	// Map: team name -> Team
	// ********************************************
	public Map<String, Team> TEAMS = new HashMap<String, Team>();

} //ScoreboardHandler
