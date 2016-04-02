package com.forgestorm.mgfw.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.util.ItemBuilder;

import net.md_5.bungee.api.ChatColor;

public class SpectatorMenu {
	
	private Inventory options;
	private ItemStack noSpeed, speed1, speed2, speed3, speed4, trackPlayers;
	
	public SpectatorMenu() {
		makeMenuItems();
		createMenu();
	}
	
	/**
	 * Lets make all the menu items for our spectator menu.
	 */
	private void makeMenuItems() {
		String noSpeedTitle = Messages.MENU_ITEM_SPECTATOR_NO_SPEED.toString();
		String speed1Title = Messages.MENU_ITEM_SPECTATOR_SPEED_1.toString();
		String speed2Title = Messages.MENU_ITEM_SPECTATOR_SPEED_2.toString();
		String speed3Title = Messages.MENU_ITEM_SPECTATOR_SPEED_3.toString();
		String speed4Title = Messages.MENU_ITEM_SPECTATOR_SPEED_4.toString();
		String trackPlayersTitle = Messages.MENU_ITEM_SPECTATOR_TRACK_PLAYERS.toString();
		
		noSpeed = new ItemBuilder(Material.MINECART).setTitle(noSpeedTitle).build();
		speed1 = new ItemBuilder(Material.HOPPER_MINECART).setTitle(speed1Title).build();
		speed2 = new ItemBuilder(Material.POWERED_MINECART).setTitle(speed2Title).build();
		speed3 = new ItemBuilder(Material.STORAGE_MINECART).setTitle(speed3Title).build();
		speed4 = new ItemBuilder(Material.EXPLOSIVE_MINECART).setTitle(speed4Title).build();
		trackPlayers = new ItemBuilder(Material.SKULL_ITEM).setTitle(trackPlayersTitle).build();
	}
	
	/**
	 * Now lets create the menu.
	 */
	private void createMenu() {
		String title = Messages.MENU_NAME_SPECTATOR.toString();
		options = Bukkit.createInventory(null, 9 * 4, title);
		
		//Row 1
		options.setItem(11, noSpeed);
		options.setItem(12, speed1);
		options.setItem(13, speed2);
		options.setItem(14, speed3);
		options.setItem(15, speed4);
		
		//Row 2
		options.setItem(22, trackPlayers);
		
	}
	
	/**
	 * This will open the menu.
	 * @param player The layer we will send the menu to.
	 */
	public void openMenu(Player player) {
		player.openInventory(options);
	}
	
	/**
	 * This is toggled when a player clicks a menu item.
	 * @param player The player who clicked a menu item.
	 * @param menuName The name of the menu the player has open.
	 * @param item The item that the player has clicked.
	 * @return Returns true if ItemStack was successful interact click.
	 */
	public boolean playerInteractMenu(Player player, String menuName, ItemStack item) {
		String optionsMenu = Messages.MENU_NAME_SPECTATOR.toString();
		
		Bukkit.broadcastMessage("Event: " + menuName + " ->? " + ChatColor.GREEN + optionsMenu); 
		
		//If the menu clicked, is the spectator menu, continue.
		if (menuName.equalsIgnoreCase(optionsMenu)) {
			
			if (item.getType().equals(noSpeed.getType())) {
				player.setFlySpeed(0.2f);
				player.sendMessage(ChatColor.GREEN + "You selected no speed.");
				return true;
			} else if (item.getType().equals(speed1.getType())) {
				player.setFlySpeed(0.4f);
				player.sendMessage(ChatColor.GREEN + "You selected speed 1.");
				return true;
			} else if (item.getType().equals(speed2.getType())) {
				player.setFlySpeed(0.5f);
				player.sendMessage(ChatColor.GREEN + "You selected speed 2.");
				return true;
			} else if (item.getType().equals(speed3.getType())) {
				player.setFlySpeed(0.6f);
				player.sendMessage(ChatColor.GREEN + "You selected speed 3.");
				return true;
			} else if (item.getType().equals(speed4.getType())) {
				player.setFlySpeed(1f);
				player.sendMessage(ChatColor.GREEN + "You selected speed 4.");
				
				return true;
			} else if (item.getType().equals(trackPlayers.getType())) {
				//TODO: Open player tracking menu.
				player.sendMessage(ChatColor.GREEN + "Opening the player tracking menu.");
				return true;
			}
		}
		return false;
	}
}
