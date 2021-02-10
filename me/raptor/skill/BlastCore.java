package me.raptor.skill;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;

public class BlastCore extends AbstractSkill implements Listener {
	Player p;
	Skill plugin;
	boolean isActivating = false;
	int click = 0;
	public BlastCore(Skill plugin) {
		super(plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerActivateAbility(PlayerInteractEvent e) {
		if (checkRequirement(e, "BlastCore", "Blast Core")) {
			blastCore(e.getPlayer());
		}
	}
	
	public void blastCore(Player p) {
		Cooldown.setCooldown(p, "BlastCore", 70f);
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

}
