package me.raptor.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class Blink extends AbstractSkill implements Listener {
	
	List<String> randomPlayer = new ArrayList<>();
	Skill plugin;
	boolean isActivating = false;
	boolean isUsing = false;
	int click = 0;	
	Entity target;
	Player p;
	boolean enhanced = false;

	public Blink(Skill plugin) {
		super(plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerActivateBlink(PlayerInteractEvent e) {
		if (checkRequirement(e, "Blink", "Blink")) {
			this.p = e.getPlayer();
		}
	}
	
	@EventHandler
	public void snowballHitEvent(ProjectileHitEvent e) {
		if (!(e.getEntity() instanceof Snowball)) return;
		if (e.getHitEntity() != null) {
			LivingEntity le = (LivingEntity) e.getHitEntity();
			Snowball s = (Snowball) e.getEntity();
			if (s.getCustomName().equals("a")) {
				s.remove();
				target = le;
			}
		}
	}
	
	@EventHandler
	public void snowballHitEvent(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Snowball) {
			if (!(e.getDamager() instanceof Snowball)) return;
			Snowball s = (Snowball) e.getDamager();
			if (s.getCustomName().equals("a")) {
				e.setCancelled(true);
				target = e.getEntity();
			}
		}
	}
	
	@EventHandler
	public void playerAttackEvent(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (p.equals(this.p) && enhanced) {
				((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 20 *2));
			}
		}
	}
	
	public void chooseTargetRightClick(Player p) {
		Snowball s = p.launchProjectile(Snowball.class);
		s.setVelocity(s.getVelocity().multiply(100));
		s.setCustomName("a");
		s.setCustomNameVisible(false);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!s.isDead()) s.remove();
			}
		}.runTaskLater(plugin, 3);
	}
	
	public boolean isPlayerNearby(Player p) {
		for (Entity e : p.getNearbyEntities(10, 10, 10)) {
			if (e instanceof Player) return true;
		}
		return false;
	}
	
	public Player chooseTargetNearby(Player p) {
		if (!isPlayerNearby(p)) return null;
		for (Entity e : p.getNearbyEntities(10, 10, 10)) {
			if (e instanceof Player) {
				Player player = (Player) e;
				randomPlayer.add(player.getUniqueId().toString());
			}			
		}
		if (randomPlayer == null) return null;
		Random ran = new Random();
		Player chosenOne = Bukkit.getPlayer(UUID.fromString(randomPlayer.get(ran.nextInt(randomPlayer.size()))));
		return chosenOne;
	}
	
	public void blink(Player p, String skill, int cooldown) {
		if (checkHunger(p, 6)) return;
		if (target == null && chooseTargetNearby(p) == null)  {
			p.sendMessage(ChatColor.RED + "No nearby target to blink");
			return;
		}
		Cooldown.setCooldown(p, "Blink", 8);
		if (target == null) target = chooseTargetNearby(p);
		p.sendMessage(ChatColor.DARK_PURPLE + "Blinked!");
		p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 5, 3);
		p.teleport(target.getLocation());
		p.setVelocity(target.getLocation().getDirection().multiply(-0.25));
		p.getLocation().setDirection(target.getLocation().getDirection());
		if (target instanceof Player) {
			Player player = (Player) target;
			player.hidePlayer(plugin, p);
			new BukkitRunnable() {
				@Override
				public void run() {
					player.showPlayer(plugin, p);
					target = null;
				}
			}.runTaskLater(plugin, 40);
		} else target = null;
	}
}
