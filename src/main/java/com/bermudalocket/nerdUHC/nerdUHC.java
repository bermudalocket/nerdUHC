package com.bermudalocket.nerdUHC;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;

import com.bermudalocket.nerdUHC.listeners.PregameListener;
import com.bermudalocket.nerdUHC.modules.Match;
import com.bermudalocket.nerdUHC.listeners.GameListener;
import com.bermudalocket.nerdUHC.commands.CommandHandler;
import com.bermudalocket.nerdUHC.commands.BarrierExecutor;
import com.bermudalocket.nerdUHC.commands.ConfigReloadExecutor;
import com.bermudalocket.nerdUHC.commands.TeamJoinExecutor;
import com.bermudalocket.nerdUHC.commands.TeamLeaveExecutor;
import com.bermudalocket.nerdUHC.commands.TeamListExecutor;
import com.bermudalocket.nerdUHC.commands.GamemodeExecutor;
import com.bermudalocket.nerdUHC.commands.StartStopExecutor;

public class NerdUHC extends JavaPlugin {

	public ScoreboardHandler scoreboardHandler;
	public Configuration CONFIG;
	public CombatLogger combatLogger = new CombatLogger(this);
	public Match match;
	
	@Override
	public void onEnable() {
		
		CONFIG = new Configuration(this);
		CONFIG.reload();
		
		match = new Match(this);
		
		scoreboardHandler = new ScoreboardHandler(this);
		
		addCommandHandler(new BarrierExecutor(this));
		addCommandHandler(new ConfigReloadExecutor(this));
		addCommandHandler(new TeamJoinExecutor(this));
		addCommandHandler(new TeamLeaveExecutor(this));
		addCommandHandler(new TeamListExecutor(this));
		addCommandHandler(new GamemodeExecutor(this));
		addCommandHandler(new StartStopExecutor(this));
		
		Listener PregameListener = new PregameListener(this);
		Listener GameListener = new GameListener(this);
		getServer().getPluginManager().registerEvents(PregameListener, this);
		getServer().getPluginManager().registerEvents(GameListener, this);
		
	}
	
	@Override
	public void onDisable() {
		scoreboardHandler.clearBoards();
	}
	
	protected void addCommandHandler(CommandHandler handler) {
		PluginCommand command = getCommand(handler.getName());
		command.setExecutor(handler);
		command.setTabCompleter(handler);
	}

}
