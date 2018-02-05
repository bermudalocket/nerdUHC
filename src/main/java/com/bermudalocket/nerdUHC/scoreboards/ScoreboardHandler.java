package com.bermudalocket.nerdUHC.scoreboards;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class ScoreboardHandler {

	private NerdUHC plugin;
	private UHCMatch match;
	
	private ScoreboardManager manager;
	private Scoreboard board;

	public ScoreboardHandler(NerdUHC plugin, UHCMatch match) {
		this.plugin = plugin;
		this.match = match;
		manager = Bukkit.getScoreboardManager();
	}

	public void createScoreboard() {
		board = manager.getNewScoreboard();
		
		match.setScoreboard(board);

		board.registerNewObjective("KILLS", Criterias.PLAYER_KILLS);
		board.registerNewObjective("HEALTH", Criterias.HEALTH).setDisplaySlot(DisplaySlot.PLAYER_LIST);
		board.registerNewObjective("HEALTHBELOWNAME", Criterias.HEALTH).setDisplaySlot(DisplaySlot.BELOW_NAME);
		board.getObjective("HEALTHBELOWNAME").setDisplayName(ChatColor.RED + "❤");
		board.registerNewObjective("main", "dummy").setDisplayName(ChatColor.BOLD + "NerdUHC");

		createTeams();
	}
	
	public void refresh() {
		UHCMatch match = plugin.matchHandler.getMatch();
		String title = board.getObjective("main").getDisplayName();
		if (match.getMatchState() == UHCMatchState.PREGAME) {
			showTeamCountCapacity();
		} else {
			showTeamsLeft();
		}
		forceHealthUpdates();
		board.getObjective("main").setDisplayName(title);
	}

	// PRE-GAME ONLY
	public void showTeamCountCapacity() {
		board.getObjective("main").unregister();
		Objective o = board.registerNewObjective("main", "dummy");
		o.setDisplayName(ChatColor.BOLD + "NerdUHC");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.getScore("=-=-=-=-=-=-=-=").setScore(2);
		o.getScore("Teams:").setScore(1);
		for (Team t : board.getTeams()) {
			String line = t.getDisplayName() + ChatColor.WHITE;
			line += " (" + t.getSize() + "/" + plugin.CONFIG.MAX_TEAM_SIZE + ")";
			o.getScore(line).setScore(0);
		}
	}

	// DURING GAME ONLY
	public void showTeamsLeft() {
		ArrayList<String> lines = new ArrayList<String>();

		board.getObjective("main").unregister();
		Objective o = board.registerNewObjective("main", "dummy");
		
		if (match.getMatchState() == UHCMatchState.DEATHMATCH) {
			o.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "Deathmatch");
		}
		
		lines.add(ChatColor.WHITE + "=-=-=-=-=-=-=-=");
		lines.add(ChatColor.AQUA + "" + ChatColor.ITALIC + "Teams Left:");
		
		if (board.getTeams().size() > 0) {
			for (Team t : board.getTeams()) {
				if (t.getSize() > 0) {
					lines.add(t.getDisplayName() + ": " + t.getSize());
				}
			}
		} else {
			lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "None!");
		}
		
		for (int k = 0; k < lines.size(); k++) {
			o.getScore(lines.get(k)).setScore(lines.size() - k - 1);
		}
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public void forceHealthUpdates() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (player.getHealth() != 0) player.setHealth(player.getHealth());
		});
	}

	public void createTeams() {
		for (Map<?, ?> map : plugin.CONFIG.getRawTeamList()) {
			String name = map.get("name").toString().toUpperCase();
			ChatColor color = ChatColor.valueOf(map.get("color").toString());
			Team t = board.registerNewTeam(name);
			t.setColor(color);
			t.setPrefix(color + "");
			t.setSuffix("" + ChatColor.WHITE);
			t.setDisplayName(color + name + ChatColor.WHITE);
		}
	}
	
	public void pruneTeams() {
		for (Team t : board.getTeams()) {
			if (t.getSize() == 0) {
				t.unregister();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean isPlayerOnBoard(Player p) {
		return board.getPlayerTeam(p) != null;
	}

}
