package com.bermudalocket.nerdUHC.gui;

import com.bermudalocket.nerdUHC.Configuration;
import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.util.MatchState;
import com.bermudalocket.nerdUHC.util.UHCSounds;
import com.bermudalocket.nerdUHC.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;

import static com.bermudalocket.nerdUHC.NerdUHC.MATCH_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.PLAYER_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.SCOREBOARD_HANDLER;

public class GUIHandler implements Listener {

    private final HashMap<ItemStack, Team> _itemToTeamMap = new HashMap<>();

    public GUIHandler() {
        teamsToItems();
    }

    public void startListening() {
        NerdUHC.PLUGIN.getServer().getPluginManager().registerEvents(this, NerdUHC.PLUGIN);
    }

    public void stopListening() {
        HandlerList.unregisterAll(this);
    }

    /**
     * Gives a player GUI items
     *
     * @param p the player
     */
    public void givePlayerGUIItems(Player p) {
        p.getInventory().setItem(0, GUIStatic.MENU_JOIN_TEAM);
        p.getInventory().setItem(1, GUIStatic.MENU_SPECTATE);
        p.getInventory().setItem(2, GUIStatic.MENU_INFO_BOOK);

        if (p.hasPermission("nerduhc.gamemaster")) {
            p.getInventory().setItem(4, GUIStatic.MENU_START_MATCH);
            p.getInventory().setItem(6, GUIStatic.MENU_DURATION);
            p.getInventory().setItem(7, GUIStatic.MENU_DIFFICULTY);
            p.getInventory().setItem(8, GUIStatic.MENU_FRIENDLY_FIRE);
        }
    }

    /**
     * Turn teams into item representatives
     */
    private void teamsToItems() {

        for (Team team : SCOREBOARD_HANDLER.getRegisteredTeams()) {

            ArrayList<String> lore = new ArrayList<>();
            ChatColor teamColor = team.getColor();
            DyeColor dyeColor = Util.chatColorToDyeColor(teamColor);
            ItemStack teamItemStack = new Wool(dyeColor).toItemStack(1);
            ItemMeta teamItemMeta = teamItemStack.getItemMeta();

            teamItemMeta.setDisplayName(team.getDisplayName());

            lore.add(ChatColor.WHITE + "Players: " + team.getSize() + "/" + Configuration.MAX_TEAM_SIZE);
            team.getEntries().forEach(teamMate -> lore.add(ChatColor.WHITE + " - " + teamMate));
            teamItemMeta.setLore(lore);

            teamItemStack.setItemMeta(teamItemMeta);

            GUIStatic.TEAM_GUI.addItem(teamItemStack);
            _itemToTeamMap.put(teamItemStack, team);
        }

        // add an itemstack that represents the "join random team" option
        GUIStatic.TEAM_GUI.addItem(GUIStatic.TEAM_RANDOM);
    }

    /**
     * Refreshes the team itemstacks, specifically to update the lores which
     * list the players currently on that team
     */
    private synchronized void refreshGui() {
        GUIStatic.TEAM_GUI.clear();
        teamsToItems();
    }

    // -------------------------------------------------------------------------
    // EVENT HANDLERS
    // -------------------------------------------------------------------------

    /**
     * Prevent players from dropping GUI items while the GUI is needed
     *
     * @param e the PlayerDropItem event
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    /**
     * Handles the player clicking (i.e. interacting) while not in an inventory
     *
     * @param e the PlayerInteract event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        ItemStack clickedItem = e.getItem();
        if (clickedItem == null) return;

        Player player = e.getPlayer();
        Boolean gm = player.hasPermission("nerduhc.gamemaster");

        if (clickedItem.equals(GUIStatic.MENU_JOIN_TEAM)) {

            player.openInventory(GUIStatic.TEAM_GUI);

        } else if (clickedItem.equals(GUIStatic.MENU_SPECTATE)) {

            PLAYER_HANDLER.makeSpectator(player);
            refreshGui();

        } else if (clickedItem.equals(GUIStatic.MENU_DURATION) && gm) {

            player.openInventory(GUIStatic.DURATION_GUI);

        } else if (clickedItem.getType().equals(Material.WRITTEN_BOOK)) {

            player.performCommand("rules");

        } else if (clickedItem.equals(GUIStatic.MENU_START_MATCH) && gm) {

            Match match = MATCH_HANDLER.getMatch();
            if (match.inState(MatchState.PREGAME)) match.startCountdown();

        } else if (clickedItem.equals(GUIStatic.MENU_DIFFICULTY) && gm) {

            player.openInventory(GUIStatic.DIFFICULTY_GUI);

        } else if (clickedItem.equals(GUIStatic.MENU_FRIENDLY_FIRE) && gm) {

            player.openInventory(GUIStatic.FRIENDLY_FIRE_GUI);

        } else {

            player.getInventory().remove(clickedItem);

        }

        e.setCancelled(true);
    }

    @EventHandler
    public synchronized void onPlayerInvClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null) return;

        if (_itemToTeamMap.containsKey(clickedItem)) {

            Team team = _itemToTeamMap.get(clickedItem);
            player.performCommand("join " + team.getName());
            player.closeInventory();
            refreshGui();

        } else if (clickedItem.equals(GUIStatic.TEAM_RANDOM)) {

            SCOREBOARD_HANDLER.addPlayerRandomTeam(player);
            player.closeInventory();
            refreshGui();

        } else if (clickedItem.equals(GUIStatic.DURATION_2)) {

            setDuration(player, 2);

        } else if (clickedItem.equals(GUIStatic.DURATION_30)) {

            setDuration(player, 30);

        } else if (clickedItem.equals(GUIStatic.DURATION_60)) {

            setDuration(player, 60);

        } else if (clickedItem.equals(GUIStatic.DURATION_120)) {

            setDuration(player, 120);

        } else if (clickedItem.equals(GUIStatic.DURATION_180)) {

            setDuration(player, 180);

        } else if (clickedItem.equals(GUIStatic.DIFFICULTY_PEACEFUL)) {

            setDifficulty(player, Difficulty.PEACEFUL);

        } else if (clickedItem.equals(GUIStatic.DIFFICULTY_EASY)) {

            setDifficulty(player, Difficulty.EASY);

        } else if (clickedItem.equals(GUIStatic.DIFFICULTY_NORMAL)) {

            setDifficulty(player, Difficulty.NORMAL);

        } else if (clickedItem.equals(GUIStatic.DIFFICULTY_HARD)) {

            setDifficulty(player, Difficulty.HARD);

        } else if (clickedItem.equals(GUIStatic.FRIENDLY_FIRE_OFF)) {

            setFriendlyFire(player, false);

        } else if (clickedItem.equals(GUIStatic.FRIENDLY_FIRE_ON)) {

            setFriendlyFire(player, true);

        }

        SCOREBOARD_HANDLER.refresh();
        e.setCancelled(true);
    }

    private void setDuration(Player player, int duration) {
        MATCH_HANDLER.getMatch().setDuration(duration);
        UHCSounds.DING.playSound(player);
        player.sendMessage(ChatColor.GOLD + "Duration set to " + duration + " minutes.");
        player.closeInventory();
    }

    private void setDifficulty(Player player, Difficulty difficulty) {
        MATCH_HANDLER.getMatch().getWorld().setDifficulty(difficulty);
        UHCSounds.DING.playSound(player);
        player.sendMessage(ChatColor.GOLD + "Difficulty set to " + difficulty.toString());
        player.closeInventory();
    }

    private void setFriendlyFire(Player player, boolean state) {
        SCOREBOARD_HANDLER.getRegisteredTeams().forEach(team ->
                team.setAllowFriendlyFire(state));
        UHCSounds.DING.playSound(player);
        player.sendMessage(ChatColor.GOLD + "Friendly fire is now " + ((state) ? "enabled" : "disabled"));
        player.closeInventory();
    }
}
