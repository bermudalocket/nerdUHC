package com.bermudalocket.nerdUHC.gui;

import java.util.ArrayList;
import java.util.HashMap;

import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.match.MatchState;
import com.bermudalocket.nerdUHC.modules.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.NerdUHC;

public class GUIHandler implements Listener {

	private final NerdUHC plugin;
	private final Match match;
	
	private static Inventory teamGUI;
	private static Inventory durationGUI;
	private static Inventory difficultyGUI;
	private static Inventory friendlyfireGUI;
	
	private final ItemStack joinTeam = new ItemStack(Material.STONE_SWORD, 1);
	private final ItemStack spectate = new ItemStack(Material.EYE_OF_ENDER, 1);
	private final ItemStack infoBook = new ItemStack(Material.WRITTEN_BOOK, 1);
	private final ItemStack randomTeam = new ItemStack(Material.CHEST, 1);
	
	private final ItemStack setDuration = new ItemStack(Material.WATCH, 1);
	private final ItemStack setDifficulty = new ItemStack(Material.DIAMOND_SWORD, 1);
	private final ItemStack setFriendlyFire = new ItemStack(Material.BARRIER, 1);
	private final ItemStack startMatch = new ItemStack(Material.NETHER_STAR, 1);
	
	private final HashMap<ItemStack, Team> stackMap = new HashMap<>();
	
	public GUIHandler(Match match) {
		this.plugin = NerdUHC.PLUGIN;
		this.match = match;
		build();
		teamsToItems();
	}

	public void startListening() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void stopListening() {
		HandlerList.unregisterAll(this);
	}
	
	public void givePlayerGUIItems(Player p) {	
		p.getInventory().setItem(0, joinTeam);
		p.getInventory().setItem(1, spectate);
		p.getInventory().setItem(2, infoBook);
		if (p.hasPermission("nerduhc.gamemaster")) {
			giveGamemasterGUIItems(p);
		}
	}
	
	private void giveGamemasterGUIItems(Player p) {
		p.getInventory().setItem(4, startMatch);
		p.getInventory().setItem(6, setDuration);
		p.getInventory().setItem(7, setDifficulty);
		p.getInventory().setItem(8, setFriendlyFire);
	}
	
	private void build() {
		teamGUI = Bukkit.createInventory(null, 27, "Select a team:");
		durationGUI = Bukkit.createInventory(null, 9, "Set duration:");
		difficultyGUI = Bukkit.createInventory(null, 9, "Set difficulty:");
		friendlyfireGUI = Bukkit.createInventory(null, 9, "Set friendly fire:");
		
		// player items
		
		ItemMeta joinateammeta = joinTeam.getItemMeta();
		joinateammeta.setDisplayName("Join a team");
		joinTeam.setItemMeta(joinateammeta);
		
		ItemMeta spectatemeta = spectate.getItemMeta();
		spectatemeta.setDisplayName("Spectate");
		spectate.setItemMeta(spectatemeta);

		BookMeta infoBookMeta = (BookMeta) infoBook.getItemMeta();
		infoBookMeta.setTitle("NerdUHC Quick Start Guide");
		String page1 = String.format("%sWelcome to NerdUHC!%s \n \n" +
				"If this is your first time playing, there are a few things to keep in mind... \n" +
				"-%s %syour health will not regenerate naturally%s%s! \n" +
				"- food is still necessary, but it will not heal you. \n" +
				"- the %sNether%s and %sThe End%s are open.",
				ChatColor.BOLD, ChatColor.RESET, ChatColor.BOLD, ChatColor.RED, ChatColor.RESET, ChatColor.BLACK,
				ChatColor.RED, ChatColor.BLACK, ChatColor.DARK_PURPLE, ChatColor.BLACK);
		String page2 = String.format("- you can use %s/t [msg]%s to chat with your team. \n \n" +
				"- don't be a chicken and log out during combat, that's fowl play! %sA vulnerable chicken with" +
				" your name, XP, and inventory will spawn in your place%s!",
				ChatColor.GOLD, ChatColor.BLACK, ChatColor.DARK_RED, ChatColor.BLACK);
		String page3 = String.format("- try crafting a %scompass or clock%s; they actually do something here! " +
				"Just keep in mind there is a %s10 second%s cooldown on them. \n \n" +
				"- last but not least, Ghasts will not drop %sghast tears%s! I hear they'll give you some gold instead...",
				ChatColor.DARK_BLUE, ChatColor.BLACK, ChatColor.BOLD, ChatColor.RESET, ChatColor.AQUA, ChatColor.BLACK);
				
		infoBookMeta.addPage(page1);
		infoBookMeta.addPage(page2);
		infoBookMeta.addPage(page3);
		infoBook.setItemMeta(infoBookMeta);
		
		ItemMeta randomteammeta = randomTeam.getItemMeta();
		randomteammeta.setDisplayName("Random");
		randomTeam.setItemMeta(randomteammeta);
		
		// gamemaster items
		// duration
		
		ItemMeta setdurationmeta = setDuration.getItemMeta();
		setdurationmeta.setDisplayName("Duration");
		setdurationmeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		setDuration.setItemMeta(setdurationmeta);
		
		ItemStack thirty = new ItemStack(Material.COAL, 1);
		ItemMeta thirtymeta = thirty.getItemMeta();
		thirtymeta.setDisplayName("30 minutes");
		thirtymeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		thirty.setItemMeta(thirtymeta);
		
		ItemStack hour = new ItemStack(Material.IRON_INGOT, 1);
		ItemMeta hourmeta = hour.getItemMeta();
		hourmeta.setDisplayName("1 hour");
		hourmeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		hour.setItemMeta(hourmeta);
		
		ItemStack twohours = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta twohoursmeta = twohours.getItemMeta();
		twohoursmeta.setDisplayName("2 hours");
		twohoursmeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		twohours.setItemMeta(twohoursmeta);
		
		ItemStack threehours = new ItemStack(Material.DIAMOND, 1);
		ItemMeta threehoursmeta = threehours.getItemMeta();
		threehoursmeta.setDisplayName("3 hours");
		threehoursmeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		threehours.setItemMeta(threehoursmeta);
		
		durationGUI.addItem(thirty);
		durationGUI.addItem(hour);
		durationGUI.addItem(twohours);
		durationGUI.addItem(threehours);
		
		// difficulty
		
		ItemMeta setdifficultymeta = setDifficulty.getItemMeta();
		setdifficultymeta.setDisplayName("Difficulty");
		setdifficultymeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		setDifficulty.setItemMeta(setdifficultymeta);
		
		ItemStack peaceful = new Wool(DyeColor.LIGHT_BLUE).toItemStack(1);
		ItemMeta pm = peaceful.getItemMeta();
		pm.setDisplayName("Peaceful");
		pm.addEnchant(Enchantment.KNOCKBACK, 1, true);
		peaceful.setItemMeta(pm);
		
		ItemStack easy = new Wool(DyeColor.LIME).toItemStack(1);
		ItemMeta em = easy.getItemMeta();
		em.setDisplayName("Easy");
		em.addEnchant(Enchantment.KNOCKBACK, 1, true);
		easy.setItemMeta(em);
		
		ItemStack medium = new Wool(DyeColor.ORANGE).toItemStack(1);
		ItemMeta mm = medium.getItemMeta();
		mm.setDisplayName("Normal");
		mm.addEnchant(Enchantment.KNOCKBACK, 1, true);
		medium.setItemMeta(mm);
		
		ItemStack hard = new Wool(DyeColor.RED).toItemStack(1);
		ItemMeta hm = hard.getItemMeta();
		hm.setDisplayName("Hard");
		hm.addEnchant(Enchantment.KNOCKBACK, 1, true);
		hard.setItemMeta(hm);
		
		difficultyGUI.addItem(peaceful);
		difficultyGUI.addItem(easy);
		difficultyGUI.addItem(medium);
		difficultyGUI.addItem(hard);
		
		// friendly fire
		
		ItemMeta setfriendlyfiremeta = setFriendlyFire.getItemMeta();
		setfriendlyfiremeta.setDisplayName("Friendly Fire");
		setfriendlyfiremeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		setFriendlyFire.setItemMeta(setfriendlyfiremeta);
		
		ItemStack ffon = new ItemStack(Material.DIAMOND_SWORD, 1);
		ItemMeta om = ffon.getItemMeta();
		om.setDisplayName("Enabled");
		ffon.setItemMeta(om);
		
		ItemStack ffoff = new ItemStack(Material.SHIELD, 1);
		ItemMeta fm = ffoff.getItemMeta();
		fm.setDisplayName("Disabled");
		ffoff.setItemMeta(fm);
		
		friendlyfireGUI.addItem(ffon);
		friendlyfireGUI.addItem(ffoff);
		
		// start match
		ItemMeta startmatchmeta = startMatch.getItemMeta();
		startmatchmeta.setDisplayName("Start match!");
		startmatchmeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		startMatch.setItemMeta(startmatchmeta);
	}
	
	private void teamsToItems() {
		ItemStack i;
		ItemMeta m;
		
		for (Team t : match.getScoreboard().getTeams()) {
			ArrayList<String> lore = new ArrayList<>();
			
			DyeColor dye;
			ChatColor tc = t.getColor();
			
			switch (tc) {
				case AQUA:
					dye = DyeColor.LIGHT_BLUE;
					break;
				case BLACK:
					dye = DyeColor.BLACK;
					break;
				case BLUE:
					dye = DyeColor.BLUE;
					break;
				case BOLD:
					dye = DyeColor.WHITE;
					break;
				case DARK_AQUA:
					dye = DyeColor.BLUE;
					break;
				case DARK_BLUE:
					dye = DyeColor.BLUE;
					break;
				case DARK_GRAY:
					dye = DyeColor.GRAY;
					break;
				case DARK_GREEN:
					dye = DyeColor.GREEN;
					break;
				case DARK_PURPLE:
					dye = DyeColor.PURPLE;
					break;
				case DARK_RED:
					dye = DyeColor.RED;
					break;
				case GOLD:
					dye = DyeColor.ORANGE;
					break;
				case GRAY:
					dye = DyeColor.GRAY;
					break;
				case GREEN:
					dye = DyeColor.LIME;
					break;
				case ITALIC:
					dye = DyeColor.WHITE;
					break;
				case LIGHT_PURPLE:
					dye = DyeColor.PINK;
					break;
				case MAGIC:
					dye = DyeColor.WHITE;
					break;
				case RED:
					dye = DyeColor.RED;
					break;
				case RESET:
					dye = DyeColor.WHITE;
					break;
				case STRIKETHROUGH:
					dye = DyeColor.WHITE;
					break;
				case UNDERLINE:
					dye = DyeColor.WHITE;
					break;
				case WHITE:
					dye = DyeColor.WHITE;
					break;
				case YELLOW:
					dye = DyeColor.YELLOW;
					break;
				default:
					dye = DyeColor.WHITE;
					break;
			}
			
			i = new Wool(dye).toItemStack(1);
			m = i.getItemMeta();
			m.setDisplayName(t.getDisplayName());
			
			lore.add(ChatColor.WHITE + "Players: " + t.getSize() + "/" + plugin.config.MAX_TEAM_SIZE);
			lore.add(" " + ChatColor.RESET);
			for (String s : t.getEntries()) {
				lore.add(ChatColor.WHITE + "" + s);
			}
			m.setLore(lore);
			
			i.setItemMeta(m);
			
			teamGUI.addItem(i);
			
			stackMap.put(i, t);
		}
		
		teamGUI.addItem(randomTeam);
		
	}
	
	private synchronized void refreshItemGUI() {
		teamGUI.clear();
		teamsToItems();
	}
	
	//
	//
	//
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		if (e.getItem() == null || !e.getItem().getItemMeta().hasDisplayName()) {
			return;
		}
		
		Player p = e.getPlayer();
		String s = e.getItem().getItemMeta().getDisplayName();
		Boolean gm = p.hasPermission("nerduhc.gamemaster");
		
		if (e.getItem().getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta bookMeta = (BookMeta) e.getItem().getItemMeta();
			s = bookMeta.getDisplayName();
		}
		
		if (s.equals("Join a team")) {
			p.openInventory(teamGUI);
		} else if (s.equals("Spectate")) {
			match.getScoreboardHandler().makeSpectator(p);
			refreshItemGUI();
		} else if (s.equals("NerdUHC Quick Start Guide")) {
			// do nothing
		} else if (s.equals("Duration")) {
			if (gm) p.openInventory(durationGUI);
		} else if (s.equals("Start match!")) {
			if (gm && match.getMatchState().equals(MatchState.PREGAME)) {
				match.beginMatchStartCountdown();
			}
		} else if (s.equals("Difficulty")) {
			if (gm) p.openInventory(difficultyGUI);
		} else if (s.equals("Friendly Fire")) {
			if (gm) p.openInventory(friendlyfireGUI);
		} else {
			p.getInventory().remove(e.getItem());
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public synchronized void onPlayerInvClick(InventoryClickEvent e) {
		
		// check to make sure everything is on the up-and-up
		
		Player p = (Player) e.getWhoClicked();
		ItemStack i = e.getCurrentItem();
		
		if (p == null || i == null) return;
		
		String s = "";
		if (i.hasItemMeta()) {
			s = i.getItemMeta().getDisplayName();
		} else {
			if (i.isSimilar(new Wool(DyeColor.LIGHT_BLUE).toItemStack(1))) s = "Peaceful";
			if (i.isSimilar(new Wool(DyeColor.LIME).toItemStack(1))) s = "Easy";
			if (i.isSimilar(new Wool(DyeColor.ORANGE).toItemStack(1))) s = "Medium";
			if (i.isSimilar(new Wool(DyeColor.RED).toItemStack(1))) s = "Hard";
		}
		if (s.equals("")) return;
		
		// do stuff
		
		if (e.getInventory().getName().equals(teamGUI.getName())) {
			
			// selecting a team?
			
			if (stackMap.containsKey(i)) {
				Team t = stackMap.get(i);
				if (t.getSize() < plugin.config.MAX_TEAM_SIZE) {
					match.getScoreboardHandler().addPlayerToTeam(p, t.getName());
					p.closeInventory();
					refreshItemGUI();
				} else {
					UHCLibrary.LIB_ERR_TEAM_FULL.err(p);
				}
			} else if (s.equals("Random")) {

				// Joining a random team?

				match.getScoreboardHandler().addPlayerRandomTeam(p);
				p.closeInventory();
				refreshItemGUI();
				
			} else {
				e.getInventory().remove(i);
			}
			
		} else if (e.getInventory().getName().equals(durationGUI.getName())) {
			
			// changing match duration?

			switch (s) {
				case "30 minutes":
					match.setDuration(30);
					break;
				case "1 hour":
					match.setDuration(60);
					break;
				case "2 hours":
					match.setDuration(120);
					break;
				case "3 hours":
					match.setDuration(180);
					break;
				default:
					return; // that's not right
			}
			UHCSound.DING.playSound(p);
			p.sendMessage(ChatColor.GOLD + "Duration set to " + s);
			p.closeInventory();
			
		} else if (e.getInventory().getName().equals(difficultyGUI.getName())) {
			
			// changing world difficulty?
			
			if (i.getType() != Material.WOOL) return; // that's not right
			
			match.getWorld().setDifficulty(Difficulty.valueOf(s.toUpperCase()));
			UHCSound.DING.playSound(p);
			p.sendMessage(ChatColor.GOLD + "Difficulty set to " + s);
			p.closeInventory();
			
		} else if (e.getInventory().getName().equals(friendlyfireGUI.getName())) {
			
			// changing friendly fire state?
			
			if (i.getType() != Material.DIAMOND_SWORD && i.getType() != Material.SHIELD) return; // that's not right
			
			for (Team t : match.getScoreboard().getTeams()) {
				t.setAllowFriendlyFire(s.equals("Enabled"));
			}
			
			UHCSound.DING.playSound(p);
			p.sendMessage(ChatColor.GOLD + "Friendly fire is now " + ((s.equals("Enabled")) ? "enabled" : "disabled"));
			p.closeInventory();
			
		}
		
		// and cancel
		
		e.setCancelled(true);
	}
}
