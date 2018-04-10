package com.bermudalocket.nerdUHC.util;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class Util {

	public final Random random = new Random();

	public void removeGhastTears(EntityDeathEvent e) {
		List<ItemStack> drops = e.getDrops();
		for (int i = 0; i < drops.size(); i++) {
			if (drops.get(i).getType() == Material.GHAST_TEAR) {
				drops.remove(i);
				drops.add(new ItemStack(Material.GOLD_INGOT, 1));
			}
		}
		e.getDrops().clear();
		e.getDrops().addAll(drops);
	}

}
