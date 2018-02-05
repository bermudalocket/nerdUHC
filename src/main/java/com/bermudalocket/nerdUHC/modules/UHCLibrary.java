package com.bermudalocket.nerdUHC.modules;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum UHCLibrary {
	
	// Gamemaster Command Messages
	LIB_UPDATED("Game mode updated!"),
	LIB_UHC_STOPPED("Stopping the UHC..."),
	LIB_CONF_RELOADED("Config reloaded!" + ChatColor.GRAY + " This will take effect in the next match."),
	LIB_FROZEN("Players have been frozen."),
	LIB_UNFROZEN("Players have been unfrozen."),
	LIB_BARRIER_DRAWN("Barrier drawn!"),
	LIB_BARRIER_REM("Barrier removed!"),
	LIB_SCOREBOARD_REFRESHED("Your scoreboard has been refreshed."),
	LIB_SCOREBOARD_ALL_REFRESHED("Scoreboard refreshed for all players in this match."),
	LIB_PVP_ENABLED("PVP has been enabled."),
	LIB_PVP_DISABLED("PVP has been disabled."),
	
	// Gamemaster Command Errors
	LIB_ERR_INVALID("Invalid game mode!"),
	LIB_ERR_UHC_RUNNING("A UHC is already in progress!"),
	LIB_ERR_NO_UHC_RUNNING("A UHC is not currently running!"),
	LIB_ERR_STARTED("You can't reload the config while a UHC is in session."),
	
	// Pregame Listener
	LIB_WELCOME("Welcome to nerdUHC. The next round will be a %t round."),
	LIB_SOLO_JOIN("To join, run " + ChatColor.WHITE + "/join"),
	LIB_SPEC("To spectate, run " + ChatColor.WHITE + "/join spectator"),
	LIB_TEAM_LIST("To view a list of teams, run " + ChatColor.WHITE + "/teamlist"),
	LIB_TEAM_JOIN("To join a team, run " + ChatColor.WHITE + "/join [team]"),
	LIB_TEAM_CHAT("Chat with your team by using " + ChatColor.WHITE + "/t [msg]"),
	LIB_IN_PROGRESS("A round is already in progress, so you will have to spectate."),
	
	// Player Command Errors
	LIB_ERR_NOTEAMFORCHAT("You can't chat with your team if you're not on a team!"),
	
	// CombatLogger
	LIB_CL_DOPPELDEAD("You combat logged and your doppel died!"),
	LIB_CL_DMG("You combat logged and your doppel took %d damage!"),
	LIB_CL_NODMG("You combat logged but your doppel took no damage. Lucky you!");

	private String s;
	private static ChatColor base = ChatColor.GOLD;
	private static ChatColor emph = ChatColor.ITALIC;
	private static ChatColor err = ChatColor.RED;
	
	UHCLibrary(String s) {
		this.s = s;
	}
	
	public void get(Player p) {
		p.sendMessage(base + s);
	}
	
	public void emph(Player p) {
		p.sendMessage(emph + s);
	}
	
	public void emph(Player p, String find, String replace) {
		String msg = emph + s.replace(find, replace);
		p.sendMessage(msg);
	}
	
	public void err(Player p) {
		p.sendMessage(err + s);
	}
	
	public void rep(Player p, String find, String replace) {
		String msg = base + s.replace(find, replace);
		p.sendMessage(msg);
	}

}
