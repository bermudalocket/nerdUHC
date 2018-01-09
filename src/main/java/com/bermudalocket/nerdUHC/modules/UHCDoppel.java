package com.bermudalocket.nerdUHC.modules;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class UHCDoppel {
	
	private UUID _doppel;
	private UUID _player;
	
	private String _name;
	private ItemStack[] _drops;
	private double _maxhealth;
	private double _health;
	private boolean _AI;
	private boolean _alive;
	
	private EntityDeathEvent lastdeath;
	
	public UHCDoppel(UUID doppel, UUID player) {
		_doppel = doppel;
		_player = player;
	}
	
	public LivingEntity getEntity() {
		return (LivingEntity) Bukkit.getEntity(_doppel);
	}

	public UUID getPlayer() {
		return _player;
	}
	
	public void setDrops(ItemStack[] itemStacks) {
		_drops = itemStacks;
	}
	
	public ItemStack[] getDrops() {
		return _drops;
	}
	
	public void setMaxHealth(int maxhealth) {
		_maxhealth = maxhealth;
		getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxhealth);
	}
	
	public void setHealth(double health) {
		_health = health;
		getEntity().setHealth(health);
	}
	
	public double getHealth() {
		return _health;
	}
	
	public void setAI(boolean AI) {
		_AI = AI;
		getEntity().setAI(AI);
	}
	
	public void setName(String name) {
		_name = name;
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
		getEntity().setHealth(_health);
	}
	
	public void recordDeath(EntityDeathEvent e) {
		lastdeath = e;
	}
	
	public EntityDeathEvent getLastDeath() {
		return lastdeath;
	}
	
}
