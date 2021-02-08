package me.raptor.skill;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class Unbreakable implements Listener{
	Player p;
	Skill plugin;
	boolean isActivating = false;
	boolean isUsing = false;
	LivingEntity le;
	int click = 0;
	
	public Unbreakable(Skill plugin) {
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
				if (!isEquipping("Unbreakable")) return;
				if (click == 3) {
					p.sendMessage(String.valueOf(click));
					click = 0;
					if (isUsing) return;
					if (Cooldown.isOnCooldown(p, "Unbreakable")) {
						p.sendMessage(ChatColor.RED + "You can use Unbreakable after " + Cooldown.getTimeLeft(p, "Unbreakable"));
						return;
					}
					Cooldown.setCooldown(p, "Unbreakable", 40f);
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Unbreakable!");
					unbreakable(p);
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
	public void PlayerUseIronBreaker(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof LivingEntity) {
			le = (LivingEntity) e.getDamager();
			if (isUsing && le.equals(p)) {
				e.setDamage(e.getDamage() * 2);
				isUsing = false;
				p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.GRAY + "Ironbreaker!");
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 5, 1);
				e.getEntity().getWorld().spawnParticle(Particle.CRIT, e.getEntity().getLocation(), 20);
			}
		}
		
		
	}
	
	public void unbreakable(Player p) {
		isUsing = true;
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 3));
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
	
	public boolean isEquipping(String skill) {
		if (plugin.getConfig().get(p.getName() + ".Equipping").equals(skill)) return true;
		else return false;		
	}
	
	public boolean checkSafemode(Player p) {
		return plugin.getConfig().getBoolean(p.getName() + ".Safemode");
	}
}
