package me.raptor.skill;

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

public class ChiBlockage extends AbstractSkill implements Listener{
	
	Skill plugin;
	boolean isActivating = false;
	int click = 0;	
	
	public ChiBlockage(Skill plugin) {
		super(plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerActivateAbility(PlayerInteractAtEntityEvent e) {
		if (checkRequirement(e)) {
			Player p = e.getPlayer();
			Cooldown.setCooldown(p, "ChiBlockage", 40f);
			if (e.getRightClicked() instanceof Player) palmStrikePlayer(p, (Player) e.getRightClicked());
			else palmStrikeEntity((LivingEntity) e.getRightClicked(), p);
		}
	
	}
	public void palmStrikePlayer(Player p, Player target) {
		p.setFoodLevel(p.getFoodLevel() - 2);
		target.damage(9);
		target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 2));
		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
	}
	
	public void palmStrikeEntity(LivingEntity e, Player p) {
		p.setFoodLevel(p.getFoodLevel() - 2);
		e.damage(12);
	}
	
	public boolean checkRequirement(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		if (!(e.getRightClicked() instanceof LivingEntity)) return false;
		isActivating = true;
		if (isActivating) click++;
		if (!isEquipping("ChiBlockage", p)) return false;
		if (checkSafemode(p)) return false;
		if (click == 3) {
			click = 0;
			if (Cooldown.isOnCooldown(p, "ChiBlockage")) {
				p.sendMessage(ChatColor.RED + "You can use Chi Blockage after " + Cooldown.getTimeLeft(p, "ChiBlockage"));
				return false;
			}	
			if (checkHunger(p, 6)) return false;
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
		return false;
	}
}
