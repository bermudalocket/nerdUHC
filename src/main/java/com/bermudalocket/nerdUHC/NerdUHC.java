package com.bermudalocket.nerdUHC;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.bermudalocket.nerdUHC.match.MatchHandler;
import com.bermudalocket.nerdUHC.commands.GamemasterCommands;
import com.bermudalocket.nerdUHC.commands.PlayerCommands;
import com.bermudalocket.nerdUHC.listeners.PersistentListener;

//-------------------------------------------------------------------------------

public class NerdUHC extends JavaPlugin {

	public Configuration CONFIG;
	public MatchHandler matchHandler;

	// -------------------------------------------------------------------------------

	@Override
	public void onEnable() {

		CONFIG = new Configuration(this);
		CONFIG.reload();

		matchHandler = new MatchHandler(this);

		PersistentListener persistentListener = new PersistentListener(this);
		getServer().getPluginManager().registerEvents(persistentListener, this);

		GamemasterCommands gamemasterCommandHandler = new GamemasterCommands(this);
		
		this.getCommand("uhc").setExecutor(gamemasterCommandHandler);
		this.getCommand("sb-all").setExecutor(gamemasterCommandHandler);
		this.getCommand("togglepvp").setExecutor(gamemasterCommandHandler);
		this.getCommand("extendtime").setExecutor(gamemasterCommandHandler);

		PlayerCommands playerCommandHandler = new PlayerCommands(this);
		
		this.getCommand("join").setExecutor(playerCommandHandler);
		this.getCommand("t").setExecutor(playerCommandHandler);
		this.getCommand("teamlist").setExecutor(playerCommandHandler);
		this.getCommand("fixme").setExecutor(playerCommandHandler);
		this.getCommand("sb").setExecutor(playerCommandHandler);
		this.getCommand("kit").setExecutor(playerCommandHandler);
		
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}
	
}
