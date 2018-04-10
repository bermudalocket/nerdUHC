package com.bermudalocket.nerdUHC.team;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.match.MatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TeamHandler implements java.io.Serializable {

	private LinkedHashSet<UHCTeam> _roster = new LinkedHashSet<>();

	private Logger logger = NerdUHC.PLUGIN.getLogger();

	public UHCTeam getTeam(Player player) {
		for (UHCTeam uhcTeam : _roster) {
			if (uhcTeam.contains(player)) return uhcTeam;
		}
		return null;
	}

	public boolean teamExists(String teamName) {
		return _roster.stream().anyMatch( t -> t.getName().equalsIgnoreCase(teamName) );
	}

	private void deleteTeam(UHCTeam uhcTeam, TeamDeleteReason teamDeleteReason) {
		logger.info("TEAM_HANDLER pruned " + uhcTeam.getName() + " (" + teamDeleteReason.name() +")");
		_roster.remove(uhcTeam);
	}

	public void pruneTeams() {
		Match match = NerdUHC.MATCH_HANDLER.getMatch();
		TeamDeleteReason deleteReason = (match.getMatchState() == MatchState.INPROGRESS) ?
				TeamDeleteReason.PRUNED : TeamDeleteReason.EMPTY_AT_START;
		for (UHCTeam uhcTeam : _roster) {
			if (uhcTeam.size() == 0) deleteTeam(uhcTeam, deleteReason);
		}
	}

	public void addPlayerToTeam(Player player, UHCTeam uhcTeam) {
		uhcTeam.add(player);

		if (uhcTeam.isSpectator()) {
			NerdUHC.PLAYER_HANDLER.makeSpectator(player);
		} else {
			NerdUHC.PLAYER_HANDLER.makeSurvival(player);
		}

		NerdUHC.PLAYER_HANDLER.formatDisplayName(player);
		NerdUHC.SCOREBOARD_HANDLER.refresh();
		UHCSound.JOINTEAM.playSound(player);
		player.sendMessage("You joined the " + uhcTeam.getDisplayName() + " team!");
	}

	public void removePlayerFromTeam(Player player) {
		UHCTeam uhcTeam = getTeam(player);
		if (uhcTeam != null) uhcTeam.remove(player);
	}

	public void addPlayerRandomTeam(Player player) {
		// temporarily convert the team set to a list so that an index has meaning
		List<UHCTeam> rosterAsList = _roster.stream().filter(t -> !t.isFull()).collect(Collectors.toList());

		if (rosterAsList.isEmpty()) {
			player.sendMessage(ChatColor.RED + "Sorry, no teams are available to join.");
			return;
		}

		int j = NerdUHC.UTIL.random.nextInt(_roster.size());
		UHCTeam uhcTeam = rosterAsList.get(j);
		addPlayerToTeam(player, uhcTeam);
		NerdUHC.SCOREBOARD_HANDLER.refresh();
	}

	public boolean hasTeam(Player player) {
		for (UHCTeam uhcTeam : _roster) {
			if (uhcTeam.contains(player)) return true;
		}
		return false;
	}

	//

	public void load(FileConfiguration config) {
		// let's safeguard against this at the lowest possible level
		if (NerdUHC.MATCH_HANDLER.getMatch().getMatchState() != MatchState.PREGAME) return;

		// create teams based on the Teams list in config
		for (Map<?, ?> map : config.getMapList("teams")) {
			String name = map.get("name").toString().toUpperCase();
			ChatColor color = ChatColor.valueOf(map.get("color").toString());
			UHCTeam uhcTeam = new UHCTeam(name, color, NerdUHC.CONFIG.MAX_TEAM_SIZE);

			if (NerdUHC.CONFIG.USE_SCOREBOARD) NerdUHC.SCOREBOARD_HANDLER.createLink(uhcTeam);

			_roster.add(uhcTeam);
		}
	}

	public void load(TeamHandler teamHandler) {
		_roster.addAll(teamHandler._roster);
	}

}
