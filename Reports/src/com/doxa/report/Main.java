package com.doxa.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
	
	//REPORT PLUGIN 1.0
	//CREATED BY NICK DOXA
    
    public boolean useDatabase = this.getConfig().getBoolean("use-database");
	
    public SQL sql;
    public GUI gui;
    
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.sql = new SQL(this);
		this.gui = new GUI(this);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(gui, this);
		try {
		if (!useDatabase) {
			reportFile.createFile();
		} else {
			try {
				sql.connect();
				sql.createTable();
			} catch (SQLException e) {
				System.out.print("Could not connect to MySQL Database!\n");
			}
		}
		} catch (NullPointerException e) {
		System.out.print("THIS SHIT BROKE");
		}
		gui.createInventory();
	}
	
	@Override
	public void onDisable() {
		System.out.print("Plugin disengaging!\n");
		if (useDatabase) {
			sql.disconnect();
		}
		saveDefaultConfig();
	}
	
	FileClass reportFile = new FileClass();
	
	public String prefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix")) + " ";
	
	private List<Player> waiting = new ArrayList<>();
	
	private String reason;
	
	private Player reported_player;
	
	private String adminperm = "reports.admin";
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (waiting.contains(player)) {
			reason = event.getMessage();
			if (!useDatabase) {
				reportFile.writeReport(player, reported_player, reason, prefix);
			} else {
				sql.createPlayerReport(reported_player, player, reason, prefix);
			}
			alertAdmins(player);
			waiting.remove(player);
			event.setCancelled(true);
		} else {
			return;
		}
	}
	
	private boolean usealert;
	
	public void alertAdmins(Player sender) {
		usealert = getConfig().getBoolean("GUI.alert-admins");
		for (Player admin : Bukkit.getOnlinePlayers()) {
			if (admin.hasPermission(adminperm)) {
				if (usealert) {
					admin.playSound(admin.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
					admin.sendMessage(prefix + ChatColor.DARK_RED + reported_player.getName() + 
							ChatColor.RED + " was reported for " + reason + " by " + ChatColor.DARK_RED + 
							sender.getName());
				}
			}
		}
	}
	
	public void getReason(Player p, String p2) {
		try {
			Player reported = Bukkit.getPlayer(p2);
			p.sendMessage("");
			p.sendMessage(prefix + ChatColor.RED + "What is your reason for reporting " + ChatColor.DARK_RED + reported.getName());
			p.sendMessage("");
			waiting.add(p);
			reported_player = reported;
		} catch (NullPointerException e) {
			p.sendMessage(ChatColor.RED + "That player is not online!");
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (sender instanceof Player) {
			if (label.equalsIgnoreCase("report") && args.length < 1 || args.length > 1) {
				player.sendMessage(prefix + ChatColor.RED + "Incorrect arguments. Correct usage: /report <player>");
			} else if (label.equalsIgnoreCase("report") && args.length == 1
					&& !args[0].equalsIgnoreCase("help") && !args[0].equalsIgnoreCase("list")
					&& !args[0].equalsIgnoreCase("clear") && !args[0].equalsIgnoreCase("config")) {
				getReason(player, args[0]);
			} else if (label.equalsIgnoreCase("report") && args[0].equalsIgnoreCase("list")) {
				if (player.hasPermission(adminperm)) {
					if (useDatabase) {
						sql.readTable(player);
					} else {
						try {
							reportFile.scanFile(player);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					player.sendMessage(prefix + ChatColor.RED + "Insufficient Permissions!");
				}
			} else if (label.equalsIgnoreCase("report") && args[0].equalsIgnoreCase("help")) {
				player.sendMessage("");
				player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Reports");
				player.sendMessage("");
				player.sendMessage(ChatColor.YELLOW + "Main Command: /report");
				player.sendMessage(ChatColor.YELLOW + "Usage: /report <player>");
				if (player.hasPermission(adminperm)) {
					player.sendMessage("");
					player.sendMessage(ChatColor.RED + "Admin Commands");
					player.sendMessage("");
					player.sendMessage(ChatColor.YELLOW + "To clear report list: /report clear");
					player.sendMessage(ChatColor.YELLOW + "To view active reports: /report list");
					player.sendMessage(ChatColor.YELLOW + "To change config settings: /report config");
				}
			} else if (label.equalsIgnoreCase("report") && args[0].equalsIgnoreCase("clear")) {
				if (player.hasPermission(adminperm)) {
					if (useDatabase) {
						sql.clearTable();
					} else {
						reportFile.clearFile();
					}
					player.sendMessage(prefix + ChatColor.GOLD + "Reports list cleared!");
				} else {
					player.sendMessage(prefix + ChatColor.RED + "Insufficient Permissions!");
				}
			} else if (label.equalsIgnoreCase("report") && args[0].equalsIgnoreCase("config")) {
				gui.openGUI(player);
			}
		}
		return false;
	}
	
}
