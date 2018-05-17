package com.bermudalocket.nerdUHC.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum UHCLibrary {

    DEATHMATCH("Deathmatch!"),

    // Notices
    LIB_PVP_ENABLED(ChatColor.RED + "PVP has been enabled!"),

    // Gamemaster Command Messages
    LIB_SCOREBOARD_ALL_REFRESHED(ChatColor.GOLD + "Scoreboard refreshed for all players in this match."),
    LIB_BORDER_SHRINKING(ChatColor.RED + "The world border has begun to shrink!"),

    // Gamemaster Command Errors
    LIB_ERR_UHC_RUNNING(ChatColor.RED + "A UHC is already in progress!"),
    LIB_ERR_NO_UHC_RUNNING(ChatColor.RED + "A UHC is not currently running!"),

    // Pregame Listener
    LIB_NOW_SPECTATING(ChatColor.GOLD + "You are now spectating this match."),
    LIB_TO_EXIT_SPEC(ChatColor.GRAY + "" + ChatColor.ITALIC + "Run /join [team] if you change your mind before the match starts."),

    // Player Command Errors
    LIB_ERR_NOTEAMFORCHAT(ChatColor.RED + "You can't chat with your team if you're not on a team!"),
    LIB_ERR_NOKIT(ChatColor.RED + "You can't get a lobby kit if the match has already started!"),
    LIB_ERR_TEAM_FULL(ChatColor.RED + "That team is either full!"),
    LIB_ERR_JOIN_SYNTAX(ChatColor.RED + "Invalid syntax! Try /join [team]."),

    // CombatLogger
    LIB_CL_DOPPELDEAD(ChatColor.RED + "You combat logged and your doppel died!"),
    LIB_CL_DMG(ChatColor.RED + "You combat logged and your doppel took %d damage!"),
    LIB_CL_NODMG(ChatColor.RED + "You combat logged but your doppel took no damage. Lucky you!");

    private final String s;

    UHCLibrary(String s) {
        this.s = s;
    }

    public void tell(Player p) {
        p.sendMessage(s);
    }

    public void err(Player p) {
        tell(p);
        UHCSounds.OOPS.playSound(p);
    }

    public void rep(Player p, String find, String replace) {
        String msg = s.replace(find, replace);
        p.sendMessage(msg);
    }

    public void broadcast() {
        Bukkit.getServer().broadcastMessage(s);
    }

    public void sendAsTitle() {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(s, null, 10, 60, 10));
        broadcast();
    }

}
