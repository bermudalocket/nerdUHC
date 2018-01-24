package com.bermudalocket.nerdUHC;

import org.bukkit.plugin.java.JavaPlugin;

import com.bermudalocket.nerdUHC.match.MatchHandler;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardHandler;
import com.bermudalocket.nerdUHC.commands.GamemasterCommands;
import com.bermudalocket.nerdUHC.commands.PlayerCommands;
import com.bermudalocket.nerdUHC.listeners.PersistentListener;

public class NerdUHC extends JavaPlugin {

	public ScoreboardHandler scoreboardHandler;
	public Configuration CONFIG;
	public MatchHandler matchHandler;
	
	private PersistentListener persistentListener;
	
	private GamemasterCommands gamemasterCommandHandler;
	private PlayerCommands playerCommandHandler;

	@Override
	public void onEnable() {

		CONFIG = new Configuration(this);
		CONFIG.reload();

		scoreboardHandler = new ScoreboardHandler(this);
		matchHandler = new MatchHandler(this);
		
		persistentListener = new PersistentListener(this);
		getServer().getPluginManager().registerEvents(persistentListener, this);

		this.gamemasterCommandHandler = new GamemasterCommands(this);
		this.getCommand("barrier").setExecutor(gamemasterCommandHandler);
		this.getCommand("uhc").setExecutor(gamemasterCommandHandler);
		this.getCommand("freeze").setExecutor(gamemasterCommandHandler);
		this.getCommand("sb-all").setExecutor(gamemasterCommandHandler);
		
		this.playerCommandHandler = new PlayerCommands(this);
		this.getCommand("join").setExecutor(playerCommandHandler);
		this.getCommand("t").setExecutor(playerCommandHandler);
		this.getCommand("teamlist").setExecutor(playerCommandHandler);
		this.getCommand("fixme").setExecutor(playerCommandHandler);
		this.getCommand("sb").setExecutor(playerCommandHandler);
	}

	@Override
	public void onDisable() {
		
	}
	
}
