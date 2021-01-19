package com.doxa.report;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GUI implements Listener {

	Main plugin;
	public GUI(Main main) {
		this.plugin = main;
	}

	private boolean usealerts;
	
	public static Inventory inv;
	
	public void createInventory() {
		usealerts = plugin.getConfig().getBoolean("GUI.alert-admins");
		Inventory i = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Reports Config");
		ItemStack item = new ItemStack(Material.EMERALD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "Admin Alerts?");
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Click Me To Toggle!");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		i.setItem(4, item);
		
		inv = i;
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(!event.getView().getTitle().contains(ChatColor.GOLD + "Reports Config")) 
			return;
		if (event.getCurrentItem() == null)
			return;
		if (event.getCurrentItem().getItemMeta() == null)
			return;
		
		Player player = (Player) event.getWhoClicked();
		event.setCancelled(true);
		if(event.getClickedInventory().getType() == InventoryType.PLAYER)
			return;
		usealerts = plugin.getConfig().getBoolean("GUI.alert-admins");
		if (event.getSlot() == 4) {
			if (!usealerts) {
				plugin.getConfig().set("GUI.alert-admins", true);
			} else {
				plugin.getConfig().set("GUI.alert-admins", false);
			}
			plugin.saveConfig();
			player.closeInventory();
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
					new TextComponent(ChatColor.GREEN + "Config Updated!"));
		}
	}
	
	public void openGUI(Player player) {
		usealerts = plugin.getConfig().getBoolean("GUI.alert-admins");
		ItemStack option = getInventory().getItem(4);
		if (usealerts) {
			option.setType(Material.EMERALD);
		} else {
			option.setType(Material.REDSTONE);
		}
		player.openInventory(inv);
	}
	
}
