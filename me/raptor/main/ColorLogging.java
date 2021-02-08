package me.raptor.main;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class ColorLogging {
public static void logging(String s) {
Bukkit.getConsoleSender().sendMessage(prefix() + color(s));
}
public static String prefix() {
    return color("&f&l[&c&lNinja&f&l]");
}
public static String color(String s) {
	return ChatColor.translateAlternateColorCodes('&', s);
}
public static String getHelpMessage() {
    StringBuilder builder = new StringBuilder();
    builder.append("\n");
    builder.append("&7&l----------------------&r &l&f[&l&cNinja&l&f] &r&7&l----------------------\n");
    builder.append("&7/&fninja help &c- Show the list of commands for this plugin.\n");
    builder.append("&7/&fninja gears &c- Show available ninja gears\n");
    builder.append("&7/&fninja abilities &c- See all ninja's abilities.\n");
    builder.append("&7&l-----------------------------------------------------\n");
   
   
        return ColorLogging.color(builder.toString());
  }
public static String getNinjaGear() {
    StringBuilder builder = new StringBuilder();
    builder.append("\n");
    builder.append("&7&l----------------------&r &l&f[&l&cNinja&l&f] &r&7&l----------------------\n");
    builder.append("&7/&cninja visor &f&l- Give you an &c&lIron Visor .\n");
    builder.append("&7/&cninja chestplate &f&l- Give you an &c&lArrowproof Chestplate\n");
    builder.append("&7/&cninja leggings&f&l - Give you a pair of &c&lFine Leggings.\n");
    builder.append("&7/&cninja sneakers&f&l - Give you a pair of &c&l'Sneak'ers.\n");
    builder.append("&7/&cninja katana&f&l - Give you a &c&lKeen Katana.\n");
    builder.append("&7/&cninja bow&f&l - Give you a &c&lSensei's Bow.\n");
    builder.append("&7/&cninja hook&f&l - Give you a &c&lSteel Hook.\n");
    builder.append("&7/&cninja set&f&l - Give you a ninja set.\n");
    builder.append("&7&l-----------------------------------------------------\n");
   
   
        return ColorLogging.color(builder.toString());
  }
}
