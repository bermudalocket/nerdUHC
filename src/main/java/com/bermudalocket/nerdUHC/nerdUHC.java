package com.bermudalocket.nerdUHC;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;

import com.bermudalocket.nerdUHC.listeners.ListenForPlayerDeathEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForPlayerJoinEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForPlayerQuitEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForChunkUnloadEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForEntityDamageByEntityEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForEntityDeathEvent;
import com.bermudalocket.nerdUHC.commands.CommandHandler;
import com.bermudalocket.nerdUHC.commands.BarrierExecutor;
import com.bermudalocket.nerdUHC.commands.ConfigReloadExecutor;
import com.bermudalocket.nerdUHC.commands.TeamChangeExecutor;
import com.bermudalocket.nerdUHC.commands.GamemodeExecutor;
import com.bermudalocket.nerdUHC.commands.StartStopExecutor;

/////////////////////////////////////////////////////////////////////////////
//
//	NerdUHC
//    Your friendly neighborhood UHC plugin.
//	

public class NerdUHC extends JavaPlugin {
	
	public static NerdUHC PLUGIN;
	public static ScoreboardHandler scoreboardHandler = new ScoreboardHandler();
	public static Configuration CONFIG;
	public static CombatLogger combatLogger = new CombatLogger();
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	General utility commands
	//
	//

	// ********************************************
	// Sets the current world, loads config, constructs
	// command handlers, initializes scoreboards,
	// sets gamerules, registers listeners
	// ********************************************
	@Override
	public void onEnable() {
		
		// Init
		PLUGIN = this;
		WORLD = Bukkit.getServer().getWorld("world");
		
		// Config
		CONFIG = new Configuration(this);
		CONFIG.reload();
		CONFIG.setGameRules();
		uhcgamemode = CONFIG.DEFAULT_UHC_MODE;
		
		// Scoreboards
		scoreboardHandler.setManager();
		scoreboardHandler.configureScoreboards();
		
		// Construct command handlers
		addCommandHandler(new BarrierExecutor());
		addCommandHandler(new ConfigReloadExecutor());
		addCommandHandler(new TeamChangeExecutor());
		addCommandHandler(new GamemodeExecutor());
		addCommandHandler(new StartStopExecutor());
		
		// Register listeners
		getServer().getPluginManager().registerEvents(new ListenForPlayerDeathEvent(), this);
		getServer().getPluginManager().registerEvents(new ListenForEntityDamageByEntityEvent(), this);
		getServer().getPluginManager().registerEvents(new ListenForPlayerJoinEvent(), this);
		getServer().getPluginManager().registerEvents(new ListenForPlayerQuitEvent(), this);
		getServer().getPluginManager().registerEvents(new ListenForEntityDeathEvent(), this);
		getServer().getPluginManager().registerEvents(new ListenForChunkUnloadEvent(), this);
		
	}
	
	// ********************************************
	// Clears out the scoreboards upon disable
	// ********************************************
	@Override
	public void onDisable() {
		scoreboardHandler.clearBoards();
	}
	
	// ********************************************
	// GETTER
	// ********************************************
	public static World getWorld() {
		return WORLD;
	}
	
	// ********************************************
	// Compact init for commands
	// ********************************************
	protected void addCommandHandler(CommandHandler handler) {
		PluginCommand command = getCommand(handler.getName());
		command.setExecutor(handler);
		command.setTabCompleter(handler);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	UHC Gamemode
	//
	//
	
	// ********************************************
	// GETTER/SETTER
	// ********************************************
	public static void setGameMode(UHCGameMode gameMode) {
		NerdUHC.uhcgamemode = gameMode;
	}
	public static UHCGameMode getGameMode() {
		return uhcgamemode;
	}

	// ********************************************
	// checks if a string is a valid game mode by
	// checking it against the UHCGameMode enum
	// ********************************************
	public static boolean isValidGameMode(String gameMode) {
		try {
			UHCGameMode.valueOf(gameMode);
			return true;
		} catch (Exception f) {
			return false;
		}
	}

	// ********************************************
	// GETTER/SETTER
	// Controls starting and stopping of the round
	// TRUE will start the UHC
	// FALSE will stop the UHC
	// ********************************************
	public static boolean isGameStarted() { 
		return gameStarted;
	}
	public static void setGameStarted(Boolean bool) {
		NerdUHC.gameStarted = bool;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Players
	//
	//
	
	// ********************************************
	// General method for registering a new player
	// Sets their scoreboard and auto-handles
	// teams based on current gamemode
	// ********************************************
	public static void registerPlayer(Player player, Boolean overridegamestarted) {
		scoreboardHandler.setPlayerBoard(player);
		if (!gameStarted || overridegamestarted) {
			switch (uhcgamemode) {
				case SOLO:
					scoreboardHandler.setPlayerTeam(player, NerdUHC.CONFIG.ALIVE_TEAM_NAME);
					player.sendMessage(ChatColor.GRAY + "Welcome to nerdUHC. You have been added to the " + NerdUHC.CONFIG.ALIVE_TEAM_NAME + " team.");
					break;
				case TEAM:
					if (CONFIG.LET_PLAYERS_PICK_TEAMS) {
						player.sendMessage(ChatColor.GRAY + "Welcome to nerdUHC. View a list of teams with " + ChatColor.WHITE + "/uhcteam list" + ChatColor.GRAY + ". Join a team with " + ChatColor.WHITE + "/uhcteam join [TEAM]" + ChatColor.GRAY + ".");
					} else {
						if (scoreboardHandler.chooseTeamForPlayer(player)) {
							player.sendMessage(ChatColor.GRAY + "Welcome to nerdUHC. You have been added to the " + scoreboardHandler.getPlayerTeam(player).getName() + " team");
						} else {
							player.sendMessage(ChatColor.GRAY + "Sorry, there are no available teams to join.");
						}
					}
			}
		} else {
			player.sendMessage(ChatColor.GRAY + "Sorry! You joined after the UHC began, so you will have to spectate.");
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	drawBarrier - spawn barrier feature
	//  draws a quasi-spherical barrier around spawnpoint
	//
	
	// ********************************************
	// drawBarrier: it's almost a sphere!
	// ********************************************
	public static void drawBarrier(World world, int x, int y, int z, int radius, Material barriertype, Material onlyreplace) {
		if (radius <= 0) return;
		
		Location center = new Location(world, x, y, z);
		Location point = new Location(world, 0, 0, 0);
		
		for (int i = -radius - 1; i < radius + 1; i++) {
			for (int j = -radius - 1; j < radius + 1; j++) {
				for (int k = -radius - 1; k < radius + 1; k++) {
					point.setX(x+i);
					point.setY(y+j);
					point.setZ(z+k);
					Material lookingat = world.getBlockAt(point).getType();
					if (Math.abs((int) Math.round(center.distanceSquared(point)) - radius*radius) <= radius) {
						if (lookingat == onlyreplace) {
							world.getBlockAt(point).setType(barriertype);
						}  else {
							// nope
						}
					}
				}
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Fields
	// 
	//
	
	// ********************************************
	// enum holding gamemode types
	// ********************************************
	public enum UHCGameMode {
		SOLO,
		TEAM
	}
	
	// ********************************************
	// UHC gamemode for current or upcoming round
	// ********************************************
	private static UHCGameMode uhcgamemode;
	
	// ********************************************
	// boolean holding whether a round is in progress
	// ********************************************
	private static boolean gameStarted = false;
	
	// ********************************************
	// World in which the UHC is taking place
	// ********************************************
	private static World WORLD;

} // NerdUHC
