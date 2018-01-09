package com.bermudalocket.nerdUHC;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.modules.Match.UHCGameMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class ScoreboardHandler {
	
	private NerdUHC plugin;
	private UHCGameMode uhcgamemode;
	
	private ScoreboardManager manager;
	private Scoreboard board;
	
	private Map<String, Objective> OBJECTIVES = new HashMap<String, Objective>();
	private Map<String, Team> TEAMS = new HashMap<String, Team>();
	
	public ScoreboardHandler(NerdUHC plugin) {
		
		this.plugin = plugin;
		
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		
		uhcgamemode = plugin.match.getGameMode();
		
		populateObjectives();
		setFormattingHealthBelowName();
		checkForDeathObjective();
		createTeams();
		
	}
	
	private void populateObjectives() {
		plugin.CONFIG.rawobjectiveslist.forEach(objective -> {
			String objname = objective.get("name").toString();
			String objcriteria = objective.get("criteria").toString();
			String objdisplayslot = objective.get("displayslot").toString();
			
			if (objcriteria != null && isValidDisplaySlot(objdisplayslot)) {
				board.registerNewObjective(objname.toUpperCase(), objcriteria).setDisplaySlot(DisplaySlot.valueOf(objdisplayslot));
				OBJECTIVES.put(objname.toUpperCase(), board.getObjective(objname.toUpperCase()));
			}
		});
	}
	
	
	private void setFormattingHealthBelowName() {
		List<Objective> healthobj = board.getObjectives()
											.stream()
											.filter(obj -> obj.getDisplaySlot().equals(DisplaySlot.BELOW_NAME)
														&& obj.getCriteria().equals(Criterias.HEALTH))
											.collect(Collectors.toList());
		
		if (healthobj == null) return;
		
		healthobj.get(0).setDisplayName(ChatColor.RED + "❤");
	}
	
	
	private void checkForDeathObjective() {
		List<Objective> deathobj = board.getObjectives()
										.stream()
										.filter(obj -> obj.getCriteria().equals(Criterias.DEATHS))
										.collect(Collectors.toList());

		if (deathobj == null) {
			board.registerNewObjective("DEATHS", "deathCount");
			plugin.CONFIG.DEATH_OBJECTIVE_NAME = "DEATHS";
			OBJECTIVES.put("DEATHS", board.getObjective("DEATHS"));
		} else {
			plugin.CONFIG.DEATH_OBJECTIVE_NAME = deathobj.get(0).getName();
		}
	}
	
	
	public void forceHealthUpdates() {
		Bukkit.getOnlinePlayers().forEach(player -> player.setHealth(player.getHealth()));
	}
	

	private void createTeams() {
		switch (uhcgamemode) {
			default:
			case SOLO:
				TEAMS.put(plugin.CONFIG.ALIVE_TEAM_NAME.toUpperCase(), board.registerNewTeam(plugin.CONFIG.ALIVE_TEAM_NAME));
				TEAMS.put(plugin.CONFIG.DEAD_TEAM_NAME.toUpperCase(), board.registerNewTeam(plugin.CONFIG.DEAD_TEAM_NAME));
				break;
			case TEAM:
				plugin.CONFIG.rawteamlist.forEach(team -> {
					String teamname = team.get("name").toString().toUpperCase();
					String teamcolor = team.get("color").toString().toUpperCase();
					
					board.registerNewTeam(teamname);
					TEAMS.put(teamname, board.getTeam(teamname));
					ChatColor color;
					
					try {
						color = ChatColor.valueOf(teamcolor);
						board.getTeam(teamname).setColor(color);
						board.getTeam(teamname).setPrefix(color + "");
						board.getTeam(teamname).setSuffix(ChatColor.WHITE + "");
					} catch (Exception f) {
						color = ChatColor.STRIKETHROUGH;
						board.getTeam(teamname).setColor(color);
						plugin.getLogger().info("Config error: Invalid color option for team " + teamname);
					}
				});
				break;
		}

	}

	public Set<String> getTeams() {
		return TEAMS.keySet();
	}
	
	public boolean chooseTeamForPlayer(UUID player) {
		Player p = Bukkit.getPlayer(player);
		Optional<Team> foundteam = board.getTeams().stream().
													filter(team -> team.getSize() < plugin.CONFIG.MAX_TEAM_SIZE).
													findFirst();
		
		if (foundteam.orElse(null) != null) {
			String t = foundteam.get().getName();
			
			setPlayerTeam(player, t);
			p.sendMessage(ChatColor.GRAY + "Team set to " + t);
			return true;
		} else {
			p.sendMessage(ChatColor.GRAY + "Sorry, there are no available teams to join.");
			return false;
		}
	}
	
	public boolean teamExists(String team) {
		return TEAMS.containsKey(team) ? true : false;
	}
	
	public boolean isTeamFull(String team) {
		return (plugin.scoreboardHandler.getTeamSize(team) < plugin.CONFIG.MAX_TEAM_SIZE) ? true : false;
	}
	
	public void setPlayerTeam(UUID player, String team) {
		Player p = Bukkit.getPlayer(player);
		board.getTeam(team).addEntry(p.getName());
	}
	
	public void removePlayerTeam(Player player) {
		board.getEntryTeam(player.getName()).removeEntry(player.getName());
		player.setDisplayName(ChatColor.WHITE + player.getName());
		player.setPlayerListName(ChatColor.WHITE + player.getName());
	}

	public int getTeamSize(String team) {
		return board.getTeam(team).getSize();
	}
	
	public ChatColor getTeamColor(String team) {
		return board.getTeam(team).getColor();
	}

	public void setPlayerBoard(UUID player) {
		Player p = Bukkit.getPlayer(player);
		p.setScoreboard(board);
	}

	public void clearBoards() {
		board.getEntries().forEach(entry -> board.resetScores(entry));
		board.getTeams().forEach(team -> team.unregister());
		board.getObjectives().forEach(objective -> objective.unregister());
		
		TEAMS.clear();
		OBJECTIVES.clear();
	}
	
	public boolean isValidDisplaySlot(String slot) {
		try {
			DisplaySlot.valueOf(slot);
			return true;
		} catch (Exception f) {
			return false;
		}
	}

} //ScoreboardHandler
