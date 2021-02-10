package me.raptor.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class PoiseidonsArmy extends AbstractSkill implements Listener {
	Skill plugin;
	boolean isActivating = false;
	int click = 0;
	List<LivingEntity> army = new ArrayList<>();
	NamespacedKey key;
	
	public PoiseidonsArmy(Skill plugin) {
		super(plugin);
		this.plugin = plugin;
		key = new NamespacedKey(plugin, "poiseidon");
	}
	
	@EventHandler
	public void onPlayerActivateAbility(PlayerInteractEvent e) {
		if (checkRequirement(e, "ArmyOfPoiseidon", "Poiseidon's Army")) {
			Player p = e.getPlayer();
			spawnArmy(p, "ArmyOfPoiseidon", 60);
		}
	}
	
	@EventHandler
	public void onBodyguardChooseTarget(EntityTargetLivingEntityEvent e) {
		if (!(e.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity) e.getEntity();
		if (checkEntityString(le, "drowned")) {
			if (le instanceof Player || checkEntityString((LivingEntity) e.getEntity(), "drowned")) { 
				e.setTarget(null);
				chooseTargetNearby((LivingEntity) e.getEntity());
				e.setTarget(chooseTargetNearby(le));
			}
		}
		
	}
	
	public void spawnArmy(Player p, String skill, int cooldown) {
		Cooldown.setCooldown(p, skill, cooldown);
		for (int i = 0; i <  9; i++) {
			Drowned e = (Drowned) p.getWorld().spawnEntity(p.getLocation(), EntityType.DROWNED);
			e.setHealth(15);
			e.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
			createCustomEntity(e, "drowned");
			e.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1, 20 * 10));
			e.setCustomName(ChatColor.AQUA + p.getName() + "'s Bodyguard");
			e.setCustomNameVisible(true);
			army.add(e);
			new BukkitRunnable() {
				@Override
				public void run() {				
					if (e.getTarget() == null || e.getTarget().isDead()) {
						e.setTarget(chooseTargetNearby(e));
						if (e.isDead()) {
							cancel();
						}
					}
				}
			}.runTaskTimer(plugin, 0, 20);
		}
		LivingEntity e = army.get(0);
		createCustomEntity(e, "drowned");
		e.setCustomName(ChatColor.BLUE + p.getName() + "'s Knight");
		e.setCustomNameVisible(true);
		e.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
		e.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Entity e : army) {
					if (e.isDead() != true) {
						e.remove();
						e.getWorld().spawnParticle(Particle.ASH, e.getLocation(), 80, 2, 2, 2);
					}
				}
			}
		}.runTaskLater(plugin, 20 * 15);
		
	}
	
	public LivingEntity chooseTargetNearby(LivingEntity le) {
		List<Monster> nearby = new ArrayList<>();
		for (Entity e : le.getNearbyEntities(10, 10, 10)) {
			if (e instanceof Monster) {
				if (e.equals(le) || checkEntity(le, (LivingEntity) e)) continue;
				Monster p = (Monster) e;
				nearby.add(p);
			}			
		}
		if (nearby.size() == 0) return null;
		Random r = new Random();
		LivingEntity target = nearby.get(r.nextInt(nearby.size()));
		return target;
	}
	
	public void createCustomEntity(LivingEntity e, String s) {
		PersistentDataContainer name = e.getPersistentDataContainer();
		name.set(key, PersistentDataType.STRING, s);
	}
	
	public boolean checkEntity(LivingEntity e, LivingEntity check) {
		String s, s1;
		PersistentDataContainer name = e.getPersistentDataContainer();
		PersistentDataContainer tar = check.getPersistentDataContainer();
		if(name.has(key , PersistentDataType.STRING)) {
		    s1 = name.get(key, PersistentDataType.STRING);
		    s = tar.get(key, PersistentDataType.STRING);
			return s1.equals(s);
		}
		return false;
	}
	
	public boolean checkEntityString(LivingEntity e, String s) {
		String check;
		PersistentDataContainer name = e.getPersistentDataContainer();
		if(name.has(key , PersistentDataType.STRING)) {
		    check = name.get(key, PersistentDataType.STRING);
			return check.equals(s);
		}
		return false;
	}
}
