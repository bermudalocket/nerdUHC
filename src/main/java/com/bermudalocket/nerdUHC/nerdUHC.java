package com.bermudalocket.nerdUHC;

import org.bukkit.plugin.java.JavaPlugin;

import com.bermudalocket.nerdUHC.ScoreboardHandler;
import com.bermudalocket.nerdUHC.commands.nerdUHCCommandExecutor;
import com.bermudalocket.nerdUHC.listeners.ListenForPlayerDeathEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForPlayerJoinEvent;
import com.bermudalocket.nerdUHC.listeners.ListenForEntityDamageByEntityEvent;

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
		
		new nerdUHCCommandExecutor(this);
		
		ScoreboardHandler = new ScoreboardHandler(this);
		ScoreboardHandler.configureScoreboards(gameMode);
		
		getServer().getPluginManager().registerEvents(new ListenForPlayerDeathEvent(this), this);
		getServer().getPluginManager().registerEvents(new ListenForEntityDamageByEntityEvent(this), this);
		getServer().getPluginManager().registerEvents(new ListenForPlayerJoinEvent(this), this);
		
	}
	
	@Override
	public void onDisable() {
		ScoreboardHandler.ClearBoards();
	}
	
	public void setGameMode(int gamemode) {
		// note to self: figure out somewhere else to put this
		gameMode = gamemode;
		ScoreboardHandler.configureScoreboards(gameMode);
	}

}
