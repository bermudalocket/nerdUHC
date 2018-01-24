package com.bermudalocket.nerdUHC.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.bermudalocket.nerdUHC.NerdUHC;

public class UHCUtils {

	private NerdUHC plugin;
	private UHCMatch match;
	private World world;

	private static final Set<Material> omitMaterials = new HashSet<Material>(Arrays.asList(Material.LAVA,
			Material.WATER, Material.LEAVES, Material.END_GATEWAY, Material.PORTAL, Material.CACTUS, Material.MAGMA));

	public UHCUtils(NerdUHC plugin, UHCMatch match) {
		this.plugin = plugin;
		this.match = match;
		this.world = match.getWorld();
		drawBarrier(true);
	}

	public void drawBarrier(boolean draw) {

		int x = plugin.CONFIG.SPAWN_X;
		int y = plugin.CONFIG.SPAWN_Y;
		int z = plugin.CONFIG.SPAWN_Z;

		int radius = plugin.CONFIG.SPAWN_BARRIER_RADIUS;
		Material barriertype = plugin.CONFIG.SPAWN_BARRIER_BLOCK;

		Location center = new Location(world, x, y, z);
		Location point = new Location(world, 0, 0, 0);

		Material onlyreplace;
		Material replacewith;
		if (draw) {
			onlyreplace = Material.AIR;
			replacewith = barriertype;
		} else {
			onlyreplace = barriertype;
			replacewith = Material.AIR;
		}

		if (radius <= 0)
			return;

		for (int i = -radius - 1; i < radius + 1; i++) {
			for (int j = -radius - 1; j < radius + 1; j++) {
				for (int k = -radius - 1; k < radius + 1; k++) {
					point.setX(x + i);
					point.setY(y + j);
					point.setZ(z + k);
					Material lookingat = world.getBlockAt(point).getType();
					if (Math.abs((int) Math.round(center.distanceSquared(point)) - radius * radius) <= radius) {
						if (lookingat == onlyreplace) {
							world.getBlockAt(point).setType(replacewith);
						}
					}
				}
			}
		}
	}

	// Custom spreadplayers method
	
	// First: layNodes() maps n nodes on a circle around a specified center with a specified radius
	// Second: spread() checks a 7x7 area centered at the block with isValidPosition()
	// Last: spread() teleports the player (and, if applicable, their teammates) to the position

	public ArrayList<Vector> layNodes(int nodes, double radius, Vector center) {

		// List of nodes
		ArrayList<Vector> vectors = new ArrayList<Vector>();

		// Set up default position at 0 degrees
		int x = (int) (center.getX() + radius);
		int z = (int) center.getZ();
		int y = world.getHighestBlockYAt(x, z);

		// Split up the inner angle (2pi rad, aka 360 deg) into equal pieces
		double angle = (2 * Math.PI) / nodes;

		// Add the first (0 degree) node to the list
		vectors.add(new Vector(x, y, z));

		// Iterate over the rest of the circle
		for (int i = 1; i < nodes; i++) {
			int nextX = (int) Math.cos(i * angle);
			int nextY = world.getHighestBlockYAt((int) x, (int) z);
			int nextZ = (int) Math.sin(i * angle + Math.asin(y / radius));
			vectors.add(new Vector(nextX, nextY, nextZ));
		}

		return vectors;
	}

	// deprecated method: Team#getPlayers
	@SuppressWarnings("deprecation")
	public void spread(ArrayList<Player> players) {

		int nodes = players.size();
		double radius = world.getWorldBorder().getSize() / 2 - 200;
		Vector center = world.getSpawnLocation().toVector();

		vectorSearch:
			for (Vector v : layNodes(nodes, radius, center)) {
				for (int x = v.getBlockX() - 3; x <= v.getBlockX() + 3; x++) {
					for (int z = v.getBlockZ() - 3; z <= v.getBlockZ() + 3; z++) {
						Block pos = world.getHighestBlockAt(x, z);
						if (isValidPosition(pos)) {
							Location tp = new Location(world, pos.getX(), pos.getY() + 1, pos.getZ());
							Player p = players.get(0);
							Team t = match.getTeamForPlayer(p);
							if (t == null) {
								p.teleport(tp);
								players.remove(p);
							} else {
								for (OfflinePlayer player : t.getPlayers()) {
									if (player.isOnline()) {
										player.getPlayer().teleport(tp);
										players.remove(player.getPlayer());
									}
								}
							}
							continue vectorSearch;
						}
					}
				}
			}
	}

	// Check to make sure we won't kill the player or put them somewhere ridiculous
	private boolean isValidPosition(Block block) {
		
		// Check if position is safe
		if (omitMaterials.contains(block.getType()))
			return false;

		// Check around the block to make sure there's nothing bad around
		// and that elevation doesn't make a quick change
		for (int x = block.getX() - 1; x <= block.getX() + 1; x++) {
			for (int z = block.getZ() - 1; z <= block.getZ(); z++) {
				
				Block currentposition = world.getHighestBlockAt(x, z);
				Material type = currentposition.getType();

				if (Math.abs(currentposition.getY() - block.getY()) > 6) {
					return false;
				}
				if (omitMaterials.contains(type)) {
					return false;
				}
				
			}
		}
		return true;
	}

}
