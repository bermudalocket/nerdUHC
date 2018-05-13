package com.bermudalocket.nerdUHC.player;

import com.bermudalocket.nerdUHC.Configuration;
import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.util.MatchState;
import com.bermudalocket.nerdUHC.util.UHCLibrary;
import com.bermudalocket.nerdUHC.util.UHCSounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.bermudalocket.nerdUHC.NerdUHC.MATCH_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.SCOREBOARD_HANDLER;

public class PlayerHandler {

    public void resetPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setSaturation(20);
        player.getInventory().clear();
        player.setExp(0);
        SCOREBOARD_HANDLER.refresh();
    }

    public void makeSpectator(Player p) {
        if (SCOREBOARD_HANDLER.hasTeam(p)) {
            SCOREBOARD_HANDLER.removePlayerFromTeam(p, false);
        }
        UHCSounds.JOINTEAM.playSound(p);
        p.setAllowFlight(true);
        p.setGameMode(GameMode.SPECTATOR);
        p.setFlying(true);
        p.setDisplayName(ChatColor.GRAY + "" + ChatColor.ITALIC + p.getName() + ChatColor.WHITE);
        UHCLibrary.LIB_NOW_SPECTATING.tell(p);
        if (MATCH_HANDLER.getMatch().inState(MatchState.PREGAME)) {
            UHCLibrary.LIB_TO_EXIT_SPEC.tell(p);
        }
        SCOREBOARD_HANDLER.refresh();
    }

    public void formatDisplayName(Player player) {
        Team team = SCOREBOARD_HANDLER.getTeamByPlayer(player);
        ChatColor newColor = (team == null) ? ChatColor.RESET : team.getColor();
        player.setPlayerListName(newColor + player.getName());
        player.setDisplayName(newColor + player.getName() + ChatColor.WHITE);
    }

    public void spreadPlayers() {
        Match match = MATCH_HANDLER.getMatch();
        HashMap<Team, Location> teamLocations = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {

            Team team = SCOREBOARD_HANDLER.getTeamByPlayer(player);
            if (teamLocations.containsKey(team)) {
                safeTeleport(player, teamLocations.get(team));
                continue;
            }
            if (team == null) {
                player.setGameMode(GameMode.SPECTATOR);
                continue;
            }

            if (!match.inState(MatchState.DEATHMATCH)) resetPlayer(player);

            World world = match.getWorld();
            int multiplier = (match.inState(MatchState.PREGAME)) ? 20 : 2;
            int maxDistance = (int) (world.getWorldBorder().getSize() / multiplier) - 100;

            boolean unsafe = true;
            while (unsafe) {
                double mult1 = Math.random();
                double mult2 = Math.random();
                int sign1 = (Math.random() < 0.5) ? 1 : -1;
                int sign2 = (Math.random() < 0.5) ? 1 : -1;

                int x = (int) (sign1 * maxDistance * mult1);
                int z = (int) (sign2 * maxDistance * mult2);
                int y = world.getHighestBlockYAt(x, z);

                Biome newBiome = match.getWorld().getBiome(x, z);
                if (excludedBiomes.contains(newBiome)) continue;

                Material newBlock = match.getWorld().getBlockAt(x, y, z).getType();
                if (excludedBlocks.contains(newBlock)) continue;

                Location newLoc = new Location(match.getWorld(), x, y, z);
                safeTeleport(player, newLoc);

                if (Configuration.SPREAD_RESPECT_TEAMS) teamLocations.put(team, newLoc);

                unsafe = false;
            }
        }
        SCOREBOARD_HANDLER.refresh();
    }

    private void safeTeleport(Player player, Location newLoc) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 100));
        player.teleport(newLoc);
    }

    private static final ArrayList<Biome> excludedBiomes = new ArrayList<>(Arrays.asList(
            Biome.OCEAN, Biome.DEEP_OCEAN, Biome.FROZEN_OCEAN));

    private static final ArrayList<Material> excludedBlocks = new ArrayList<>(Arrays.asList(
            Material.LAVA, Material.STATIONARY_LAVA, Material.WATER, Material.STATIONARY_WATER,
            Material.LEAVES, Material.LEAVES_2, Material.CACTUS));

}