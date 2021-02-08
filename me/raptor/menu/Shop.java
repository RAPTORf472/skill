package me.raptor.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.raptor.main.Cooldown;
import me.raptor.main.Skill;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class Shop extends Thread implements Listener{
	
	Economy econ = Skill.getEconomy();
	Plugin plugin;
	FileConfiguration config;
	Player p;
	Inventory inv, iair, iwater, ifire, iearth, choice;
	ItemStack blink, dash, swim, army, advance, blast, palm, shield, demon, air, fire, water, earth, yes, no, orange, brown, white, aqua, black;
	List<String> blinkdesc = Arrays.asList(ChatColor.WHITE + "Teleport behind your opponent",
											ChatColor.WHITE + "Your next attack will slow the opponent",
											ChatColor.WHITE + "for 2 seconds",
											ChatColor.BOLD + "" + ChatColor.GOLD + "Damage: 0",
											ChatColor.BOLD + "" + ChatColor.GREEN + "Hunger: 2",
											ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Cooldown: 8s",
											ChatColor.BOLD + "" + ChatColor.YELLOW + "Cost: 6000$");
	List<String> dashdesc = Arrays.asList(ChatColor.WHITE + "Dash with an increadible speed",
											ChatColor.WHITE + "Create a small explosion when stop",
											ChatColor.BOLD + "" + ChatColor.GOLD + "Damage: 3",
											ChatColor.BOLD + "" + ChatColor.GREEN + "Hunger: 2",
											ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Cooldown: 10s",
											ChatColor.BOLD + "" + ChatColor.YELLOW + "Cost: 5000$");
	List<String> swimdesc = Arrays.asList(ChatColor.BLUE + "Unleash a hall of punches to keep",
											ChatColor.BLUE + "some distances with your opponent",
											ChatColor.BLUE + "while damaging them",
											ChatColor.BOLD + "" + ChatColor.GOLD + "Damage: 1/punch/300 punches",
											ChatColor.BOLD + "" + ChatColor.GREEN + "Hunger: 0",
											ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Cooldown: 40s",
											ChatColor.BOLD + "" + ChatColor.YELLOW + "Cost: 10000$");
	List<String> armydesc = Arrays.asList(ChatColor.BLUE + "Summon a horde of undeads to fight for you",
											ChatColor.BLUE + "" + ChatColor.GREEN + "that will vanish after 10 seconds",
											ChatColor.BOLD + "" + ChatColor.GOLD + "Damage: 2 - 4",
											ChatColor.BOLD + "" + ChatColor.GREEN + "Hunger: 0",
											ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Cooldown: 60s",
											ChatColor.BOLD + "" + ChatColor.YELLOW + "Cost: 11000$");
	List<String> advancedesc = Arrays.asList(ChatColor.RED + "Drastically increase heartbeats rate",
											ChatColor.RED + "which will enhance your damage (x3), speed, jump",
											ChatColor.RED + "but will drain your food bar quickly",
											ChatColor.RED + "and x4 the intake damage",
											ChatColor.BOLD + "" + ChatColor.GOLD + "Damage: x3",
											ChatColor.BOLD + "" + ChatColor.GREEN + "Hunger: 2/s",
											ChatColor.BOLD + "" + ChatColor.GREEN + "Self Damaged: 1/s",
											ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Cooldown: 70s",
											ChatColor.BOLD + "" + ChatColor.YELLOW + "Cost: 13000$");
	List<String> blastdesc = Arrays.asList(ChatColor.RED + "Create a blast to damage and set",
											ChatColor.RED + "nearby entities on fire for 3s",
											ChatColor.BOLD + "" + ChatColor.GOLD + "Damage: 17",
											ChatColor.BOLD + "" + ChatColor.GREEN + "Self Damage: 3",
											ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Cooldown: 70s",
											ChatColor.BOLD + "" + ChatColor.YELLOW + "Cost: 13000$");
	List<String> shielddesc = Arrays.asList(ChatColor.GOLD + "The power of the earth will reduce",
											ChatColor.GOLD + "80% of intake damage for 5 second",
											ChatColor.GOLD + "attack you enemy during that time will",
											ChatColor.GOLD + "x2 the damage of your attack",
											ChatColor.BOLD + "" + ChatColor.GOLD + "Damage: x2",
											ChatColor.BOLD + "" + ChatColor.GREEN + "Hunger: 0",
											ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Cooldown: 40s",
											ChatColor.BOLD + "" + ChatColor.YELLOW + "Cost:9000$");
	List<String> palmdesc = Arrays.asList(ChatColor.GOLD + "Strike your opponents, dealing 8 damage",
											ChatColor.GOLD + "to them and confuse, blind, slow them",
											ChatColor.GOLD + "for 5 seconds",
											ChatColor.BOLD + "" + ChatColor.GOLD + "Damage: 8 (Player), 12 (Mobs)",
											ChatColor.BOLD + "" + ChatColor.GREEN + "Hunger: 0",
											ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Cooldown: 40s",
											ChatColor.BOLD + "" + ChatColor.YELLOW + "Cost: 8000$");
	List<String> airdesc = Arrays.asList(ChatColor.WHITE + "Movement skill, cost 2 hunger per use",
											ChatColor.WHITE + "Very short cooldown");
	List<String> waterdesc = Arrays.asList(ChatColor.BLUE + "Consistent flow of damage",
											ChatColor.BLUE + "Average cooldown");
	List<String> firedesc = Arrays.asList(ChatColor.RED + "Huge damage, but comes with huge drawbacks",
											ChatColor.RED + "Drain your hunger bar quickly",
											ChatColor.RED + "Very long cooldown");
	List<String> earthdesc = Arrays.asList(ChatColor.GOLD + "Melee attacks, average damage",
											ChatColor.GOLD + "Average cooldown");
	List<String> demondesc = Arrays.asList(ChatColor.RED + "The ultimate attack, the trump card of the fight",
											ChatColor.RED + "Redirect all of the attack's damage and add some of",
											ChatColor.RED + "your own to create a knock out blow",
											ChatColor.RED + "Require level 40 to be used",
											ChatColor.GOLD + "Coming soon!");
	
	String name;
	public Shop(Plugin plugin) {
		this.plugin = plugin;
		config = plugin.getConfig();
		choice = Bukkit.getServer().createInventory(new ChoiceGUI(), 27, ChatColor.RED + "Confirm");
		inv = Bukkit.getServer().createInventory(new ElementGUI(), 45, ChatColor.RED + "Elements");
		iair = Bukkit.getServer().createInventory(new AirGUI(), 27, ChatColor.WHITE + "" + ChatColor.BOLD  + "AIR");
		ifire = Bukkit.getServer().createInventory(new FireGUI(), 27, ChatColor.RED + "" + ChatColor.BOLD  + "FIRE");
		iwater = Bukkit.getServer().createInventory(new WaterGUI(), 27, ChatColor.BLUE + "" + ChatColor.BOLD  + "WATER");
		iearth = Bukkit.getServer().createInventory(new EarthGUI(), 27, ChatColor.GOLD + "" + ChatColor.BOLD  + "EARTH");
		blink = createItem(ChatColor.BOLD + "Blink", Material.ENDER_EYE, blinkdesc);
		dash = createItem(ChatColor.BOLD + "Dash", Material.RABBIT_FOOT, dashdesc);	
		swim = createItem(ChatColor.BLUE +  "" + ChatColor.BOLD  + "Swimming Swallow", Material.SNOWBALL, swimdesc);
		army = createItem(ChatColor.BLUE +  "" + ChatColor.BOLD  + "Army Of Poiseidon", Material.TRIDENT, armydesc);	
		advance = createItem(ChatColor.RED +  "" + ChatColor.BOLD  + "Advance", Material.BLAZE_POWDER, advancedesc);
		blast = createItem(ChatColor.RED +  "" + ChatColor.BOLD  + "Blast Core", Material.TNT, blastdesc);	
		palm = createItem(ChatColor.GOLD +  "" + ChatColor.BOLD  + "Chi Blockage", Material.DIRT, palmdesc);
		shield = createItem(ChatColor.GOLD +  "" + ChatColor.BOLD  + "Unbreakable", Material.SHIELD, shielddesc);	
		air = createItem(ChatColor.GREEN +  "" + ChatColor.BOLD  + "AIR", Material.GLASS_BOTTLE, airdesc);	
		fire = createItem(ChatColor.GOLD +  "" + ChatColor.BOLD  + "FIRE", Material.BLAZE_POWDER, firedesc);
		earth = createItem(ChatColor.DARK_RED +  "" + ChatColor.BOLD  + "EARTH", Material.DIRT, earthdesc);	
		water = createItem(ChatColor.YELLOW +  "" + ChatColor.BOLD  + "WATER", Material.WATER_BUCKET, waterdesc);
		demon = createItem(ChatColor.DARK_PURPLE +  "" + ChatColor.BOLD  + "Demonsbane", Material.DRAGON_HEAD, demondesc);
		yes = createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Yes", Material.GREEN_STAINED_GLASS_PANE);
		no = createItem(ChatColor.RED + "" + ChatColor.BOLD + "No", Material.RED_STAINED_GLASS_PANE);
		orange = createItem(ChatColor.RED + "" + ChatColor.BOLD + "Back", Material.ORANGE_STAINED_GLASS_PANE);
		white = createItem(ChatColor.RED + "" + ChatColor.BOLD + "Back", Material.WHITE_STAINED_GLASS_PANE);
		aqua = createItem(ChatColor.RED + "" + ChatColor.BOLD + "Back", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
		brown = createItem(ChatColor.RED + "" + ChatColor.BOLD + "Back", Material.BROWN_STAINED_GLASS_PANE);
		black = createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Unequip Skill", Material.BLACK_STAINED_GLASS_PANE);
		inv.setItem(10, air);
		inv.setItem(12, water);
		inv.setItem(14, fire);
		inv.setItem(16, earth);
		inv.setItem(31, demon);
		iair.setItem(11, blink);
		iair.setItem(15, dash);
		iwater.setItem(11, swim);
		iwater.setItem(15, army);
		ifire.setItem(11, advance);
		ifire.setItem(15, blast);
		iearth.setItem(11, palm);
		iearth.setItem(15, shield);
		choice.setItem(11, yes);
		choice.setItem(15, no);
		for (int i = 0; i < 45; i++) {
			if (inv.getItem(i) != null) continue;
			inv.setItem(i, black);
		}
		for (int i = 0; i < 27; i++) {
			if (iair.getItem(i) != null) continue;
			iair.setItem(i, white);
		}
		for (int i = 0; i < 27; i++) {
			if (iwater.getItem(i) != null) continue;
			iwater.setItem(i, aqua);
		}
		for (int i = 0; i < 27; i++) {
			if (ifire.getItem(i) != null) continue;
			ifire.setItem(i, orange);
		}
		for (int i = 0; i < 27; i++) {
			if (iearth.getItem(i) != null) continue;
			iearth.setItem(i, brown);
		}
		for (int i = 0; i < 27; i++) {
			if (choice.getItem(i) != null) continue;
			choice.setItem(i, new ItemStack(Material.GREEN_STAINED_GLASS_PANE));
		}
	    Bukkit.getServer().getPluginManager().registerEvents(this, plugin);	
	}
	private ItemStack createItem(String name, Material mat, List<String> lore) {
		ItemStack i = new ItemStack(mat);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		i.setItemMeta(meta);
		return i;
	}
	
	private ItemStack createItem(String name, Material mat) {
		ItemStack i = new ItemStack(mat);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(name);
		i.setItemMeta(meta);
		return i;
	}
	
	public Inventory getDefaultInventory() {
		return inv;
	}
	
	@EventHandler
	public void onMenuInteract(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof ElementGUI) {
			p = (Player) e.getWhoClicked();
			e.setCancelled(true);
			if (e.getCurrentItem().equals(black)) {
				if (plugin.getConfig().getString(p.getName() + ".Equipping").equals("")) {
					p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You are not equipping any skill!");
					return;
				}
				String skill = plugin.getConfig().getString(p.getName() + ".Equipping");
				plugin.getConfig().set(p.getName() + ".Equipping", "");
				plugin.saveConfig();
				p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Unequipped " + skill);
				e.setCancelled(true);
				return;
			}
			if (e.getCurrentItem().equals(air)) {
				p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 5, 3);
				p.closeInventory();
				e.setCancelled(true);
				p.openInventory(iair);

				return;
			}
			if (e.getCurrentItem().equals(water)) {
				p.playSound(p.getLocation(), Sound.BLOCK_WATER_AMBIENT, 5, 3);
				p.closeInventory();
				e.setCancelled(true);
				p.openInventory(iwater);

				return;
			}
			if (e.getCurrentItem().equals(fire)) {
				p.playSound(p.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 5, 3);
				p.closeInventory();
				p.openInventory(ifire);
				return;
			}
			if (e.getCurrentItem().equals(earth)) {
				p.playSound(p.getLocation(), Sound.BLOCK_CROP_BREAK, 5, 3);
				p.closeInventory();
				p.openInventory(iearth);
				return;
			}
		}
	} 
	
	
	@EventHandler
	public void onAirMenuInteract(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof AirGUI) {
			e.setCancelled(true);
			if (e.getCurrentItem().equals(white)) {
				p.closeInventory();
				p.openInventory(inv);
				e.setCancelled(true);
				return;
			}
			Player p = (Player) e.getWhoClicked();
			if (!plugin.getConfig().contains(p.getName(), true)) initConfig(p);
			if (e.getCurrentItem().equals(dash)) {
				p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 5, 3);
				if (haveSkill(p, "Dash")) {
					equipSkill(p, "Dash");
					p.sendMessage(ChatColor.GOLD + "Equipped Dash");
					return;
				} else {
					if (checkPrice(p, 5000)) {
						buySkill(p, "Dash");
						withdrawPlayer(p, 5000);
						p.sendMessage(ChatColor.GOLD + "Learned Dash");
						if (checkSkill(p).equals("")) equipSkill(p, "Dash");
						return;
					} else {
						p.sendMessage(ChatColor.RED + "You don't have enough money to buy this skill!");
					};
				}
				return;
			}
			if (e.getCurrentItem().equals(blink)) {
				p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 5, 3);
				if (haveSkill(p, "Blink")) {
					equipSkill(p, "Blink");
					p.sendMessage(ChatColor.GOLD + "Equipped Blink");
					return;
				} else {
					if (checkPrice(p, 6000)) {
						buySkill(p, "Blink");
						withdrawPlayer(p, 6000);
						p.sendMessage(ChatColor.GOLD + "Learned Blink");
						if (checkSkill(p).equals("")) equipSkill(p, "Blink");
						return;
					} else {
						p.sendMessage(ChatColor.RED + "You don't have enough money to buy this skill!");
					};
				}
				return;
			}
		}
	} 

	@EventHandler
	public void onFireMenuInteract(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof FireGUI) {
			e.setCancelled(true);
			if (e.getCurrentItem().equals(orange)) {
				p.closeInventory();
				p.openInventory(inv);
				e.setCancelled(true);
				return;
			}
			Player p = (Player) e.getWhoClicked();
			if (e.getCurrentItem().equals(advance)) {
				p.playSound(p.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 5, 3);
				if (haveSkill(p, "Advance")) {
					equipSkill(p, "Advance");
					p.sendMessage(ChatColor.GOLD + "Equipped Advance");
					return;
				} else {
					if (checkPrice(p, 13000)) {
						buySkill(p, "Advance");
						withdrawPlayer(p, 13000);
						p.sendMessage(ChatColor.GOLD + "Learned Advance");
						if (checkSkill(p).equals("")) equipSkill(p, "Advance");
						return;
					} else {
						p.sendMessage(ChatColor.RED + "You don't have enough money to buy this skill!");
					};
				}
			}
			if (e.getCurrentItem().equals(blast)) {
				p.playSound(p.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 5, 3);
				if (haveSkill(p, "BlastCore")) {
					equipSkill(p, "BlastCore");
					p.sendMessage(ChatColor.GOLD + "Equipped Blast Core");
					return;
				} else {
					if (checkPrice(p, 13000)) {
						buySkill(p, "BlastCore");
						withdrawPlayer(p, 13000);
						p.sendMessage(ChatColor.GOLD + "Learned Blast Core");
						if (checkSkill(p).equals("")) equipSkill(p, "BlastCore");
						return;
					} else {
						p.sendMessage(ChatColor.RED + "You don't have enough money to buy this skill!");
					};
				}
			}
			return;
		}
	} 
	
	@EventHandler
	public void onWaterMenuInteract(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof WaterGUI) {
			e.setCancelled(true);
			if (e.getCurrentItem().equals(aqua)) {
				p.closeInventory();
				p.openInventory(inv);
				e.setCancelled(true);
				return;
			}
			Player p = (Player) e.getWhoClicked();
			if (e.getCurrentItem().equals(swim)) {
				p.playSound(p.getLocation(), Sound.BLOCK_WATER_AMBIENT, 5, 3);
				if (haveSkill(p, "SwimmingSwallow")) {
					equipSkill(p, "SwimmingSwallow");
					p.sendMessage(ChatColor.GOLD + "Equipped Swimming Swallow");
					return;
				} else {
					if (checkPrice(p, 10000)) {
						withdrawPlayer(p, 10000);
						buySkill(p, "SwimmingSwallow");
						p.sendMessage(ChatColor.GOLD + "Learned Swimming Swallow");
						if (checkSkill(p).equals("")) equipSkill(p, "SwimmingSwallow");
						return;
					} else {
						p.sendMessage(ChatColor.RED + "You don't have enough money to buy this skill!");
					};
				}
				return;
			}
		}
		if (e.getInventory().getHolder() instanceof WaterGUI) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			if (e.getCurrentItem().equals(army)) {
				p.playSound(p.getLocation(), Sound.BLOCK_WATER_AMBIENT, 5, 3);
				if (haveSkill(p, "ArmyOfPoiseidon")) {
					equipSkill(p, "ArmyOfPoiseidon");
					p.sendMessage(ChatColor.GOLD + "Equipped Army Of Poiseidon");
					return;
				} else {
					if (checkPrice(p, 11000)) {
						withdrawPlayer(p, 11000);
						buySkill(p, "ArmyOfPoiseidon");
						p.sendMessage(ChatColor.GOLD + "Learned Army Of Poiseidon");
						if (checkSkill(p).equals("")) equipSkill(p, "ArmyOfPoiseidon");
						return;
					} else {
						p.sendMessage(ChatColor.RED + "You don't have enough money to buy this skill!");
					};
				}
				return;
			}
		}
	} 
	
	@EventHandler
	public void onEarthMenuInteract(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof EarthGUI) {
			e.setCancelled(true);
			if (e.getCurrentItem().equals(brown)) {
				p.closeInventory();
				p.openInventory(inv);
				e.setCancelled(true);
				return;
			}
			Player p = (Player) e.getWhoClicked();
			if (e.getCurrentItem().equals(palm)) {
				p.playSound(p.getLocation(), Sound.BLOCK_CROP_BREAK, 5, 3);
				if (haveSkill(p, "ChiBlockage")) {
					equipSkill(p, "ChiBlockage");
					p.sendMessage(ChatColor.GOLD + "Equipped Chi Blockage");
					return;
				} else {
					if (checkPrice(p, 8000)) {
						withdrawPlayer(p, 8000);
						buySkill(p, "ChiBlockage");
						p.sendMessage(ChatColor.GOLD + "Learned Chi Blockage");
						if (checkSkill(p).equals("")) equipSkill(p, "ChiBlockage");
						return;
					} else {
						p.sendMessage(ChatColor.RED + "You don't have enough money to buy this skill!");
					};
				}
				return;
			}
		}
		if (e.getInventory().getHolder() instanceof EarthGUI) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			if (e.getCurrentItem().equals(shield)) {
				p.playSound(p.getLocation(), Sound.BLOCK_CROP_BREAK, 5, 3);
				if (haveSkill(p, "Unbreakable")) {
					equipSkill(p, "Unbreakable");
					p.sendMessage(ChatColor.GOLD + "Equipped Unbreakable");
					return;
				} else {
					if (checkPrice(p, 9000)) {
						buySkill(p, "Unbreakable");
						withdrawPlayer(p, 9000);
						p.sendMessage(ChatColor.GOLD + "Learned Unbreakable");
						if (checkSkill(p).equals("")) equipSkill(p, "Unbreakable");
						return;
					} else {
						p.sendMessage(ChatColor.RED + "You don't have enough money to buy this skill!");
					};
				}
				return;
			}
		}
	} 
	

	
	public void buySkill(Player p, String skill) {
		String name = p.getName();
		String path = name + '.' + skill;
		if (!plugin.getConfig().getBoolean(path)) {
			plugin.getConfig().set(path, true);
			plugin.saveConfig();
		}
	}
	
	public boolean haveSkill(Player p, String skill) {
		String name = p.getName();
		String path = name + '.' + skill;
		if (!plugin.getConfig().getBoolean(path)) return false;
		else return true;
		
	}
	
	public void equipSkill(Player p, String skill) {
		String name = p.getName();
		if (Cooldown.isOnCooldown(p, "Blink")
			|| Cooldown.isOnCooldown(p, "Dash")
			|| Cooldown.isOnCooldown(p, "SwimmingSwallow")
			|| Cooldown.isOnCooldown(p, "ArmyOfPoiseidon")
			|| Cooldown.isOnCooldown(p, "Advance")
			|| Cooldown.isOnCooldown(p, "BlastCore")
			|| Cooldown.isOnCooldown(p, "Unbreakable")
			|| Cooldown.isOnCooldown(p, "ChiBlockage")
			|| Cooldown.isOnCooldown(p, "Demonsbane")) {
				p.sendMessage(ChatColor.RED + "You can't change skill right now because you are still on cooldown");
				return;
			}
		plugin.getConfig().set(name + "." + "Equipping", skill);
		plugin.saveConfig();
	}
	
	public String checkSkill(Player p) {
		String name = p.getName();
		return plugin.getConfig().getString(name + "." + "Equipping");
	}
	
	public boolean checkPrice(Player p, int price) {
		if (econ.getBalance(p) >= price) return true;
		else return false;
	}
	
	public void withdrawPlayer(Player p, int amount) {
		econ.withdrawPlayer(p, amount);
	}
	
	public void initConfig(Player p) {
		String name = p.getName() + ".";
		plugin.getConfig().set(name + "Equipping", "");
		plugin.getConfig().set(name + "Blink", false);
		plugin.getConfig().set(name + "Dash", false);
		plugin.getConfig().set(name + "SwimmingSwallow", false);
		plugin.getConfig().set(name + "ArmyOfPoiseidon", false);
		plugin.getConfig().set(name + "Advance", false);
		plugin.getConfig().set(name + "BlastCore", false);
		plugin.getConfig().set(name + "Unbreakable", false);
		plugin.getConfig().set(name + "ChiBlockage", false);
		plugin.saveConfig();
	}
}

