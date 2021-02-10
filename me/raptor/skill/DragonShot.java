package me.raptor.skill;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class DragonShot extends AbstractSkill implements Listener {

	Skill plugin;
	boolean isActivating = false;
	int click = 0;
	NamespacedKey key;
	
	public DragonShot(Skill plugin) {
		super(plugin);
		this.plugin = plugin;

	}
	
	@EventHandler
	public void onPlayerActivateAbility(PlayerInteractEvent e) {
		if (checkRequirement(e, "DragonShot", "Dragon Shot")) {
			dragonShot(e.getPlayer(), "DragonShot", 40);
		}
	}

	public void dragonShot(Player p, String skill, int cooldownTime) {
		List<LivingEntity> tar = new ArrayList<LivingEntity>();
		for (Entity e : p.getWorld().getNearbyEntities(p.getLocation().add(0, 1, 0), 0.5, 2, 0.5)) {
			if (e instanceof LivingEntity) {
				if (e.equals(p)) continue;
				LivingEntity le = (LivingEntity) e;
				tar.add(le);
			}
		}
		if (tar.isEmpty()) {
			p.sendMessage(ChatColor.LIGHT_PURPLE + "Dragon Shot failed");
			p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 10, 1);
			return;
		} 
		p.sendMessage(ChatColor.LIGHT_PURPLE + "Dragon Shot!");
		p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 10, 1);
		for (LivingEntity le : tar) {
			le.getWorld().spawnParticle(Particle.DRAGON_BREATH, le.getLocation(), 100, 2, 2, 2, 0.5);
			le.damage(80);
			Vector v = le.getLocation().add(0, 0.25, 0).toVector().subtract(p.getLocation().toVector());
			le.setVelocity(v.multiply(3));
		}
		Cooldown.setCooldown(p, skill, cooldownTime);
	}
	
	
}
