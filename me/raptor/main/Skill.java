package me.raptor.main;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.raptor.menu.Shop;
import me.raptor.skill.Advance;
import me.raptor.skill.BlastCore;
import me.raptor.skill.Blink;
import me.raptor.skill.ChiBlockage;
import me.raptor.skill.Dash;
import me.raptor.skill.PoiseidonsArmy;
import me.raptor.skill.SwimmingSwallow;
import me.raptor.skill.Unbreakable;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Skill extends JavaPlugin implements Listener{
	
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static Permission perms = null;
    Shop shop;
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
		ColorLogging.logging("&cSkill plugin has been enabled");
		shop = new Shop(this);
		new Blink(this);
		new Dash(this);
		new SwimmingSwallow(this);
		new PoiseidonsArmy(this);
		new Advance(this);
		new BlastCore(this);
		new ChiBlockage(this);
		new Unbreakable(this);
	}
	
	@Override
	public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
		ColorLogging.logging("&cSkill plugin has been disabled");
	}
	
	public boolean onCommand(CommandSender sd, Command cmd, String CommandLabel, String[] args) {
		if (!(sd instanceof Player)) {
			sd.sendMessage("Player Only");
		} else {
			Player p = (Player) sd;
			if (cmd.getName().equalsIgnoreCase("menu")) {
				p.openInventory(shop.getDefaultInventory());
				return false;
			}
			if (cmd.getName().equalsIgnoreCase("skill")) {
				String skill = getConfig().getString(p.getName() + ".Equipping");
				if (skill.equals("")) {
					p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You are not equipping any skill!");
					return false;
				}
				p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "You are using " + skill);
				return false;
			}
			if (cmd.getName().equalsIgnoreCase("safemode")) {
				if (getConfig().getBoolean(p.getName() + ".Safemode")) {
					getConfig().set(p.getName() + ".Safemode", false);
					p.sendMessage(ChatColor.RED + "Skill safemode disabled");
					return false;
				} else {
					getConfig().set(p.getName() + ".Safemode", true);
					p.sendMessage(ChatColor.YELLOW + "Skill safemode enabled");
					return false;
				}
			}
		}
		return false;
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	public static Economy getEconomy() {
        return econ;
    }
    
    public static Permission getPermissions() {
        return perms;
    }
    
    @EventHandler
    public void onPlayerLogIn(PlayerJoinEvent e) {
    	Player p = e.getPlayer();
    	if (!getConfig().contains(p.getName(), true)) initConfig(p);
    }
    
    
    public void initConfig(Player p) {
		String name = p.getName() + ".";
		getConfig().set(name + "Equipping", "");
		getConfig().set(name + "Safemode", false);
		getConfig().set(name + "Blink", false);
		getConfig().set(name + "Dash", false);
		getConfig().set(name + "SwimmingSwallow", false);
		getConfig().set(name + "ArmyOfPoiseidon", false);
		getConfig().set(name + "Advance", false);
		getConfig().set(name + "BlastCore", false);
		getConfig().set(name + "Unbreakable", false);
		getConfig().set(name + "ChiBlockage", false);
		saveConfig();
	}
}
