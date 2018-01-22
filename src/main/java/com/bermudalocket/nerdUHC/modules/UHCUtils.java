package com.bermudalocket.nerdUHC.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bermudalocket.nerdUHC.NerdUHC;

public class UHCUtils {
	
	private NerdUHC plugin;
	private UHCMatch match;
	private World world;
	
	private static final Set<Material> omitMaterials = new HashSet<Material>(Arrays.asList(Material.LAVA, Material.WATER, Material.LEAVES, Material.END_GATEWAY, Material.PORTAL, Material.CACTUS, Material.MAGMA));
	
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
		
		if (radius <= 0) return;

		for (int i = -radius - 1; i < radius + 1; i++) {
			for (int j = -radius - 1; j < radius + 1; j++) {
				for (int k = -radius - 1; k < radius + 1; k++) {
					point.setX(x+i);
					point.setY(y+j);
					point.setZ(z+k);
					Material lookingat = world.getBlockAt(point).getType();
					if (Math.abs((int) Math.round(center.distanceSquared(point)) - radius*radius) <= radius) {
						if (lookingat == onlyreplace) {
							world.getBlockAt(point).setType(replacewith);
						}
					}
				}
			}
		}
	}
	
	// Custom spreadplayers method
	
	public ArrayList<Vector> layNodes(int nodes, double radius, Vector center) {
		plugin.getLogger().info("laying nodes");
		ArrayList<Vector> vectors = new ArrayList<Vector>();
		double x = center.getX() + radius;
		double y = center.getY();
		double z = center.getZ();
				
		// Split up the inner angle (2pi rad, aka 360 deg) into equal pieces
		double angle = (2 * Math.PI) / nodes;
		
		vectors.add(new Vector(x,world.getHighestBlockYAt((int) x, (int) z),z));
		for (int i = 1; i < nodes; i++) {
			double nextX = Math.cos(i*angle);
			double nextZ = Math.sin(i*angle + Math.asin(y/radius));
			vectors.add(new Vector(nextX, world.getHighestBlockYAt((int) x, (int) z), nextZ));
			plugin.getLogger().info("found node at " + nextX + "," + nextZ);
		}
		
		return vectors;
	}
	
	public void spread(ArrayList<Player> players) {
		plugin.getLogger().info("calling spread");
		int nodes = players.size();
		double radius = world.getWorldBorder().getSize()/2 - 200;
		Vector center = world.getSpawnLocation().toVector();
		
		vectorSearch: for (Vector v : layNodes(nodes, radius, center)) {
			plugin.getLogger().info("vector: " + v.toString());
			//
			for (double x = v.getX() - 4; x <= v.getX() + 4; x++) {
				for (double z = v.getZ() - 4; z <= v.getZ() + 4; z++) {
					Block pos = world.getHighestBlockAt((int) x, (int) z);
					plugin.getLogger().info("checking " + pos.getType() + " at " + pos.getLocation().toString());
					if (isValidPosition(pos)) {
						Player p = players.get(0);
						p.teleport(new Location(world, pos.getX(), pos.getY() + 1, pos.getZ()));
						plugin.getLogger().info("Spread " + p.getName() + " to (" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")");
						players.remove(p);
						continue vectorSearch;
					}
				}
			}
			//
		}
	}
	
	private boolean isValidPosition(Block block) {
		if (omitMaterials.contains(block.getType())) return false;
		
		for (int x = block.getX() - 1; x <= block.getX() + 1; x++) {
			for (int z = block.getZ() - 1; z <= block.getZ(); z++) {
				Block currentposition = world.getHighestBlockAt(x,z);
				Material type = currentposition.getType();
				
				if (Math.abs(currentposition.getY() - block.getY()) > 6) {
					plugin.getLogger().info("too high");
					return false;
				}
				if (omitMaterials.contains(type)) {
					plugin.getLogger().info("bad type");
					return false;
				}
			}
		}
		return true;
	}

}
