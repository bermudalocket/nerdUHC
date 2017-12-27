package com.bermudalocket.nerdUHC;

import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bermudalocket.nerdUHC.commands.NerdUHCCommandExecutor;
import com.bermudalocket.nerdUHC.listeners.ListenForPlayerDeathEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForPlayerJoinEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForEntityDamageByEntityEvent;

public class NerdUHC extends JavaPlugin {
	
	public NerdUHC plugin;
	public Configuration CONFIG;
	protected ScoreboardHandler scoreboardHandler;
	protected CombatLogger combatLogger;
	public enum UHCGameMode {
		SOLO,
		TEAM
	}
	public UHCGameMode uhcgamemode;
	private boolean gameStarted = false;
	private World world;

	@Override
	public void onEnable() {
		
		CONFIG = new Configuration(this);
		CONFIG.reload();
		
		this.getCommand("nerduhc").setExecutor(new NerdUHCCommandExecutor(this));
		
		scoreboardHandler = new ScoreboardHandler(this);
		scoreboardHandler.configureScoreboards(CONFIG.DEFAULT_UHC_MODE);
		uhcgamemode = CONFIG.DEFAULT_UHC_MODE;
		getLogger().info(uhcgamemode.toString());
		
		combatLogger = new CombatLogger(this);
		
		getServer().getPluginManager().registerEvents(new ListenForPlayerDeathEvent(this), this);
		getServer().getPluginManager().registerEvents(new ListenForEntityDamageByEntityEvent(this), this);
		getServer().getPluginManager().registerEvents(new ListenForPlayerJoinEvent(this), this);
		
	}
	
	@Override
	public void onDisable() {
		scoreboardHandler.clearBoards();
	}
	
	public void throwError(String msg) {
		getLogger().info(msg);
	}
	
	public void reloadScoreboards() {
		scoreboardHandler.clearBoards();
		scoreboardHandler.configureScoreboards(this.uhcgamemode);
	}
	
	public void setGameMode(UHCGameMode gameMode) {
		this.uhcgamemode = gameMode;
	}
	
	public UHCGameMode getGameMode() {
		return this.uhcgamemode;
	}

	public boolean isValidGameMode(String gameMode) {
		try {
			UHCGameMode.valueOf(gameMode);
			return true;
		} catch (Exception f) {
			return false;
		}
	}
	
	public boolean isGameStarted() { 
		return gameStarted;
	}
	
	public void registerPlayer(Player player, Boolean overridegamestarted) {
		scoreboardHandler.setPlayerBoard(player);
		if (!gameStarted || overridegamestarted) {
			switch (uhcgamemode) {
				case SOLO:
					scoreboardHandler.setPlayerTeam(player, "Alive");
					player.sendMessage("Welcome to nerdUHC. You have been added to the Alive team.");
					break;
				case TEAM:
					if (CONFIG.LET_PLAYERS_PICK_TEAMS) {
						player.sendMessage("Welcome to nerdUHC. View a list of teams with /teamlist. Join a team with /jointeam.");
					} else {
						if (scoreboardHandler.chooseTeamForPlayer(player)) {
							player.sendMessage("Welcome to nerdUHC. You have been added to the " + scoreboardHandler.getPlayerTeam(player).getName() + " team");
						} else {
							player.sendMessage("Sorry, there are no available teams to join.");
						}
					}
			}
		} else {
			player.sendMessage("Sorry! You joined after the UHC began, so you will have to spectate.");
		}
	}
	
	public void unregisterPlayer(Player player) {
		scoreboardHandler.unsetPlayerBoard(player);
	}
	
	public void handleDeath(Player player) {
		
		if (gameStarted) {
	
			if (scoreboardHandler.getPlayerScore(player, "Deaths") == 0) {
				
				scoreboardHandler.setPlayerScore(player, "Deaths", 1);
				scoreboardHandler.removePlayerTeam(player);
				scoreboardHandler.setPlayerTeam(player, "Dead");
			}
			
		} // if gameStarted
		
	} // handleDeath
	
	// drawBarrier: it's almost a sphere!
	public void drawBarrier(World world, int x, int y, int z, int radius, Material barriertype, Material onlyreplace) {
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

} // NerdUHC
