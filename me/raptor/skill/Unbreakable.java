package me.raptor.skill;


import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class Unbreakable extends AbstractSkill implements Listener{
	Player p;
	Skill plugin;
	boolean isActivating = false;
	boolean isUsing = false;
	LivingEntity le;
	int click = 0;
	
	public Unbreakable(Skill plugin) {
		super(plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerActivateAbility(PlayerInteractEvent e) {
		if (checkRequirement(e, "Unbreakable", "Unbreakable")) {
			unbreakable(e.getPlayer(), "Unbreakable", 40);
		}
	}
	
	@EventHandler
	public void PlayerUseIronBreaker(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof LivingEntity && e.getEntity() instanceof LivingEntity) {
			le = (LivingEntity) e.getDamager();
			LivingEntity tar = (LivingEntity) e.getEntity();
			if (isUsing && le.equals(p)) {
				e.setDamage(e.getDamage() * 2);
				isUsing = false;
				Vector v = le.getLocation().add(0, 0.25, 0).toVector().subtract(tar.getLocation().toVector());
				tar.setVelocity(v.multiply(-1));
				p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				p.removePotionEffect(PotionEffectType.SLOW);
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.GRAY + "Ironbreaker!");
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 5, 1);
				e.getEntity().getWorld().spawnParticle(Particle.CRIT, e.getEntity().getLocation(), 20);
			}
		}
		
		
	}
	
	public void unbreakable(Player p, String skill, int cooldown) {
		Cooldown.setCooldown(p, skill, cooldown);
		isUsing = true;
		this.p = p;
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 3));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
		p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 5, 3);
		p.spawnParticle(Particle.REDSTONE, p.getLocation().add(0, 1, 0), 50, 1.5d, 1.5d, 1.5d, new Particle.DustOptions(Color.fromRGB(87, 65, 47), 1.5f));
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!isUsing) return;
				isUsing = false;
				p.sendMessage("Unbreakable's Over");
			}
		}.runTaskLater(plugin, 20 * 5);
	}
}
