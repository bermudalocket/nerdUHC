package com.bermudalocket.nerdUHC.scoreboards;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.events.MatchStateChangeEvent;
import com.bermudalocket.nerdUHC.events.MatchTimerTickEvent;
import com.bermudalocket.nerdUHC.events.PlayerChangeTeamEvent;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCTeam;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class ScoreboardHandler implements Listener {
	
	private NerdUHC plugin;
	
	private ScoreboardManager manager;
	private List<Scoreboard> boards = new ArrayList<Scoreboard>();
	
	// ------------------------------------------------------------------------
	/**
	 * The constructor for ScoreboardHandler
	 * @param plugin
	 */
	public ScoreboardHandler(NerdUHC plugin) {
		this.plugin = plugin;
		manager = Bukkit.getScoreboardManager();
		configureNewScoreboard(false);
	}
	
	// ------------------------------------------------------------------------
	/**
	 * Sets player's scoreboard upon player join, then displays it
	 * @param e
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		BukkitRunnable PlayerJoinTask = new BukkitRunnable() {
            @Override
            public void run() {
	            	e.getPlayer().setScoreboard(boards.get(0));
	        		if (plugin.match.isGameStarted()) {
	        			showTeamsKillsAndTimer();
	        		} else {
	        			showTeamCountCapacity();
	            }
            }
        };
        PlayerJoinTask.runTaskLater(plugin, 1);
	}
	
	/**
	 * Updates the scoreboard and health displays when a player changes team
	 * @param e
	 */
	@EventHandler
	public void onPlayerChangeTeam(PlayerChangeTeamEvent e) {
		if (!plugin.match.isGameStarted()) showTeamCountCapacity();
		forceHealthUpdates();
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (plugin.match.isGameStarted()) {
			showTeamsKillsAndTimer();
		}
	}
	
	@EventHandler
	public void onMatchStateChange(MatchStateChangeEvent e) {
		if (e.getState().equals(UHCMatchState.DEATHMATCH)) {
			boards.get(0).getObjective("main").setDisplayName(ChatColor.BOLD + "" + ChatColor.RED + "Deathmatch");
		} else if (e.getState().equals(UHCMatchState.INPROGRESS)) {
		}
	}
	
	@EventHandler 
	public void onMatchTimerTick(MatchTimerTickEvent e) {
		if (plugin.match.isGameStarted()) {
			showTeamsKillsAndTimer();
		} else {
			showTeamCountCapacity();
		}
	}
	
	//
	// SETUP
	//
	
	public void configureNewScoreboard(boolean garbage) {
		if (boards.size() == 1) boards.remove(0);
		Scoreboard board = manager.getNewScoreboard();
		if (!garbage) {
			board.registerNewObjective("DEATHS", Criterias.DEATHS);
			board.registerNewObjective("KILLS", Criterias.TOTAL_KILLS);
			board.registerNewObjective("HEALTH", Criterias.HEALTH).setDisplaySlot(DisplaySlot.PLAYER_LIST);
			board.registerNewObjective("HEALTHBELOWNAME", Criterias.HEALTH).setDisplaySlot(DisplaySlot.BELOW_NAME);
			board.getObjective("HEALTHBELOWNAME").setDisplayName(ChatColor.RED + "❤");
			board.registerNewObjective("main", "dummy").setDisplayName(ChatColor.BOLD + "NerdUHC");
			boards.add(board);
			
			if (Bukkit.getOnlinePlayers().size() > 0) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.setScoreboard(board);
				}
				showTeamCountCapacity();
			}
		}
	}
	
	// PRE-GAME ONLY
	public void showTeamCountCapacity() {
		boards.get(0).getObjective("main").unregister();
		Objective o = boards.get(0).registerNewObjective("main", "dummy");
		o.setDisplayName(ChatColor.BOLD + "NerdUHC");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		int currteams = plugin.match.getTeams().size();
		
		o.getScore( "----------------").setScore(currteams);
		o.getScore("Team (Curr./Max)").setScore(currteams);
		int i = 0;
		for (UHCTeam t : plugin.match.getTeams()) {
			o.getScore(t.getColor() + t.getName() + " " + ChatColor.WHITE + " (" + t.getSize() + "/" + t.getMaxSize() + ")").setScore(i);
			i++;
		}
	}
	
	// DURING GAME ONLY
	public void showTeamsKillsAndTimer() {
		boards.get(0).getObjective("main").unregister();
		Objective o = boards.get(0).registerNewObjective("main", "dummy");
		o.setDisplayName(ChatColor.BOLD + "NerdUHC");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		int currteams = plugin.match.getTeams().size();
		int i = 0;
		o.getScore(ChatColor.WHITE + "----------------").setScore(currteams);
		for (UHCTeam team : plugin.match.getTeams()) {
			StringBuilder alive = new StringBuilder();
			StringBuilder dead = new StringBuilder();
			int aliveplayers = team.getAlivePlayers();
			for (int j = 0; j < aliveplayers; j++) {
				alive.append("❤ ");
			}
			int deadplayers = team.getSize() - aliveplayers;
			for (int k = 0; k < deadplayers; k++) {
				dead.append("X ");
			}
			o.getScore(team.getColor() + team.getName() + ": " + ChatColor.WHITE + alive.toString() + dead.toString()).setScore(i);
			i++;
		}
	}

	public void forceHealthUpdates() {
		Bukkit.getOnlinePlayers().forEach(player -> player.setHealth(player.getHealth()));
	}
	
	public void hideSidebar() {
		boards.get(0).getObjective(DisplaySlot.SIDEBAR).setDisplaySlot(null);
	}
	
	public void showSidebar() {
		boards.get(0).getObjective("main").setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public Team registerTeam(UHCTeam team) {
		Team t = boards.get(0).registerNewTeam(team.getName());
		t.setColor(team.getColor());
		return t;
	}
	
	protected Objective getObjective(String name) {
		return boards.get(0).getObjective(name);
	}
	
	public boolean isValidDisplaySlot(String slot) {
		try {
			DisplaySlot.valueOf(slot);
			return true;
		} catch (Exception f) {
			return false;
		}
	}

}
