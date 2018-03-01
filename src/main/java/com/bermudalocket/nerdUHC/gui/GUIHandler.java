package com.bermudalocket.nerdUHC.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.NerdUHC;

@SuppressWarnings("StringEquality")
public class GUIHandler implements Listener {

	private final NerdUHC plugin;
	private final UHCMatch match;
	
	private static Inventory teamGUI;
	private static Inventory durationGUI;
	private static Inventory difficultyGUI;
	private static Inventory friendlyfireGUI;
	
	private final ItemStack joinateam = new ItemStack(Material.IRON_HELMET, 1);
	private final ItemStack spectate = new ItemStack(Material.EYE_OF_ENDER, 1);
	private final ItemStack teamlist = new ItemStack(Material.BOOK, 1);
	private final ItemStack randomteam = new ItemStack(Material.CHEST, 1);
	
	private final ItemStack setduration = new ItemStack(Material.WATCH, 1);
	private final ItemStack setdifficulty = new ItemStack(Material.DIAMOND_SWORD, 1);
	private final ItemStack setfriendlyfire = new ItemStack(Material.BARRIER, 1);
	private final ItemStack startmatch = new ItemStack(Material.NETHER_STAR, 1);
	
	private final HashMap<ItemStack, Team> stackmap = new HashMap<>();
	private final ArrayList<Team> teamList = new ArrayList<>();
	
	public GUIHandler(NerdUHC plugin, UHCMatch match) {
		this.plugin = plugin;
		this.match = match;
		
		build();
		teamsToItems();
	}
	
	public void givePlayerGUIItems(Player p) {	
		p.getInventory().setItem(0, joinateam);
		p.getInventory().setItem(1, spectate);
		p.getInventory().setItem(2, teamlist);
		if (p.hasPermission("nerduhc.gamemaster")) {
			giveGamemasterGUIItems(p);
		}
	}
	
	private void giveGamemasterGUIItems(Player p) {
		p.getInventory().setItem(4, startmatch);
		p.getInventory().setItem(6, setduration);
		p.getInventory().setItem(7, setdifficulty);
		p.getInventory().setItem(8, setfriendlyfire);
	}
	
	private void build() {
		teamGUI = Bukkit.createInventory(null, 27, "Select a team:");
		durationGUI = Bukkit.createInventory(null, 9, "Set duration:");
		difficultyGUI = Bukkit.createInventory(null, 9, "Set difficulty:");
		friendlyfireGUI = Bukkit.createInventory(null, 9, "Set friendly fire:");
		
		// player items
		
		ItemMeta joinateammeta = joinateam.getItemMeta();
		joinateammeta.setDisplayName("Join a team");
		joinateam.setItemMeta(joinateammeta);
		
		ItemMeta spectatemeta = spectate.getItemMeta();
		spectatemeta.setDisplayName("Spectate");
		spectate.setItemMeta(spectatemeta);
		
		ItemMeta teamlistmeta = teamlist.getItemMeta();
		teamlistmeta.setDisplayName("Team list");
		teamlist.setItemMeta(teamlistmeta);
		
		ItemMeta randomteammeta = randomteam.getItemMeta();
		randomteammeta.setDisplayName("Random");
		randomteam.setItemMeta(randomteammeta);
		
		// gamemaster items
		// duration
		
		ItemMeta setdurationmeta = setduration.getItemMeta();
		setdurationmeta.setDisplayName("Duration");
		setdurationmeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		setduration.setItemMeta(setdurationmeta);
		
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
		
		ItemMeta setdifficultymeta = setdifficulty.getItemMeta();
		setdifficultymeta.setDisplayName("Difficulty");
		setdifficultymeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		setdifficulty.setItemMeta(setdifficultymeta);
		
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
		
		ItemMeta setfriendlyfiremeta = setfriendlyfire.getItemMeta();
		setfriendlyfiremeta.setDisplayName("Friendly Fire");
		setfriendlyfiremeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		setfriendlyfire.setItemMeta(setfriendlyfiremeta);
		
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
		ItemMeta startmatchmeta = startmatch.getItemMeta();
		startmatchmeta.setDisplayName("Start match!");
		startmatchmeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		startmatch.setItemMeta(startmatchmeta);
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
			
			lore.add(ChatColor.WHITE + "Players: " + t.getSize() + "/" + plugin.CONFIG.MAX_TEAM_SIZE);
			lore.add(" " + ChatColor.RESET);
			for (String s : t.getEntries()) {
				lore.add(ChatColor.WHITE + "" + s);
			}
			m.setLore(lore);
			
			i.setItemMeta(m);
			
			teamGUI.addItem(i);
			
			stackmap.put(i, t);
		}
		
		teamGUI.addItem(randomteam);
		
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
		
		if (e.getItem() == null) {
			return;
		}
		
		Player p = e.getPlayer();
		String s = e.getItem().getItemMeta().getDisplayName();
		Boolean gm = p.hasPermission("nerduhc.gamemaster");

		switch (s) {
			case "Join a team":
				p.openInventory(teamGUI);
				break;
			case "Spectate":
				if (match.getScoreboard().getEntryTeam(p.getName()) != null)
					match.getScoreboard().getEntryTeam(p.getName()).removeEntry(p.getName());
				p.setGameMode(GameMode.SPECTATOR);
				p.setAllowFlight(true);
				p.setFlying(true);
				UHCSound.JOINTEAM.playSound(p);
				p.sendMessage(ChatColor.GOLD + "You are now spectating this match.");
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Run /join [team] if you change your mind before the match starts.");
				match.getScoreboardHandler().refresh();
				refreshItemGUI();
				break;
			case "Team list":
				if (match.getGameMode() == UHCGameMode.TEAM) {
					match.getScoreboard().getTeams().forEach(t -> p.sendMessage(t.getDisplayName() + ChatColor.WHITE + "(" + t.getSize() + "/" + plugin.CONFIG.MAX_TEAM_SIZE + ")"));
					UHCSound.DING.playSound(p);
				}
				break;
			case "Duration":
				if (gm) p.openInventory(durationGUI);
				break;
			case "Start match!":
				if (gm && match.getMatchState().equals(UHCMatchState.PREGAME)) {
					match.beginMatchStartCountdown();
				}
				break;
			case "Difficulty":
				if (gm) p.openInventory(difficultyGUI);
				break;
			case "Friendly Fire":
				if (gm) p.openInventory(friendlyfireGUI);
				break;
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
		if (s == "") return;
		
		// do stuff
		
		if (e.getInventory().getName() == teamGUI.getName()) {	
			
			// selecting a team?
			
			if (stackmap.containsKey(i)) {
				Team t = stackmap.get(i);
				if (t.getSize() < plugin.CONFIG.MAX_TEAM_SIZE) {
					match.getScoreboardHandler().addPlayerToTeam(p, t.getName());
					p.closeInventory();
					refreshItemGUI();
				} else {
					p.sendMessage(ChatColor.RED + "That team is full!");
					UHCSound.OOPS.playSound(p);
				}
			} else if (s == "Random") {
				Boolean foundTeam = false;
				
				teamList.clear();
				teamList.addAll(match.getScoreboard().getTeams());
				
				while (!foundTeam) {
					Random randInt = new Random();
					Integer j = randInt.nextInt(teamList.size());

					Team t = teamList.get(j);
					String teamName = t.getName();

					if (t.getSize() < plugin.CONFIG.MAX_TEAM_SIZE) {
						match.getScoreboardHandler().addPlayerToTeam(p, teamName);
						p.closeInventory();
						refreshItemGUI();
						foundTeam = true;
					} else {
						teamList.remove((int) j);
					}
					
					if (teamList.size() == 0) {
						p.sendMessage(ChatColor.RED + "Sorry, no teams are available to join.");
						foundTeam = true;
					}
				}
				
			} else {
				e.getInventory().remove(i);
			}
			
		} else if (e.getInventory().getName() == durationGUI.getName()) {
			
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
			
		} else if (e.getInventory().getName() == difficultyGUI.getName()) {
			
			// changing world difficulty?
			
			if (i.getType() != Material.WOOL) return; // that's not right
			
			match.getWorld().setDifficulty(Difficulty.valueOf(s.toUpperCase()));
			UHCSound.DING.playSound(p);
			p.sendMessage(ChatColor.GOLD + "Difficulty set to " + s);
			p.closeInventory();
			
		} else if (e.getInventory().getName() == friendlyfireGUI.getName()) {
			
			// changing friendly fire state?
			
			if (i.getType() != Material.DIAMOND_SWORD && i.getType() != Material.SHIELD) return; // that's not right
			
			for (Team t : match.getScoreboard().getTeams()) {
				t.setAllowFriendlyFire(s == "Enabled");
			}
			
			UHCSound.DING.playSound(p);
			p.sendMessage(ChatColor.GOLD + "Friendly fire is now " + ((s == "Enabled") ? "enabled" : "disabled"));
			p.closeInventory();
			
		}
		
		// and cancel
		
		e.setCancelled(true);
	}
}
