package com.bermudalocket.nerdUHC;

import org.bukkit.plugin.java.JavaPlugin;

import com.bermudalocket.nerdUHC.ScoreboardHandler;
import com.bermudalocket.nerdUHC.commands.nerdUHCCommandExecutor;
import com.bermudalocket.nerdUHC.listeners.ListenForPlayerDeathEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForPlayerDamageEvent;

public class nerdUHC extends JavaPlugin {
	
	public nerdUHC plugin;
	public Configuration CONFIG;
	public ScoreboardHandler ScoreboardHandler;
	public int gameMode = 0;
	public boolean gameStarted = true;

	@Override
	public void onEnable() {
		
		CONFIG = new Configuration(this);
		CONFIG.reload();
		
		getServer().getPluginManager().registerEvents(new ListenForPlayerDeathEvent(this), this);
		getServer().getPluginManager().registerEvents(new ListenForPlayerDamageEvent(this), this);
		
		new nerdUHCCommandExecutor(this);
		
		ScoreboardHandler = new ScoreboardHandler(this);
		ScoreboardHandler.configureScoreboards(gameMode);
		
	}
	
	@Override
	public void onDisable() {
		ScoreboardHandler.ClearBoards();
	}
	
	public void setGameMode(int gamemode) {
		gameMode = gamemode;
		ScoreboardHandler.configureScoreboards(gameMode);
	}
	
	public void reportError(String e) {
		getLogger().info("[nerdUHC REAL BAD ERROR] " + e);
	}

}
