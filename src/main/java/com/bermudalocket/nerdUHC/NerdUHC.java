package com.bermudalocket.nerdUHC;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import com.bermudalocket.nerdUHC.listeners.PregameListener;
import com.bermudalocket.nerdUHC.match.MatchHandler;
import com.bermudalocket.nerdUHC.modules.Barrier;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardHandler;
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

		match = new MatchHandler(this);
		scoreboardHandler = new ScoreboardHandler(this);
		barrier = new Barrier(this);

		GamemasterCommands gamemastercmd = new GamemasterCommands(this);
		PlayerCommands playercmd = new PlayerCommands(this);
		
		Listener PregameListener = new PregameListener(this);

		getServer().getPluginManager().registerEvents(PregameListener, this);
		getServer().getPluginManager().registerEvents(match, this);
		getServer().getPluginManager().registerEvents(scoreboardHandler, this);
		getServer().getPluginManager().registerEvents(combatLogger, this);
	}

	@Override
	public void onDisable() {
		scoreboardHandler.configureNewScoreboard(true);
	}

	public void call(Event e) {
		getServer().getPluginManager().callEvent(e);
	}
	
}
