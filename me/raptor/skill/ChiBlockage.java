package me.raptor.skill;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class ChiBlockage implements Listener{
	
	Skill plugin;
	boolean isActivating = false;
	int click = 0;	
	int hunger;
	Player p;
	
	public ChiBlockage(Skill plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerActivateAbility(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		this.p = p;
			isActivating = true;
			if (isActivating) click++;
			if (!isEquipping("ChiBlockage")) return;
			if (checkSafemode(p)) return;
			if (click == 3) {
				click = 0;
				if (Cooldown.isOnCooldown(p, "ChiBlockage")) {
					p.sendMessage(ChatColor.RED + "You can use Chi Blockage after " + Cooldown.getTimeLeft(p, "ChiBlockage"));
					return;
				}
				Cooldown.setCooldown(p, "ChiBlockage", 40f);	
				hunger = p.getFoodLevel();
				if (hunger <= 6) {
					p.sendMessage(ChatColor.YELLOW + "Hunger too low!");
					return;
				}
				hunger -= 2;
				if (e.getRightClicked() instanceof Player) palmStrikePlayer((Player) e.getRightClicked());
				else palmStrikeEntity((LivingEntity) e.getRightClicked());
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
	
	public void palmStrikePlayer(Player p) {
		p.damage(6);
		p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 2));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
	}
	
	public void palmStrikeEntity(LivingEntity e) {
		e.damage(12);
	}
	
	public boolean isEquipping(String skill) {
		if (plugin.getConfig().get(p.getName() + ".Equipping").equals(skill)) return true;
		else return false;		
	}
	
	public boolean checkSafemode(Player p) {
		return plugin.getConfig().getBoolean(p.getName() + ".Safemode");
	}
}
