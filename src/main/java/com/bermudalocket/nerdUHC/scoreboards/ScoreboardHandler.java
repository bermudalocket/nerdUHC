package com.bermudalocket.nerdUHC.scoreboards;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class ScoreboardHandler {

	private NerdUHC plugin;

	private ScoreboardManager manager;
	private HashMap<UHCMatch, Scoreboard> scoreboards = new HashMap<UHCMatch, Scoreboard>();

	public ScoreboardHandler(NerdUHC plugin) {
		this.plugin = plugin;
		manager = Bukkit.getScoreboardManager();
	}

	public void createScoreboard(UHCMatch match) {
		Scoreboard board = configuredScoreboard(match);
		match.setScoreboard(board);
		scoreboards.put(match, board);
		
		createTeams(match);
		setBoardForPlayers(match);
	}

	public Scoreboard configuredScoreboard(UHCMatch match) {
		Scoreboard board = manager.getNewScoreboard();

		board.registerNewObjective("DEATHS", Criterias.DEATHS);
		board.registerNewObjective("KILLS", Criterias.TOTAL_KILLS);
		board.registerNewObjective("HEALTH", Criterias.HEALTH).setDisplaySlot(DisplaySlot.PLAYER_LIST);
		board.registerNewObjective("HEALTHBELOWNAME", Criterias.HEALTH).setDisplaySlot(DisplaySlot.BELOW_NAME);
		board.getObjective("HEALTHBELOWNAME").setDisplayName(ChatColor.RED + "❤");
		board.registerNewObjective("main", "dummy").setDisplayName(ChatColor.BOLD + "NerdUHC");

		return board;
	}

	public void setBoardForPlayers(UHCMatch match) {
		for (UUID uuid : match.getPlayers()) {
			Player p = Bukkit.getPlayer(uuid);
			p.setScoreboard(match.getScoreboard());
		}
	}

	// PRE-GAME ONLY
	public void showTeamCountCapacity(UHCMatch match) {
		Scoreboard board = match.getScoreboard();
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
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}

	// DURING GAME ONLY
	@SuppressWarnings("deprecation")
	public void showKills(UHCMatch match) {
		
		Scoreboard board = match.getScoreboard();
		board.getObjective("main").unregister();
		
		Objective o = board.registerNewObjective("main", "dummy");
		
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("=-=-=-=-=-=-=-=");
		lines.add(ChatColor.RED + "" + ChatColor.ITALIC + "Top Killers:");
		
		HashMap<Player, Integer> playerkills = new HashMap<Player, Integer>();
		for (UUID uuid : match.getPlayers()) {
			Player p = Bukkit.getPlayer(uuid);
			int kills = p.getStatistic(Statistic.PLAYER_KILLS);
			playerkills.put(p, kills);
		}
		playerkills = (HashMap<Player, Integer>) sortByValue(playerkills);
		
		int i = 0;
		for (Map.Entry<Player, Integer> entry : playerkills.entrySet()) {
			if (i >= 5) return;
			
			String name = entry.getKey().getDisplayName() + ChatColor.WHITE;
			if (entry.getKey().isDead()) {
				name = ChatColor.STRIKETHROUGH + name;
			}
			
			lines.add(name + ": " + entry.getValue());
			i++;
		}
		
		lines.add(ChatColor.WHITE + "=-=-=-=-=-=-=-=");
		lines.add(ChatColor.AQUA + "" + ChatColor.ITALIC + "Teams Left:");
		
		for (Team t : board.getTeams()) {
			int j = 0;
			for (OfflinePlayer e : t.getPlayers()) {
				Player p = e.getPlayer();
				if (p.isOnline() && p.getGameMode() == GameMode.SURVIVAL) {
					j++;
				}
			}
			if (j > 0) {
				lines.add(t.getDisplayName() + ": " + j);
			}
		}
		
		for (int k = 0; k < lines.size(); k++) {
			o.getScore(lines.get(k)).setScore(lines.size() - k - 1);
		}
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public void forceHealthUpdates() {
		Bukkit.getOnlinePlayers().forEach(player -> player.setHealth(player.getHealth()));
	}

	public void hideSidebar(UHCMatch match) {
		match.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplaySlot(null);
	}

	public void showSidebar(UHCMatch match) {
		match.getScoreboard().getObjective("main").setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public void createTeams(UHCMatch match) {
		for (Map<?, ?> map : plugin.CONFIG.getRawTeamList()) {
			String name = map.get("name").toString().toUpperCase();
			ChatColor color = ChatColor.valueOf(map.get("color").toString());
			Team t = match.getScoreboard().registerNewTeam(name);
			t.setColor(color);
			t.setPrefix(color + "");
			t.setSuffix("" + ChatColor.WHITE);
			t.setDisplayName(color + name + ChatColor.WHITE);
		}
	}

	public void startDeathmatch(UHCMatch match) {
		match.getScoreboard().getObjective("main").setDisplayName(ChatColor.BOLD + "" + ChatColor.RED + "Deathmatch");
	}

}
