package com.bermudalocket.nerdUHC.modules;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.bermudalocket.nerdUHC.NerdUHC;

public class UHCDoppel {

	private NerdUHC plugin;

	private UUID uuid;
	private UHCPlayer player;

	private ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
	private float xp;

	public UHCDoppel(LivingEntity e, UHCPlayer p, NerdUHC plugin) {
		this.plugin = plugin;
		this.uuid = e.getUniqueId();
		this.player = p;
		
		e.setCustomName(p.getName() + " (" + p.getTeam().getName() + ")");
		e.setCustomNameVisible(true);
		e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		e.setHealth(p.bukkitPlayer().getHealth());
		e.setAI(false);
		
		setDrops(p.bukkitPlayer().getInventory());
		setXP(p.bukkitPlayer().getExp());
		
		plugin.CONFIG.WORLD.getNearbyEntities(p.bukkitPlayer().getLocation(), 16, 16, 16).forEach(entity -> {
			if (entity instanceof Monster) {
				if (((Monster) entity).getTarget().equals(p.bukkitPlayer())) {
					((Monster) entity).setTarget(bukkitEntity());
				}
			}
		});
	}

	public LivingEntity bukkitEntity() {
		for (LivingEntity e : plugin.CONFIG.WORLD.getLivingEntities()) {
			if (e.getUniqueId().equals(this.uuid))
				return e;
		}
		return null;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public UHCPlayer getPlayer() {
		return player;
	}

	public void setDrops(PlayerInventory inv) {
		ItemStack[] contents = inv.getContents();
		ItemStack[] armor = inv.getArmorContents();
		ItemStack[] extra = inv.getExtraContents();

		for (int i = 0; i < contents.length; i++) {
			this.drops.add(contents[i]);
		}
		for (int j = 0; j < armor.length; j++) {
			this.drops.add(armor[j]);
		}
		for (int k = 0; k < extra.length; k++) {
			this.drops.add(extra[k]);
		}
	}

	public ArrayList<ItemStack> getDrops() {
		return this.drops;
	}

	public void setXP(float xp) {
		this.xp = xp;
	}

	public float getXP() {
		return this.xp;
	}

}
