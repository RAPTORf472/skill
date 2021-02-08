package me.raptor.skill;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class PoiseidonsArmy implements Listener{
	Player p;
	Skill plugin;
	boolean isActivating = false;
	int click = 0;
	List<LivingEntity> army = new ArrayList<>();
	
	public PoiseidonsArmy(Skill plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerActivateAbility(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK 
			|| e.getAction() == Action.RIGHT_CLICK_AIR) {
			EquipmentSlot hand = e.getHand();
			if (!hand.equals(EquipmentSlot.HAND)) return;
			Player p = e.getPlayer();
			this.p = p;
			if (p.isSneaking()) {
				isActivating = true;
				if (checkSafemode(p)) return;
				if (isActivating) click++;
				if (!isEquipping("ArmyOfPoiseidon")) return;
				if (click == 3) {
					click = 0;
					if (Cooldown.isOnCooldown(p, "ArmyOfPoiseidon")) {
						p.sendMessage(ChatColor.RED + "You can use Poiseidon's Army after " + Cooldown.getTimeLeft(p, "ArmyOfPoiseidon"));
						return;
					}
					Cooldown.setCooldown(p, "ArmyOfPoiseidon", 60f);
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Attack!");
					spawnArmy(p);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					if (isActivating) {
						click = 0;
						isActivating = false;
					}
				}
			}.runTaskLater(plugin, 20);
		}
		}
	}
	
	@EventHandler
	public void onMobFindingTarget(EntityTargetLivingEntityEvent e) {
		if (e.getTarget() == p && army.contains(e.getEntity())) e.setTarget(null);
	}
	
	public void spawnArmy(Player p) {
		for (int i = 0; i <  9; i++) {
			LivingEntity e = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.DROWNED);
			e.setHealth(15);
			e.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1, 20 * 10));
			e.setCustomName(ChatColor.AQUA + p.getName() + "'s Bodyguard");
			e.setCustomNameVisible(true);
			army.add(e);
		}
		LivingEntity e = army.get(0);
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
						e.getWorld().spawnParticle(Particle.ASH, e.getLocation(), 20, 2, 2, 2);
					}
				}
			}
		}.runTaskLater(plugin, 20 * 15);
	}
	
	public boolean isEquipping(String skill) {
		if (plugin.getConfig().get(p.getName() + ".Equipping").equals(skill)) return true;
		else return false;		
	}
	
	public boolean checkSafemode(Player p) {
		return plugin.getConfig().getBoolean(p.getName() + ".Safemode");
	}
}
