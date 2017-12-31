package com.bermudalocket.nerdUHC.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

/////////////////////////////////////////////////////////////////////////////
//
//	CommandHandler
//	Thanks to totemo (via EasyRider) for this layout/setup
//

public abstract class CommandHandler implements CommandExecutor, TabCompleter {
	
	protected String _command;
	protected List<String> _subcommands;
	
	protected CommandHandler(String command, String... subcommands) {
		_command = command;
		_subcommands = Arrays.asList(subcommands);
	}
	
	public String getName() {
		return _command;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		ArrayList<String> completions = new ArrayList<String>();
		if (command.getName().equalsIgnoreCase(_command)) {
			if (args.length == 0) {
				completions.addAll(_subcommands);
			} else if (args.length == 1) {
                completions.addAll(_subcommands.stream().filter(subcmd -> subcmd.startsWith(args[0].toLowerCase())).collect(Collectors.toList()));
			}
		}
		return completions;
	}
	
	public void showCommandMenu(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "NerdUHC Commands:");
		sender.sendMessage("/uhcmode solo : sets the next UHC's mode to solo");
		sender.sendMessage("/uhcmode team : sets the next UHC's mode to team");
		sender.sendMessage("/uhcteam list : displays a list of all teams");
		sender.sendMessage("/uhcteam join : join a team");
		sender.sendMessage("/uhcteam leave : leave a team");
		sender.sendMessage("/barrier on : encloses a small area around the spawn coordinates with glass");
		sender.sendMessage("/barrier off : removes the glass barrier around the spawn coordinates");
	}
}
