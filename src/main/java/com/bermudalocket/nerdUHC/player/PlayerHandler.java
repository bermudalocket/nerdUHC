package com.bermudalocket.nerdUHC.player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import com.bermudalocket.nerdUHC.match.MatchState;
import com.bermudalocket.nerdUHC.team.UHCTeam;
import com.bermudalocket.nerdUHC.util.Constants;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class PlayerHandler implements Serializable {

	private HashSet<UUID> _currentlyPlaying = new HashSet<>();

	public void setPlaying(Player player, boolean isPlaying) {
		if (isPlaying) _currentlyPlaying.add(player.getUniqueId());
			else _currentlyPlaying.remove(player.getUniqueId());
	}

	public boolean isPlaying(Player player) {
		return _currentlyPlaying.contains(player.getUniqueId());
	}

	public void makeSpectator(Player player) {
		player.setAllowFlight(true);
		player.setGameMode(GameMode.SPECTATOR);
		player.setFlying(true);
		UHCLibrary.LIB_NOW_SPECTATING.tell(player);
		if (NerdUHC.MATCH_HANDLER.getMatch().getMatchState() == MatchState.PREGAME) {
			UHCLibrary.LIB_TO_EXIT_SPEC.tell(player);
		}
		NerdUHC.SCOREBOARD_HANDLER.refresh();
	}

	public void makeSurvival(Player player) {
		player.setAllowFlight(false);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFlying(false);
		NerdUHC.SCOREBOARD_HANDLER.refresh();
	}

	public void formatDisplayName(Player player) {
		UHCTeam t = NerdUHC.TEAM_HANDLER.getTeam(player);
		ChatColor newColor = (t == null) ? ChatColor.RESET : t.getColor();
		player.setPlayerListName(newColor + player.getName());
		player.setDisplayName(newColor + player.getName() + ChatColor.WHITE);
	}

	public void migratePlayers() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			resetPlayer(player);
			NerdUHC.SCOREBOARD_HANDLER.setBoard(player);
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, null));
		});
		NerdUHC.SCOREBOARD_HANDLER.refresh();
	}

	public void resetPlayer(Player p) {
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
		p.setSaturation(20);
		p.getInventory().clear();
		p.setExp(0);
		NerdUHC.SCOREBOARD_HANDLER.refresh();
	}

	private void safeTeleport(Player p, Location l) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 100));
		p.teleport(l);
	}

	public void spreadPlayers() {
		HashMap<UHCTeam, Location> teamLocations = new HashMap<>();
		Match match = NerdUHC.MATCH_HANDLER.getMatch();
		Boolean doHealing = match.getMatchState() != MatchState.DEATHMATCH;

		for (Player player : Bukkit.getOnlinePlayers()) {
			UHCTeam uhcTeam = NerdUHC.TEAM_HANDLER.getTeam(player);

			// if this player's team has already had a player spread, use the cache to send this player
			//   to the same place
			if (teamLocations.containsKey(uhcTeam)) {
				safeTeleport(player, teamLocations.get(uhcTeam));
				continue;
			}

			if (doHealing) resetPlayer(player);

			// create an integer multiplier that will be used to determine the max distance
			//  from spawn for this spread (worldSize * (1/multiplier))
			int multiplier; // inverse
			if (match.getMatchState() == MatchState.DEATHMATCH) {
				multiplier = 20;
			} else {
				multiplier = 2;
			}
			World world = match.getWorld();
			int maxDistance = (int) (world.getWorldBorder().getSize()/multiplier) - 100;

			// loop through potential spread locations and only teleport a player once the spot is deemed
			//   safe/satisfactory
			boolean unsafe = true;
			while (unsafe) {
				double mult1 = Math.random(); // mult1 in [0,1)
				double mult2 = Math.random(); // mult2 in [0,1)
				int sign1 = (Math.random() < 0.5) ? 1 : -1; // pick random sign
				int sign2 = (Math.random() < 0.5) ? 1 : -1; // "        "

				int x = (int) (sign1 * maxDistance * mult1);
				int z = (int) (sign2 * maxDistance * mult2);
				int y = world.getHighestBlockYAt(x, z);

				Biome newBiome = match.getWorld().getBiome(x, z);
				if (Constants.excludedBiomes.contains(newBiome)) continue;

				Material newBlock = match.getWorld().getBlockAt(x, y, z).getType();
				if (Constants.excludedBlocks.contains(newBlock)) continue;

				Location newLoc = new Location(match.getWorld(), x, y, z);
				safeTeleport(player, newLoc);

				if (NerdUHC.CONFIG.SPREAD_RESPECT_TEAMS) teamLocations.put(uhcTeam, newLoc);

				unsafe = false;
			}
		}
		NerdUHC.SCOREBOARD_HANDLER.refresh();
	}

	//

	public void load(PlayerHandler playerHandler) {
		_currentlyPlaying.addAll(playerHandler._currentlyPlaying);
	}

}
