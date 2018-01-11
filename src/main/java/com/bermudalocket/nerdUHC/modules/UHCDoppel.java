package com.bermudalocket.nerdUHC.modules;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.bermudalocket.nerdUHC.NerdUHC;

public class UHCDoppel {
	
	private NerdUHC plugin;
	
	private UUID _doppel;
	private UUID _player;
	
	private ArrayList<ItemStack> _drops = new ArrayList<ItemStack>();
	private float _xp;
	private double _health;
	private boolean _alive;
	private boolean _active;
	
	private EntityDeathEvent lastdeath;
	
	public UHCDoppel(UUID doppel, UUID player, NerdUHC plugin) {
		this.plugin = plugin;
		
		Player p = Bukkit.getPlayer(player);
		
		_doppel = doppel;
		_player = player;
		_alive = true;
		_active = true;
		
		setDrops(p.getInventory());
		setMaxHealth(20);
		setHealth(p.getHealth());
		setName(p.getDisplayName());
		setAI(false);
		setXP(p.getExp());
		
		plugin.match.getWorld().getNearbyEntities(p.getLocation(), 16, 16, 16).forEach(entity -> {
			plugin.getLogger().info("Check: " + entity.getName());
			if (entity instanceof Monster) {
				plugin.getLogger().info("PASS monster");
				if (((Monster) entity).getTarget().equals(p)) {
					plugin.getLogger().info("PASS targeting player");
					((Monster) entity).setTarget(getEntity());
				} else {
					plugin.getLogger().info("FAIL targeting player");
				}
			} else {
				plugin.getLogger().info("FAIL monster");
			}
		});
	}
	
	public LivingEntity getEntity() {
		Optional doppel = plugin.match.getWorld().getLivingEntities().stream().filter(entity -> entity.getUniqueId().equals(_doppel)).findFirst();
		if (doppel.isPresent()) {
			return (LivingEntity) doppel.get();
		} else {
			return null;
		}
	}
	
	public UUID getUUID() {
		return _doppel;
	}

	public UUID getPlayer() {
		return _player;
	}
	
	public void setDrops(PlayerInventory inv) {
		ItemStack[] contents = inv.getContents();
		ItemStack[] armor = inv.getArmorContents();
		ItemStack[] extra = inv.getExtraContents();
		
		for (int i = 0; i < contents.length; i++) {
			_drops.add(contents[i]);
		}
		for (int j = 0; j < armor.length; j++) {
			_drops.add(armor[j]);
		}
		for (int k = 0; k < extra.length; k++) {
			_drops.add(extra[k]);
		}
	}
	
	public ArrayList<ItemStack> getDrops() {
		return _drops;
	}
	
	public void setMaxHealth(int maxhealth) {
		getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxhealth);
	}
	
	public void setHealth(double health) {
		_health = health;
		getEntity().setHealth(health);
	}
	
	public double getHealth() {
		return _health;
	}
	
	public void setXP(float xp) {
		_xp = xp;
	}
	
	public float getXP() {
		return _xp;
	}
	
	public void setAI(boolean state) {
		getEntity().setAI(state);
	}
	
	public void setName(String name) {
		getEntity().setCustomName(name);
		getEntity().setCustomNameVisible(true);
	}
	
	public void setAlive(boolean state) {
		_alive = state;
	}
	
	public boolean isDead() {
		return !_alive;
	}
	
	public void despawn() {
		getEntity().remove();
	}
	
	public void damage(double dmg) {
		_health -= dmg;
		if (_health < 0) _health = 0;
		getEntity().setHealth(_health);
	}
	
	public void recordDeath(EntityDeathEvent e) {
		lastdeath = e;
	}
	
	public EntityDeathEvent getLastDeath() {
		return lastdeath;
	}
	
	public void setActive(boolean state) {
		_active = state;
	}
	
	public boolean isActive() {
		return _active;
	}
	
}
