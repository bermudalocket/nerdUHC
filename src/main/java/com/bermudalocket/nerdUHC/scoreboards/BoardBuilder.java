package com.bermudalocket.nerdUHC.scoreboards;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.match.MatchState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.LinkedHashSet;

class BoardBuilder {

	/**
	 * Stores the scoreboard lines for building.
	 */
	private HashMap<Scoreboard, LinkedHashSet<String>> _boardLines = new HashMap<>();

	/**
	 * Polymorphic method that refreshes all scoreboards
	 */
	void refresh() {
		for (Scoreboard board : _boardLines.keySet()) refresh(board);
	}

	/**
	 * Polymorphic method that refreshes a certain scoreboard
	 * @param board The board to refresh
	 */
	private void refresh(Scoreboard board) {
		Match match = NerdUHC.MATCH_HANDLER.getMatch();
		if (match.getMatchState() == MatchState.PREGAME) showLobbyInfo(board); else showMatchInfo(board);
	}

	/**
	 * Resets the line cache for a given scoreboard
	 * @param board The board whose line cache should be reset
	 */
	private void resetLines(Scoreboard board) {
		LinkedHashSet<String> thisBoardsLines = _boardLines.get(board);
		for (String s : thisBoardsLines) board.resetScores(s);
		thisBoardsLines.clear();
	}

	private void buildLines(Scoreboard board) {
		LinkedHashSet<String> thisBoardsLines = _boardLines.get(board);
		int n = thisBoardsLines.size();
		for (String s : thisBoardsLines) board.getObjective("main").getScore(s).setScore(n--);
		NerdUHC.SCOREBOARD_HANDLER.forceHealthUpdates();
	}

	private void showLobbyInfo(Scoreboard board) {
		resetLines(board);

		LinkedHashSet<String> thisBoardsLines = _boardLines.get(board);
		Scoreboard masterBoard = NerdUHC.SCOREBOARD_HANDLER.getMasterBoard();

		thisBoardsLines.add(ChatColor.RESET.toString());
		thisBoardsLines.add("Teams:");
		for (Team t : masterBoard.getTeams()) {
			String teamLine = t.getDisplayName() +
					ChatColor.WHITE +
					" (" +
					t.getSize() +
					"/" +
					NerdUHC.CONFIG.MAX_TEAM_SIZE +
					")";
			thisBoardsLines.add(teamLine);
		}
		_boardLines.put(board, thisBoardsLines);

		buildLines(board);
	}

	private void showMatchInfo(Scoreboard board) {
		Match match = NerdUHC.MATCH_HANDLER.getMatch();

		resetLines(board);

		LinkedHashSet<String> thisBoardsLines = _boardLines.get(board);
		Scoreboard masterBoard = NerdUHC.SCOREBOARD_HANDLER.getMasterBoard();

		// divider
		thisBoardsLines.add(ChatColor.RESET.toString());

		// print all teams remaining, with a heart symbol per alive player...
		thisBoardsLines.add(String.format("%s%sTeams Left:", ChatColor.AQUA, ChatColor.ITALIC));
		for (Team t : masterBoard.getTeams()) {
			StringBuilder teamLine = new StringBuilder();
			teamLine.append(t.getDisplayName())
					.append(ChatColor.WHITE)
					.append(": ");
			for (int i = 0; i < t.getSize(); i++) {
				teamLine.append(ChatColor.RED).append("❤");
			}
			thisBoardsLines.add(teamLine.toString());
		}

		// ... and if no teams are present, say so
		if (masterBoard.getTeams().size() == 0) {
			thisBoardsLines.add(String.format("%s%sNone!", ChatColor.GRAY, ChatColor.ITALIC));
		}

		// Divider
		thisBoardsLines.add(ChatColor.RESET.toString());

		// PVP
		String pvpState = (match.getWorld().getPVP()) ? "enabled" : "disabled";
		String pvpInfo = String.format("%s%sPVP%s: %s",
				ChatColor.YELLOW, ChatColor.ITALIC, ChatColor.RESET, pvpState);
		thisBoardsLines.add(pvpInfo);

		// World Border
		int worldBorderSize = (int) match.getWorld().getWorldBorder().getSize()/2;
		String worldBorderInfo = String.format("%s%sWorld Border%s: %s",
				ChatColor.GREEN, ChatColor.ITALIC, ChatColor.RESET, worldBorderSize);
		thisBoardsLines.add(worldBorderInfo);

		// things to add only if personal boards are being used
		if (NerdUHC.SCOREBOARD_HANDLER.isPersonalBoard(board)) {
			Player player = NerdUHC.SCOREBOARD_HANDLER.getPlayerByPersonalBoard(board);

			// and only run it if that player is online. They'll be caught up if they log back in.
			if (player != null) {


			}
		}

		_boardLines.put(board, thisBoardsLines);
		buildLines(board);
	}

	//

	void load(BoardBuilder boardBuilder) {
		_boardLines.putAll(boardBuilder._boardLines);
	}

}
