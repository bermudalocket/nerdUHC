package com.bermudalocket.nerdUHC;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bermudalocket.nerdUHC.ScoreboardHandler;
//import com.bermudalocket.nerdUHC.CombatLogger;
import com.bermudalocket.nerdUHC.commands.NerdUHCCommandExecutor;
import com.bermudalocket.nerdUHC.listeners.ListenForPlayerDeathEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForPlayerJoinEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForEntityDamageByEntityEvent;

public class NerdUHC extends JavaPlugin {
	
	public NerdUHC plugin;
	public Configuration CONFIG;
	protected ScoreboardHandler scoreboardHandler;
	public enum UHCGameMode {
		SOLO,
		TEAM
	}
	private UHCGameMode gameMode;
	private boolean gameStarted = false;

	@Override
	public void onEnable() {
		
		CONFIG = new Configuration(this);
		CONFIG.reload();
		
		new NerdUHCCommandExecutor(this);
		
		scoreboardHandler = new ScoreboardHandler(this);
		scoreboardHandler.configureScoreboards(CONFIG.DEFAULT_UHC_MODE);
		
		gameMode = CONFIG.DEFAULT_UHC_MODE;
		
		getServer().getPluginManager().registerEvents(new ListenForPlayerDeathEvent(this), this);
		getServer().getPluginManager().registerEvents(new ListenForEntityDamageByEntityEvent(this), this);
		getServer().getPluginManager().registerEvents(new ListenForPlayerJoinEvent(this), this);
		
	}
	
	@Override
	public void onDisable() {
		scoreboardHandler.clearBoards();
	}
	
	public void setGameMode(UHCGameMode gameMode) {
		if (!gameStarted) scoreboardHandler.configureScoreboards(gameMode);
	}
	
	public boolean isValidGameMode(String gameMode) {
		try {
			UHCGameMode.valueOf(gameMode);
			return true;
		} catch (Exception f) {
			return false;
		}
	}
	
	public void registerPlayer(Player player, Boolean overridegamestarted) {
		scoreboardHandler.setPlayerBoard(player);
		if (!gameStarted || overridegamestarted) {
			switch (gameMode) {
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

} // NerdUHC
