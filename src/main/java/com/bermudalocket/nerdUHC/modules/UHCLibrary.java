package com.bermudalocket.nerdUHC.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum UHCLibrary {
	
	LIB(""),
	DEATHMATCH("Deathmatch!"),

	// Notices
	LIB_PVP_ENABLED(ChatColor.RED + "PVP has been enabled!"),
	LIB_PVP_DISABLED(ChatColor.RED + "PVP has been disabled!"),
	
	// Gamemaster Command Messages
	LIB_SCOREBOARD_REFRESHED("Your scoreboard has been refreshed."),
	LIB_SCOREBOARD_ALL_REFRESHED("Scoreboard refreshed for all players in this match."),
	LIB_BORDER_SHRINKING(ChatColor.RED + "The world border has begun to shrink!"),
	LIB_BORDER_FROZEN(ChatColor.AQUA + "The world border is now frozen."),
	
	// Gamemaster Command Errors
	LIB_ERR_UHC_RUNNING("A UHC is already in progress!"),
	LIB_ERR_NO_UHC_RUNNING("A UHC is not currently running!"),
	
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
	LIB_ERR_NOKIT("You can't get a lobby kit if the match has already started!"),
	LIB_ERR_TEAM_FULL("That team is either full or doesn't exist!"),
	LIB_ERR_JOIN_SYNTAX("Invalid syntax! Try /join [team]."),
	
	// CombatLogger
	LIB_CL_DOPPELDEAD("You combat logged and your doppel died!"),
	LIB_CL_DMG("You combat logged and your doppel took %d damage!"),
	LIB_CL_NODMG("You combat logged but your doppel took no damage. Lucky you!");

	private final String s;
	private static final ChatColor base = ChatColor.GOLD;
	private static final ChatColor emph = ChatColor.ITALIC;
	private static final ChatColor err = ChatColor.RED;
	
	UHCLibrary(String s) {
		this.s = s;
	}
	
	public void tell(Player p) {
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
		UHCSound.OOPS.playSound(p);
	}
	
	public void rep(Player p, String find, String replace) {
		String msg = base + s.replace(find, replace);
		p.sendMessage(msg);
	}

	// ---------

	public void broadcast() {
		Bukkit.getServer().broadcastMessage(s);
	}

	public void sendAsTitle() {
		Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(s, null, 10, 60, 10));
	}
	
	public void welcome(Player p, UHCGameMode uhcgamemode) {
		if (uhcgamemode == UHCGameMode.SOLO) {
			LIB_SOLO_JOIN.tell(p);
			LIB_SPEC.tell(p);
		} else {
			LIB_TEAM_LIST.tell(p);
			LIB_TEAM_JOIN.tell(p);
			LIB_TEAM_CHAT.tell(p);
			LIB_SPEC.tell(p);
		}
	}

}
