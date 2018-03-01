package com.bermudalocket.nerdUHC.scoreboards;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

import java.util.*;

import com.bermudalocket.nerdUHC.modules.UHCSound;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class ScoreboardHandler {

	private final NerdUHC plugin;
	private final UHCMatch match;
	
	private final ScoreboardManager manager;
	private Scoreboard board;

	private final Random random = new Random();
	private static final ArrayList<Biome> excludedBiomes = new ArrayList<>(Arrays.asList(
			Biome.OCEAN, Biome.DEEP_OCEAN, Biome.FROZEN_OCEAN));
	private static final ArrayList<Material> excludedBlocks = new ArrayList<>(Arrays.asList(
			Material.LAVA, Material.STATIONARY_LAVA, Material.WATER, Material.STATIONARY_WATER,
			Material.LEAVES, Material.LEAVES_2, Material.CACTUS));

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
	private void showTeamCountCapacity() {
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
	private void showTeamsLeft() {
		ArrayList<String> lines = new ArrayList<>();

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

	private void createTeams() {
		for (Map<?, ?> map : plugin.CONFIG.getRawTeamList()) {
			String name = map.get("name").toString().toUpperCase();
			ChatColor color = ChatColor.valueOf(map.get("color").toString());
			Team t = board.registerNewTeam(name);
			t.setColor(color);
			t.setPrefix(color + "");
			t.setSuffix("" + ChatColor.WHITE);
			t.setDisplayName(color + name + ChatColor.WHITE);
			t.setAllowFriendlyFire(plugin.CONFIG.ALLOW_FRIENDLY_FIRE);
		}
	}
	
	public void pruneTeams() {
		for (Team t : board.getTeams()) {
			if (t.getSize() == 0) {
				t.unregister();
			}
		}
		refresh();
	}

	public void addPlayerToTeam(Player p, String teamName) {
		Team t = board.getTeam(teamName);
		if (t == null) return;

		t.addEntry(p.getName());
		refresh();

		p.setDisplayName(t.getColor() + p.getName());
		p.setPlayerListName(t.getColor() + p.getName());

		UHCSound.JOINTEAM.playSound(p);
		p.sendMessage("You joined the " + t.getDisplayName() + " team!");
	}

	public void spreadPlayers() {
		HashMap<Team, Location> teamLocations = new HashMap<>();

		for (Player p : Bukkit.getOnlinePlayers()) {

			Team t = board.getEntryTeam(p.getName());

			if (teamLocations.containsKey(t)) {
				p.teleport(teamLocations.get(t));
				continue;
			}

			if (t == null) {
				p.setGameMode(GameMode.SPECTATOR);
				continue;
			}

			if (match.getMatchState() != UHCMatchState.DEATHMATCH) {
				match.resetPlayer(p, true);
			}

			int multiplier; // inverse
			if (match.getMatchState() == UHCMatchState.DEATHMATCH) {
				multiplier = 20;
			} else {
				multiplier = 2;
			}

			World world = match.getWorld();
			int maxDistance = (int) world.getWorldBorder().getSize()/multiplier - 100;

			boolean unsafe = true;
			while (unsafe) {
				int x = maxDistance * (random.nextInt(100) / 100);
				int z = maxDistance * (random.nextInt(100) / 100);
				int y = match.getWorld().getHighestBlockYAt(x, z);

				Biome newBiome = match.getWorld().getBiome(x, z);
				if (excludedBiomes.contains(newBiome)) continue;

				Material newBlock = match.getWorld().getBlockAt(x, y, z).getType();
				if (excludedBlocks.contains(newBlock)) continue;

				Location newLoc = new Location(match.getWorld(), x, y, z);
				p.teleport(newLoc);

				if (plugin.CONFIG.SPREAD_RESPECT_TEAMS) teamLocations.put(t, newLoc);

				unsafe = false;
			}
		}
		refresh();
	}

}