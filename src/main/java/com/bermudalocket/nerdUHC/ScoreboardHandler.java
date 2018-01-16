package com.bermudalocket.nerdUHC;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.modules.UHCTeam;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class ScoreboardHandler implements Listener {
	
	private NerdUHC plugin;
	
	private ScoreboardManager manager;
	private Scoreboard board;
	
	public ScoreboardHandler(NerdUHC plugin, MatchHandler match) {
		this.plugin = plugin;
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		populateObjectives();
	}
	
	//
	// LISTENERS
	//
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		BukkitRunnable PlayerJoinTask = new BukkitRunnable() {
            @Override
            public void run() {
	            	e.getPlayer().setScoreboard(board);
	        		if (plugin.match.isGameStarted()) {
	        			showTeamsKillsAndTimer();
	        		} else {
	        			showTeamCountCapacity();
	            }
            }
        };
        PlayerJoinTask.runTaskLater(plugin, 1);
	}
	
	//
	// SETUP
	//
	
	private void populateObjectives() {
		board.registerNewObjective("DEATHS", Criterias.DEATHS);
		board.registerNewObjective("KILLS", Criterias.TOTAL_KILLS);
		board.registerNewObjective("HEALTH", Criterias.HEALTH).setDisplaySlot(DisplaySlot.PLAYER_LIST);
		board.registerNewObjective("HEALTHBELOWNAME", Criterias.HEALTH).setDisplaySlot(DisplaySlot.BELOW_NAME);
		board.getObjective("HEALTHBELOWNAME").setDisplayName(ChatColor.RED + "❤");
		board.registerNewObjective("main", "dummy").setDisplayName(ChatColor.BOLD + "NerdUHC");
	}
	
	public void update() {
		if (plugin.match.isGameStarted()) {
			showTeamsKillsAndTimer();
		} else {
			showTeamCountCapacity();
		}
	}
	
	// PRE-GAME ONLY
	public void showTeamCountCapacity() {
		
		if (plugin.match.isGameStarted()) return;
		
		board.getObjective("main").unregister();
		Objective o = board.registerNewObjective("main", "dummy");
		o.setDisplayName(ChatColor.BOLD + "NerdUHC");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		int currteams = plugin.match.getTeams().size();
		
		o.getScore( "----------------").setScore(currteams + 1);
		o.getScore("Team (Curr./Max)").setScore(currteams);
		for (int i=0; i < currteams; i++) {
			UHCTeam team = (UHCTeam) plugin.match.getTeams().toArray()[i];
			o.getScore(team.getColor() + team.getName() + " " + ChatColor.WHITE + " (" + team.getSize() + "/" + team.getMaxSize() + ")").setScore(i);
		}

	}
	
	// DURING GAME ONLY
	public void showTeamsKillsAndTimer() {
		
		if (!plugin.match.isGameStarted()) return;
		
		board.getObjective("main").unregister();
		Objective o = board.registerNewObjective("main", "dummy");
		o.setDisplayName(ChatColor.BOLD + "NerdUHC");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		int currteams = plugin.match.getTeams().size();
		int i = 1;
		o.getScore(ChatColor.WHITE + "----------------").setScore(currteams + 1);
		String alive = "";
		String dead = "";
		for (UHCTeam team : plugin.match.getTeams()) {
			int aliveplayers = team.getAlivePlayers();
			plugin.getLogger().info("alive = " + aliveplayers);
			for (int j = 0; j < aliveplayers; j++) {
				alive.concat("❤");
			}
			int deadplayers = team.getSize() - aliveplayers;
			plugin.getLogger().info("dead = " + deadplayers);
			for (int k = 0; k < deadplayers; k++) {
				dead.concat("\u2620");
			}
			o.getScore(team.getColor() + team.getName() + ": " + ChatColor.WHITE + alive + dead).setScore(i);
			i++;
		}
	}
	
	public BukkitRunnable MatchTimer = new BukkitRunnable() {
		
		String timedisplay;
		ChatColor color = ChatColor.WHITE;
		long nexttime;
		
        @Override
        public void run() {
        	
        		if (!plugin.match.isGameStarted()) this.cancel();
        		if (plugin.match.arePlayersFrozen()) plugin.match.extendTime(1);
        		
        		nexttime = plugin.match.getTimeEnd() - System.currentTimeMillis();
        		timedisplay = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(nexttime),
        	            TimeUnit.MILLISECONDS.toMinutes(nexttime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(nexttime)),
        	            TimeUnit.MILLISECONDS.toSeconds(nexttime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(nexttime)));
        		
        		if (nexttime <= 600000) color = ChatColor.RED;
        		
        		try {
        			board.getObjective("main").setDisplayName(color + "" + ChatColor.BOLD + timedisplay);
        			if (TimeUnit.MILLISECONDS.toSeconds(nexttime) == 10) {
        				plugin.match.stopUHC();	// 10 second countdown
        			}
        		} catch(Exception e) {
        			// small hiccup, objective just happened to get unregistered
        		}
        		
        		
        }
    };
	
	public void forceHealthUpdates() {
		Bukkit.getOnlinePlayers().forEach(player -> player.setHealth(player.getHealth()));
	}
	
	public void forceKillsUpdates(UUID player) {
		String playername = Bukkit.getPlayer(player).getName();
		board.getObjective("KILLS").getScore(playername).setScore(0);
	}
	
	//
	// TEAMS
	//
	
	public void registerTeam(UHCTeam team) {
		board.registerNewTeam(team.getName());
	}
	
	//
	// SCOREBOARD (meta)
	//

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
	
	public void refreshScoreboard() {
		this.board = manager.getNewScoreboard();
		populateObjectives();
	}
	
	public void hideSidebar() {
		board.getObjective(DisplaySlot.SIDEBAR).setDisplaySlot(null);
	}
	
	public void showSidebar() {
		board.getObjective("main").setDisplaySlot(DisplaySlot.SIDEBAR);
	}

}
