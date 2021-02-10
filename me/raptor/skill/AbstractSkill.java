package me.raptor.skill;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractSkill implements Listener{
	Skill plugin;
	int click = 0;
	boolean isActivating = false;
	List<LivingEntity> army = new ArrayList<>();
	NamespacedKey key;
	
	public AbstractSkill(Skill plugin) {
		this.plugin = plugin;
		key = new NamespacedKey(plugin, "skill");
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	public boolean checkRequirement(PlayerInteractEvent e, String skill, String disSkill) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK 
				|| e.getAction() == Action.RIGHT_CLICK_AIR) {
				EquipmentSlot hand = e.getHand();
				if (!hand.equals(EquipmentSlot.HAND)) return false;
				Player p = e.getPlayer();
				if (p.isSneaking()) {
					isActivating = true;
					if (checkSafemode(p)) return false;
					if (isActivating) click++;
					if (!isEquipping(skill, p)) return false;
					if (click == 3) {
						click = 0;
						if (Cooldown.isOnCooldown(p, skill)) {
							p.sendMessage(ChatColor.RED + "You can use " + disSkill + " after " + Cooldown.getTimeLeft(p, skill));
							return false;
						}
						return true;
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
		return false;
	}
	
	public boolean isEquipping(String skill, Player p) {
		if (plugin.getConfig().get(p.getName() + ".Equipping").equals(skill)) return true;
		else return false;		
	}
	
	public boolean checkSafemode(Player p) {
		return plugin.getConfig().getBoolean(p.getName() + ".Safemode");
	}
	
	public boolean checkHunger(Player p, int hunger) {
		if (p.getFoodLevel() <= hunger) {
			p.sendMessage(ChatColor.YELLOW + "Hunger too low!");
			return false;
		}
		return true;
	}
}
