package com.bermudalocket.nerdUHC;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import org.bukkit.event.Listener;

import com.bermudalocket.nerdUHC.listeners.PregameListener;
import com.bermudalocket.nerdUHC.modules.Barrier;
import com.bermudalocket.nerdUHC.listeners.GameListener;
import com.bermudalocket.nerdUHC.commands.GamemasterCommands;
import com.bermudalocket.nerdUHC.commands.PlayerCommands;

public class NerdUHC extends JavaPlugin {

	public ScoreboardHandler scoreboardHandler;
	public Configuration CONFIG;
	public CombatLogger combatLogger = new CombatLogger(this);
	public MatchHandler match;
	public Barrier barrier;
	
	@SuppressWarnings("unused")
	@Override
	public void onEnable() {
		
		CONFIG = new Configuration(this);
		CONFIG.reload();
		
		Listener PregameListener = new PregameListener(this);
		Listener GameListener = new GameListener(this);
		
		match = new MatchHandler(this, CONFIG.DEFAULT_UHC_MODE, CONFIG.MATCH_DURATION);
		scoreboardHandler = new ScoreboardHandler(this, match);
		barrier = new Barrier(this);
		
		GamemasterCommands gamemastercmd = new GamemasterCommands(this);
		PlayerCommands playercmd = new PlayerCommands(this);
		
		getServer().getPluginManager().registerEvents(PregameListener, this);
		getServer().getPluginManager().registerEvents(GameListener, this);
		getServer().getPluginManager().registerEvents(match, this);
	}
	
	@Override
	public void onDisable() {
		combatLogger.clearDoppels();
		scoreboardHandler.clearBoards();
	}
}
