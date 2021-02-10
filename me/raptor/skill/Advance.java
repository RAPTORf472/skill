package me.raptor.skill;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class Advance extends AbstractSkill implements Listener {

	Player p;
	Skill plugin;
	boolean isActivating = false;
	boolean isUsing = false;
	int click = 0;
	
	public Advance(Skill plugin) {
		super(plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerActivateAbility(PlayerInteractEvent e) {
		if (checkRequirement(e, "Advance", "Advance")) {
			this.p = e.getPlayer();
			activateAdvance(p, "Advance", 70);
		}
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
	
	public void activateAdvance(Player p, String skill, int cooldown) {
		p.sendMessage(ChatColor.RED + "Advance!");
		if (!checkHunger(p, 6)) return;
		isUsing = true;
		Cooldown.setCooldown(p, skill, cooldown);
		p.playSound(p.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 5, 3);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 100, 2));
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 100, 3));
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 100, 1));
		p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setFoodLevel(p.getFoodLevel() - 2);
				p.damage(0.25);
				p.spawnParticle(Particle.REDSTONE, p.getLocation().add(0, 1, 0), 50, 1.5d, 1.5d, 1.5d, new Particle.DustOptions(Color.RED, 1.5f));
				if (p.getFoodLevel() <= 6) {
					isUsing = false;
					p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Advance's Over!");
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
}
