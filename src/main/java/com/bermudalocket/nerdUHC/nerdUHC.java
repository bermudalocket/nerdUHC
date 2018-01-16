package com.bermudalocket.nerdUHC;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import com.bermudalocket.nerdUHC.listeners.PregameListener;
import com.bermudalocket.nerdUHC.match.MatchHandler;
import com.bermudalocket.nerdUHC.match.TransitionTimer;
import com.bermudalocket.nerdUHC.modules.Barrier;
import com.bermudalocket.nerdUHC.scoreboards.MatchTimer;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardHandler;
import com.bermudalocket.nerdUHC.listeners.GameListener;
import com.bermudalocket.nerdUHC.commands.GamemasterCommands;
import com.bermudalocket.nerdUHC.commands.PlayerCommands;

public class NerdUHC extends JavaPlugin {

	public ScoreboardHandler scoreboardHandler;
	public Configuration CONFIG;
	public CombatLogger combatLogger = new CombatLogger(this);
	public MatchHandler match;
	public Barrier barrier;
	
	public MatchTimer matchTimer;
	public TransitionTimer transitionTimer;
	
	@SuppressWarnings("unused")
	@Override
	public void onEnable() {
		
		CONFIG = new Configuration(this);
		CONFIG.reload();
		
		Listener PregameListener = new PregameListener(this);
		Listener GameListener = new GameListener(this);
		
		match = new MatchHandler(this, CONFIG.DEFAULT_UHC_MODE);
		scoreboardHandler = new ScoreboardHandler(this, match);
		barrier = new Barrier(this);
		
		GamemasterCommands gamemastercmd = new GamemasterCommands(this);
		PlayerCommands playercmd = new PlayerCommands(this);
		
		matchTimer = new MatchTimer(this);
		transitionTimer = new TransitionTimer(this);
		
		getServer().getPluginManager().registerEvents(PregameListener, this);
		getServer().getPluginManager().registerEvents(GameListener, this);
		getServer().getPluginManager().registerEvents(match, this);
		getServer().getPluginManager().registerEvents(scoreboardHandler, this);
		getServer().getPluginManager().registerEvents(combatLogger, this);
		getServer().getPluginManager().registerEvents(matchTimer, this);
	}
	
	@Override
	public void onDisable() {
		combatLogger.clearDoppels();
		scoreboardHandler.clearBoards();
	}
	
	public void call(Event e) {
		getServer().getPluginManager().callEvent(e);
	}
	
	public void getNewScoreboardHandler() {
		this.scoreboardHandler = new ScoreboardHandler(this, match);
	}
}
