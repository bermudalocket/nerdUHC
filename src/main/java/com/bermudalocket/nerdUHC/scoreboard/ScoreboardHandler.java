package com.bermudalocket.nerdUHC.scoreboard;

import com.bermudalocket.nerdUHC.Configuration;
import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.util.MatchState;
import com.bermudalocket.nerdUHC.util.UHCSounds;
import com.bermudalocket.nerdUHC.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bermudalocket.nerdUHC.NerdUHC.MATCH_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.PLAYER_HANDLER;

public class ScoreboardHandler {

    private Scoreboard _scoreboard;

    private final ArrayList<String> boardLines = new ArrayList<>();

    private static final String HEART = ChatColor.RED + "❤";

    private static final String LOBBY = String.format("%sNerdUHC%s - %s%sLOBBY",
            ChatColor.BOLD, ChatColor.RESET, ChatColor.AQUA, ChatColor.ITALIC);

    // -------------------------------------------------------------------------

    public ScoreboardHandler() {
        requestScoreboard();
    }

    public void setPlayerBoard(Player player) {
        player.setScoreboard(_scoreboard);
    }

    public int getTotalPlayers() {
        return _scoreboard.getEntries().size();
    }

    public Set<String> getRegisteredPlayers() {
        return _scoreboard.getEntries();
    }

    public int getTotalTeams() {
        return _scoreboard.getTeams().size();
    }

    public Set<Team> getRegisteredTeams() {
        return _scoreboard.getTeams();
    }

    public Team getTeamByPlayer(Player player) {
        for (Team t : _scoreboard.getTeams()) {
            if (t.hasEntry(player.getName())) return t;
        }
        return null;
    }

    public void updateTitle(String newTitle) {
        _scoreboard.getObjective(Util.MAIN_OBJECTIVE).setDisplayName(newTitle);
        refresh();
    }

    public void requestScoreboard() {
        _scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        _scoreboard.registerNewObjective("KILLS", Criterias.PLAYER_KILLS);
        _scoreboard.registerNewObjective("HEALTH", Criterias.HEALTH).setDisplaySlot(DisplaySlot.PLAYER_LIST);
        _scoreboard.registerNewObjective("HEALTHBELOWNAME", Criterias.HEALTH).setDisplaySlot(DisplaySlot.BELOW_NAME);
        _scoreboard.getObjective("HEALTHBELOWNAME").setDisplayName(HEART);
        _scoreboard.registerNewObjective("main", "dummy").setDisplayName(LOBBY);
        _scoreboard.getObjective("main").setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Map<?, ?> map : Configuration.RAW_TEAM_LIST) {
            String name = map.get("name").toString().toUpperCase();
            ChatColor color = ChatColor.valueOf(map.get("color").toString());
            Team t = _scoreboard.registerNewTeam(name);
            t.setColor(color);
            t.setPrefix(color + "");
            t.setSuffix("" + ChatColor.WHITE);
            t.setDisplayName(color + name + ChatColor.WHITE);
            t.setAllowFriendlyFire(Configuration.ALLOW_FRIENDLY_FIRE);
        }
    }

    public void pruneTeams() {
        for (Team t : _scoreboard.getTeams()) {
            if (t.getSize() == 0) t.unregister();
        }
        refresh();
    }

    public boolean teamIsJoinable(String team) {
        return teamIsJoinable(_scoreboard.getTeam(team));
    }

    public boolean teamIsJoinable(Team team) {
        return (MATCH_HANDLER.getMatch().inState(MatchState.PREGAME)
                && team != null
                && team.getSize() < Configuration.MAX_TEAM_SIZE);
    }

    private void forceHealthUpdates() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getHealth() != 0) player.setHealth(player.getHealth());
        });
    }

    public void addPlayerToTeam(Player player, Team team) {
        if (team == null) return;

        team.addEntry(player.getName());
        refresh();
        PLAYER_HANDLER.formatDisplayName(player);

        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        UHCSounds.JOINTEAM.playSound(player);
        player.sendMessage("You joined the " + team.getDisplayName() + " team!");
    }

    public void addPlayerToTeam(Player player, String teamName) {
        addPlayerToTeam(player, _scoreboard.getTeam(teamName));
    }

    public void addPlayerRandomTeam(Player p) {
        Boolean foundTeam = false;

        List<Team> teamList = new ArrayList<>(_scoreboard.getTeams());

        while (!foundTeam) {
            Integer j = Util.random.nextInt(teamList.size());

            Team t = teamList.get(j);
            String teamName = t.getName();

            if (t.getSize() < Configuration.MAX_TEAM_SIZE) {
                addPlayerToTeam(p, teamName);
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
        _scoreboard.getEntryTeam(p.getName()).removeEntry(p.getName());
        if (MATCH_HANDLER.getMatch().getMatchState() != MatchState.PREGAME) pruneTeams();
        if (spectateNext) PLAYER_HANDLER.makeSpectator(p);
    }

    public boolean hasTeam(Player p) {
        return _scoreboard.getEntryTeam(p.getName()) != null;
    }

    // ----------------------------------------------------------------
    // SCOREBOARD BUILDING METHODS
    // ----------------------------------------------------------------

    public void refresh() {
        Match match = MATCH_HANDLER.getMatch();
        if (match.inState(MatchState.PREGAME)) showLobbyInfo();
        else showTeamsRemaining();
        forceHealthUpdates();
    }

    private void resetLines() {
        for (String s : boardLines) _scoreboard.resetScores(s);
        boardLines.clear();
    }

    private void buildLines() {
        int n = boardLines.size();
        for (String s : boardLines) _scoreboard.getObjective("main").getScore(s).setScore(n--);
    }

    private void showLobbyInfo() {
        resetLines();
        boardLines.add(ChatColor.RESET.toString());
        boardLines.add("Teams:");
        for (Team t : _scoreboard.getTeams()) {
            String teamLine = t.getDisplayName() +
                    ChatColor.WHITE +
                    " (" +
                    t.getSize() +
                    "/" +
                    Configuration.MAX_TEAM_SIZE +
                    ")";
            boardLines.add(teamLine);
        }
        boardLines.add(" ");
        boardLines.add("Duration: " + ChatColor.AQUA + MATCH_HANDLER.getMatch().getDuration() + " minutes");
        boardLines.add("Difficulty: " + ChatColor.RED + MATCH_HANDLER.getMatch().getWorld().getDifficulty().toString());
        buildLines();
    }

    private void showTeamsRemaining() {
        Match match = MATCH_HANDLER.getMatch();

        resetLines();
        boardLines.add(ChatColor.RESET.toString());
        boardLines.add(String.format("%s%sTeams Left:", ChatColor.AQUA, ChatColor.ITALIC));
        for (Team t : _scoreboard.getTeams()) {
            StringBuilder teamLine = new StringBuilder();
            teamLine.append(t.getDisplayName())
                    .append(ChatColor.WHITE)
                    .append(": ");
            for (int i = 0; i < t.getSize(); i++) {
                teamLine.append(HEART);
            }
            boardLines.add(teamLine.toString());
        }
        if (boardLines.size() == 2) {
            boardLines.add(String.format("%s%sNone!", ChatColor.GRAY, ChatColor.ITALIC));
        }
        boardLines.add(ChatColor.RESET.toString());

        String pvpState = (match.getWorld().getPVP()) ? "enabled" : "disabled";
        String pvpInfo = String.format("%sPVP%s: %s",
                ChatColor.YELLOW, ChatColor.RESET, pvpState);
        boardLines.add(pvpInfo);

        int worldBorderSize = (int) match.getWorld().getWorldBorder().getSize() / 2;
        String worldBorderInfo = String.format("%sWorld Border%s: %s",
                ChatColor.GREEN, ChatColor.RESET, worldBorderSize);
        boardLines.add(worldBorderInfo);

        buildLines();
    }

}