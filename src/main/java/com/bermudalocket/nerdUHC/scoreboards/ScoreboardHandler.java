package com.bermudalocket.nerdUHC.scoreboards;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.match.Match;

import com.bermudalocket.nerdUHC.team.UHCTeam;
import com.bermudalocket.nerdUHC.util.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardHandler implements java.io.Serializable {

	/**
	 * The current "master" scoreboard for the match
	 */
	private Scoreboard _board;

	/**
	 * Stores players' personal scoreboards. This system is used to show personalized
	 * information to each player instead of opting for a global information center.
	 */
	private HashMap<UUID, Scoreboard> _playerToScoreboardMap = new HashMap<>();

	/**
	 * creates an easily accessible association map between UHCTeams and their linked Teams
	 */
	private final HashMap<UHCTeam, Team> _uhcTeamToTeamMap = new HashMap<>();

	/**
	 * Helper class that handles the displaying of the information in each scoreboard
	 */
	private static final BoardBuilder BOARD_BUILDER = new BoardBuilder();


	// ----------------------------------------------------------------

	public void refresh() {
		BOARD_BUILDER.refresh();
	}

	/**
	 * Put the current match into the convenience field _match, and create a new scoreboard for it.
	 * @param match The next match
	 */
	public void initForMatch(Match match) {
		createScoreboard();
	}

	public void createPersonalBoard(Player player) {
		Scoreboard newBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		_playerToScoreboardMap.put(player.getUniqueId(), newBoard);
		player.setScoreboard(newBoard);
	}

	Scoreboard getPersonalBoard(Player player) {
		return _playerToScoreboardMap.get(player.getUniqueId());
	}

	public void update(Player player) {
		if (hasPersonalBoard(player)) {
			setPersonalBoard(player);
		} else {
			createPersonalBoard(player);
		}
	}

	boolean isPersonalBoard(Scoreboard board) {
		return _playerToScoreboardMap.containsValue(board);
	}

	public boolean hasPersonalBoard(Player player) {
		return _playerToScoreboardMap.containsKey(player.getUniqueId());
	}

	public void setPersonalBoard(Player player) {
		if (player.isOnline()) player.setScoreboard(_playerToScoreboardMap.get(player.getUniqueId()));
	}

	Player getPlayerByPersonalBoard(Scoreboard board) {
		for (Map.Entry<UUID, Scoreboard> pair : _playerToScoreboardMap.entrySet()) {
			if (pair.getValue() == board) {
				UUID playerUuid = pair.getKey();
				return Bukkit.getPlayer(playerUuid);
			}
		}
		return null;
	}

	/**
	 * Set the player's scoreboard to the current match scoreboard.
	 * @param player The player whose scoreboard to set
	 */
	public void setBoard(Player player) {
		player.setScoreboard(_board);
	}

	/**
	 * Create a scoreboard for the currently linked match
	 */
	private void createScoreboard() {
		_board = Bukkit.getScoreboardManager().getNewScoreboard();

		_board.registerNewObjective("KILLS", Criterias.PLAYER_KILLS);
		_board.registerNewObjective("HEALTH", Criterias.HEALTH).setDisplaySlot(DisplaySlot.PLAYER_LIST);
		_board.registerNewObjective("HEALTHBELOWNAME", Criterias.HEALTH).setDisplaySlot(DisplaySlot.BELOW_NAME);
		_board.getObjective("HEALTHBELOWNAME").setDisplayName(Constants.HEART);
		_board.registerNewObjective("main", "dummy").setDisplayName(Constants.LOBBY);
		_board.getObjective("main").setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	/**
	 * Call the polymorphic method createLink(UHCTeam uhcTeam, boolean createTeam = true);
	 * @param uhcTeam The UHCTeam to link to the scoreboard
	 */
	public void createLink(UHCTeam uhcTeam) {
		createLink(uhcTeam, true);
	}

	/**
	 * Polymorphic method for creating and updating a team link.
	 * @param uhcTeam
	 * @param createTeam
	 */
	private void createLink(UHCTeam uhcTeam, boolean createTeam) {
		String teamName = uhcTeam.getName();
		ChatColor teamColor = uhcTeam.getColor();

		// either create a new team or get the existing one
		Team team = (createTeam) ? _board.registerNewTeam(teamName)
								 : _board.getTeam(teamName);

		// set or update properties
		team.setColor(uhcTeam.getColor());
		team.setAllowFriendlyFire(NerdUHC.CONFIG.ALLOW_FRIENDLY_FIRE);
		team.setDisplayName(teamColor + teamName + Constants.RESET_COLOR);
	}

	/**
	 * Call the polymorphic method createLink(UHCTeam uhcTeam, boolean createTeam = false);
	 * @param uhcTeam
	 */
	public void updateLink(UHCTeam uhcTeam) {
		createLink(uhcTeam, false);
	}

	/**
	 * Get a link in the current match based on a UHCTeam instance
	 * @param uhcTeam The UHCTeam whose link to return
	 * @return a Team instance
	 */
	public Team getLink(UHCTeam uhcTeam) {
		return _uhcTeamToTeamMap.get(uhcTeam);
	}

	/**
	 * Forces all players' health to set to its current value.
	 * This method is specifically for the scoreboard functionality, preventing
	 * players from displaying 0 HP after a match ends and a new one begins.
	 *
	 * This method ignores players with zero health as this creates an infinite death loop
	 * ... which isn't great...
	 */
	void forceHealthUpdates() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getHealth() != 0) player.setHealth(player.getHealth());
		}
	}

	/**
	 * Get an instance of the current scoreboard.
	 * @return the current scoreboard
	 */
	Scoreboard getMasterBoard() {
		return _board;
	}

	//

	public void load(ScoreboardHandler scoreboardHandler) {
		_board = scoreboardHandler._board;
		_playerToScoreboardMap.putAll(scoreboardHandler._playerToScoreboardMap);
		_uhcTeamToTeamMap.putAll(scoreboardHandler._uhcTeamToTeamMap);
		//BOARD_BUILDER.load() ???
	}

}