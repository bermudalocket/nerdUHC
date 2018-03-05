package com.bermudalocket.nerdUHC.scoreboards;

import java.util.*;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardHandler {

	private final NerdUHC plugin;
	private final UHCMatch match;

	private Scoreboard board;
	private final ArrayList<String> boardLines = new ArrayList<>();

	private static final String HEART = ChatColor.RED + "❤";
	private static final String LOBBY = String.format("%sNerdUHC%s - %s%sLOBBY",
		ChatColor.BOLD, ChatColor.RESET, ChatColor.AQUA, ChatColor.ITALIC);

	private final Random random = new Random();
	private static final ArrayList<Biome> excludedBiomes = new ArrayList<>(Arrays.asList(
			Biome.OCEAN, Biome.DEEP_OCEAN, Biome.FROZEN_OCEAN));
	private static final ArrayList<Material> excludedBlocks = new ArrayList<>(Arrays.asList(
			Material.LAVA, Material.STATIONARY_LAVA, Material.WATER, Material.STATIONARY_WATER,
			Material.LEAVES, Material.LEAVES_2, Material.CACTUS));

	// ----------------------------------------------------------------

	public ScoreboardHandler(UHCMatch match) {
		this.plugin = NerdUHC.plugin;
		this.match = match;
		createScoreboard();
	}

	// ----------------------------------------------------------------

	private void createScoreboard() {
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		match.setScoreboard(board);

		board.registerNewObjective("KILLS", Criterias.PLAYER_KILLS);
		board.registerNewObjective("HEALTH", Criterias.HEALTH).setDisplaySlot(DisplaySlot.PLAYER_LIST);
		board.registerNewObjective("HEALTHBELOWNAME", Criterias.HEALTH).setDisplaySlot(DisplaySlot.BELOW_NAME);
		board.getObjective("HEALTHBELOWNAME").setDisplayName(HEART);
		board.registerNewObjective("main", "dummy").setDisplayName(LOBBY);
		board.getObjective("main").setDisplaySlot(DisplaySlot.SIDEBAR);

		createTeams();
	}

	private void createTeams() {
		for (Map<?, ?> map : plugin.config.getRawTeamList()) {
			String name = map.get("name").toString().toUpperCase();
			ChatColor color = ChatColor.valueOf(map.get("color").toString());
			Team t = board.registerNewTeam(name);
			t.setColor(color);
			t.setPrefix(color + "");
			t.setSuffix("" + ChatColor.WHITE);
			t.setDisplayName(color + name + ChatColor.WHITE);
			t.setAllowFriendlyFire(plugin.config.ALLOW_FRIENDLY_FIRE);
		}
	}
	
	public void pruneTeams() {
		for (Team t : board.getTeams()) {
			if (t.getSize() == 0) t.unregister();
		}
		refresh();
	}

	public boolean teamIsJoinable(UHCMatch match, String team) {
		if (match.getMatchState() == UHCMatchState.PREGAME) {
			Team t = board.getTeam(team);
			return t != null && t.getSize() < plugin.config.MAX_TEAM_SIZE;
		}
		return false;
	}

	// ----------------------------------------------------------------

	private void forceHealthUpdates() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (player.getHealth() != 0) player.setHealth(player.getHealth());
		});
	}

	public void addPlayerToTeam(Player p, String teamName) {
		Team t = board.getTeam(teamName);
		if (t == null) return;

		t.addEntry(p.getName());
		refresh();

		p.setDisplayName(t.getColor() + p.getName());
		p.setPlayerListName(t.getColor() + p.getName());

		if (p.getGameMode() == GameMode.SPECTATOR) p.setGameMode(GameMode.SURVIVAL);

		UHCSound.JOINTEAM.playSound(p);
		p.sendMessage("You joined the " + t.getDisplayName() + " team!");
	}

	public void addPlayerRandomTeam(Player p) {
		Boolean foundTeam = false;

		List<Team> teamList = new ArrayList<>(board.getTeams());

		while (!foundTeam) {
			Integer j = random.nextInt(teamList.size());

			Team t = teamList.get(j);
			String teamName = t.getName();

			if (t.getSize() < plugin.config.MAX_TEAM_SIZE) {
				match.getScoreboardHandler().addPlayerToTeam(p, teamName);
				foundTeam = true;
			} else {
				teamList.remove((int) j);
			}
			if (teamList.size() == 0) {
				p.sendMessage(ChatColor.RED + "Sorry, no teams are available to join.");
				foundTeam = true;
			}
		}
		refresh();
	}

	public void removePlayerFromTeam(Player p, boolean spectateNext) {
		if (!hasTeam(p)) return;
		board.getEntryTeam(p.getName()).removeEntry(p.getName());
		if (match.getMatchState() != UHCMatchState.PREGAME) pruneTeams();
		if (spectateNext) makeSpectator(p);
	}

	public boolean hasTeam(Player p) {
		return board.getEntryTeam(p.getName()) != null;
	}

	public void makeSpectator(Player p) {
		if (hasTeam(p)) removePlayerFromTeam(p, false);
		UHCSound.JOINTEAM.playSound(p);
		p.setAllowFlight(true);
		p.setGameMode(GameMode.SPECTATOR);
		p.setFlying(true);
		p.setDisplayName(ChatColor.GRAY + "" + ChatColor.ITALIC + p.getName() + ChatColor.WHITE);
		UHCLibrary.LIB_NOW_SPECTATING.tell(p);
		if (match.getMatchState() == UHCMatchState.PREGAME) {
			UHCLibrary.LIB_TO_EXIT_SPEC.tell(p);
		}
		refresh();
	}

	public void formatDisplayName(Player p) {
		Team t = board.getEntryTeam(p.getName());
		ChatColor newColor = (t == null) ? ChatColor.RESET : t.getColor();
		p.setPlayerListName(newColor + p.getName());
		p.setDisplayName(newColor + p.getName() + ChatColor.WHITE);
	}

	// ----------------------------------------------------------------

	public void spreadPlayers() {
		HashMap<Team, Location> teamLocations = new HashMap<>();

		for (Player p : Bukkit.getOnlinePlayers()) {
			Team t = board.getEntryTeam(p.getName());
			if (teamLocations.containsKey(t)) {
				safeTeleport(p, teamLocations.get(t));
				continue;
			}
			if (t == null) {
				p.setGameMode(GameMode.SPECTATOR);
				continue;
			}
			if (match.getMatchState() != UHCMatchState.DEATHMATCH) match.resetPlayer(p);

			int multiplier; // inverse
			if (match.getMatchState() == UHCMatchState.DEATHMATCH) {
				multiplier = 20;
			} else {
				multiplier = 2;
			}

			World world = match.getWorld();
			int maxDistance = (int) (world.getWorldBorder().getSize()/multiplier) - 100;

			boolean unsafe = true;
			while (unsafe) {
				double mult1 = Math.random();
				double mult2 = Math.random();
				int sign1 = (Math.random() < 0.5) ? 1 : -1;
				int sign2 = (Math.random() < 0.5) ? 1 : -1;

				int x = (int) (sign1 * maxDistance * mult1);
				int z = (int) (sign2 * maxDistance * mult2);
				int y = world.getHighestBlockYAt(x, z);

				Biome newBiome = match.getWorld().getBiome(x, z);
				if (excludedBiomes.contains(newBiome)) continue;

				Material newBlock = match.getWorld().getBlockAt(x, y, z).getType();
				if (excludedBlocks.contains(newBlock)) continue;

				Location newLoc = new Location(match.getWorld(), x, y, z);
				safeTeleport(p, newLoc);

				if (plugin.config.SPREAD_RESPECT_TEAMS) teamLocations.put(t, newLoc);

				unsafe = false;
			}
		}
		refresh();
	}

	private void safeTeleport(Player p, Location l) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 100));
		p.teleport(l);
	}

	// ----------------------------------------------------------------

	public void refresh() {
		if (match.getMatchState() == UHCMatchState.PREGAME) showLobbyInfo(); else showTeamsRemaining();
		forceHealthUpdates();
	}

	private void resetLines() {
		for (String s : boardLines) board.resetScores(s);
		boardLines.clear();
	}

	private void buildLines() {
		int n = boardLines.size();
		for (String s : boardLines) board.getObjective("main").getScore(s).setScore(n--);
	}

	private void showLobbyInfo() {
		resetLines();
		boardLines.add(ChatColor.RESET.toString());
		boardLines.add("Teams:");
		for (Team t : board.getTeams()) {
			String teamLine = t.getDisplayName() +
					ChatColor.WHITE +
					" (" +
					t.getSize() +
					"/" +
					plugin.config.MAX_TEAM_SIZE +
					")";
			boardLines.add(teamLine);
		}
		buildLines();
	}

	private void showTeamsRemaining() {
		resetLines();
		boardLines.add(ChatColor.RESET.toString());
		boardLines.add(String.format("%s%sTeams Left:", ChatColor.AQUA, ChatColor.ITALIC));
		for (Team t : board.getTeams()) {
			StringBuilder teamLine = new StringBuilder();
			teamLine.append(t.getDisplayName())
					.append(ChatColor.WHITE)
					.append(": ");
					for (int i = 0; i < t.getSize(); i++) {
						teamLine.append(ChatColor.RED).append("❤");
					}
			boardLines.add(teamLine.toString());
		}
		if (boardLines.size() == 2) {
			boardLines.add(String.format("%s%sNone!", ChatColor.GRAY, ChatColor.ITALIC));
		}
		boardLines.add(ChatColor.RESET.toString());

		String pvpState = (match.getWorld().getPVP()) ? "enabled" : "disabled";
		String pvpInfo = String.format("%s%sPVP%s: %s",
			ChatColor.YELLOW, ChatColor.ITALIC, ChatColor.RESET, pvpState);
		boardLines.add(pvpInfo);

		int worldBorderSize = match.getWorldBorder().getSize()/2;
		String worldBorderInfo = String.format("%s%sWorld Border%s: %s",
				ChatColor.GREEN, ChatColor.ITALIC, ChatColor.RESET, worldBorderSize);
		boardLines.add(worldBorderInfo);

		buildLines();
	}

}