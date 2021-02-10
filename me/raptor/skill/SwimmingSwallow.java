package me.raptor.skill;

import org.bukkit.Sound;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;

public class SwimmingSwallow extends AbstractSkill implements Listener {
	Skill plugin;
	boolean isActivating = false;
	int click = 0;
	int count = 0;
	
	public SwimmingSwallow(Skill plugin) {
		super(plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerActivateAbility(PlayerInteractEvent e) {
		if (checkRequirement(e, "SwimmingSwallow", "Swimming Swallow")) {
			punch(e.getPlayer(), "SwimmingSwallow", 40);
		}
	}
	
	public void punch(Player p, String skill, int cooldown) {
		Cooldown.setCooldown(p, skill, cooldown);
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
}
