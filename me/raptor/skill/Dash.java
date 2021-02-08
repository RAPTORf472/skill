package me.raptor.skill;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class Dash implements Listener{
	
	Skill plugin;
	boolean isActivating = false;
	int click = 0;	
	int hunger;
	Player p;
	
	public Dash(Skill plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerActivateDash(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK 
				|| e.getAction() == Action.RIGHT_CLICK_AIR) {
				EquipmentSlot hand = e.getHand();
				if (!hand.equals(EquipmentSlot.HAND)) return;
				Player p = e.getPlayer();
				this. p = p;
				hunger = p.getFoodLevel();
				if (!isEquipping("Dash")) return;
				if (p.isSprinting()) {
					isActivating = true;
					if (checkSafemode(p)) return;
					if (isActivating) click++;
					if (click == 3) {
						click = 0;
						if (Cooldown.isOnCooldown(p, "Dash")) {
							p.sendMessage(ChatColor.RED + "You can use Dash after " + Cooldown.getTimeLeft(p, "Dash"));
							return;
						}
						Cooldown.setCooldown(p, "Dash", 10f);
						if (hunger <= 6) {
							p.sendMessage(ChatColor.YELLOW + "Hunger too low!");
							return;
						}
						hunger -= 4;
						dash(p);
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
	
	public void dash(Player p) {
		p.setVelocity(p.getLocation().getDirection().multiply(3));
		p.sendMessage(ChatColor.GREEN + "Whooosh");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 5, 3);
		p.getWorld().createExplosion(p.getLocation(), 0);
		p.setFoodLevel(hunger);
		BukkitTask dashing = new BukkitRunnable() {
			@Override
			public void run() {
				p.spawnParticle(Particle.REDSTONE, p.getLocation().add(0, 2, 0), 20, 2, 2, 2, new Particle.DustOptions(Color.WHITE, 0.5f));
			}
		}.runTaskTimer(plugin, 0, 2);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.spawnParticle(Particle.EXPLOSION_LARGE, p.getLocation(), 3);
				for (Entity e : p.getNearbyEntities(2, 2, 2)) {
					if (e instanceof LivingEntity) ((LivingEntity) e).damage(2);
				}
				dashing.cancel();
			}
		}.runTaskLater(plugin, 15);
	}
	
	public boolean isEquipping(String skill) {
		if (plugin.getConfig().get(p.getName() + ".Equipping").equals(skill)) return true;
		else return false;		
	}
	
	public boolean checkSafemode(Player p) {
		return plugin.getConfig().getBoolean(p.getName() + ".Safemode");
	}
}
