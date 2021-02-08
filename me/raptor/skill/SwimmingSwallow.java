package me.raptor.skill;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;

public class SwimmingSwallow implements Listener {
	Player p;
	Skill plugin;
	boolean isActivating = false;
	int click = 0;
	int count = 0;
	
	public SwimmingSwallow(Skill plugin) {
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
				if (!isEquipping("SwimmingSwallow")) return;
				if (isActivating) click++;
				if (click == 3) {
					click = 0;
					if (Cooldown.isOnCooldown(p, "SwimmingSwallow")) {
						p.sendMessage(ChatColor.RED + "You can use Swimming Swallow after " + Cooldown.getTimeLeft(p, "SwimmingSwallow"));
						return;
					}
					Cooldown.setCooldown(p, "SwimmingSwallow", 40f);
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Rush!");
					punch(p);
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
	
	public void punch(Player p) {
		float accuracy = 0.85F;
          new BukkitRunnable() {
        	  @Override
        	  public void run() {
        		  for (int i = 0; i < 3; i++) {
        			  LlamaSpit snowball = p.launchProjectile(LlamaSpit.class);
        			  Vector v = p.getPlayer().getLocation().getDirection();
        			  v.add(new Vector(Math.random() * accuracy - (accuracy / 2) ,Math.random() * accuracy - (accuracy / 2),Math.random() * accuracy - (accuracy / 2)));
        			  snowball.setVelocity(v.multiply(2));
		              count++;
		              if (count == 300) {
		            	  count = 0;
		            	  cancel();
		              }
		              if (count % 4 == 0) p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 5, 3);
	              }
        	  }
          }.runTaskTimer(plugin, 0, 1);
	}
	public boolean isEquipping(String skill) {
		if (plugin.getConfig().get(p.getName() + ".Equipping").equals(skill)) return true;
		else return false;		
	}
	
	public boolean checkSafemode(Player p) {
		return plugin.getConfig().getBoolean(p.getName() + ".Safemode");
	}
}
