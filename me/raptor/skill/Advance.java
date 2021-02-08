package me.raptor.skill;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class Advance implements Listener {
	
	Player p;
	Skill plugin;
	boolean isActivating = false;
	boolean isUsing = false;
	int click = 0;
	int hunger;
	public Advance(Skill plugin) {
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
			hunger = p.getFoodLevel();
			this.p = p;
			if (!isEquipping("Advance")) return;
			if (p.isSneaking()) {
				isActivating = true;
				if (checkSafemode(p)) return;
				if (isActivating) click++;
				if (click == 3) {
					click = 0;
					if (isUsing) return;
					if (Cooldown.isOnCooldown(p, "Advance")) {
						p.sendMessage(ChatColor.RED + "You can use Advance after " + Cooldown.getTimeLeft(p, "Advance"));
						return;
					}
					Cooldown.setCooldown(p, "Advance", 70f);
					if (hunger <= 6) {
						p.sendMessage(ChatColor.YELLOW + "Hunger too low!");
						return;
					}
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Advance!");
					p.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, p.getLocation(), 25, 1, 1, 1);
					activateAdvance(p);
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
	
	public void activateAdvance(Player p) {
		isUsing = true;
		hunger = p.getFoodLevel();
		p.playSound(p.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 5, 3);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 100, 2));
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 100, 3));
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 100, 1));
		p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setFoodLevel(hunger -= 2);
				p.damage(0.25);
				p.spawnParticle(Particle.REDSTONE, p.getLocation().add(0, 1, 0), 50, 1.5d, 1.5d, 1.5d, new Particle.DustOptions(Color.RED, 1.5f));
				if (hunger <= 6) {
					isUsing = false;
					p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Advance Over!");
					p.removePotionEffect(PotionEffectType.SPEED);
					p.removePotionEffect(PotionEffectType.FAST_DIGGING);
					p.removePotionEffect(PotionEffectType.JUMP);
					p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 20, 10);
	}
	
	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (isUsing) {
				if (!p.equals(this.p)) return;
				e.setDamage(e.getDamage() * 3);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamaged(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (isUsing) {
				if (!p.equals(this.p)) return;
				e.setDamage(e.getDamage() * 2);
			}
		}
	}
	public boolean isEquipping(String skill) {
		if (plugin.getConfig().get(p.getName() + ".Equipping").equals(skill)) return true;
		else return false;		
	}
	
	public boolean checkSafemode(Player p) {
		return plugin.getConfig().getBoolean(p.getName() + ".Safemode");
	}
}
