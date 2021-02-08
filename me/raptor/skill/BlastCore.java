package me.raptor.skill;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class BlastCore implements Listener {
	Player p;
	Skill plugin;
	boolean isActivating = false;
	int click = 0;
	public BlastCore(Skill plugin) {
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
				if (isActivating) click++;
				if (!isEquipping("BlastCore")) return;
				if (checkSafemode(p)) return;
				if (click == 3) {
					click = 0;
					if (Cooldown.isOnCooldown(p, "BlastCore")) {
						p.sendMessage(ChatColor.RED + "You can use Blast Core after " + Cooldown.getTimeLeft(p, "BlastCore"));
						return;
					}
					Cooldown.setCooldown(p, "BlastCore", 70f);
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Blast!");
					blastCore(p);
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
	
	public void blastCore(Player p) {
		p.spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 14, 5, 5, 5);
		p.spawnParticle(Particle.FLAME, p.getLocation(), 150, 7, 7, 7);
		p.getWorld().createExplosion(p.getLocation(), 0);
		p.damage(3);
		for (Entity e : p.getNearbyEntities(7, 7, 7)) {
			if (e instanceof LivingEntity) {
				e.setFireTicks(20 * 3);
				((LivingEntity) e).damage(15);
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
