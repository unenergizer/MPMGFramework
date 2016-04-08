package com.forgestorm.mgfw.core.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.profiles.PlayerProfile;

import net.md_5.bungee.api.ChatColor;

public class PlayerTracker {

	private MGFramework PLUGIN;
	private final Player PLAYER;
	private Inventory tracker;
	private ArrayList<ItemStack> playerHeads;

	public PlayerTracker(MGFramework plugin, Player player) {
		PLUGIN = plugin;
		PLAYER = player;
		createMenu();
		playerHeads = new ArrayList<ItemStack>();
	}

	/**
	 * Lets make all the menu items for our spectator menu.
	 */
	private void makeMenuItems() {
		HashMap<Player, PlayerProfile> playerProfile = PLUGIN.getGameManager().getGAME_LOBBY().getPlayerProfile();
		int headCount = 0;

		//TODO: Fix this, its not pretty.
		if (playerHeads != null) {
			playerHeads.clear();
		}

		for (Player players: Bukkit.getOnlinePlayers()) {

			if (!playerProfile.get(players).isSpectator()) {
				ItemStack skull = createPlayerSkull(players);
				playerHeads.add(skull);
				tracker.setItem(headCount, skull);
				headCount++;
			}
		}
	}

	private ItemStack createPlayerSkull(Player player) {
		//Make the skull item.
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		skullMeta.setOwner(player.getName());
		skullMeta.setDisplayName(player.getName());
		skull.setItemMeta(skullMeta);

		return skull;
	}

	/**
	 * Now lets create the menu.
	 */
	private void createMenu() {
		String title = Messages.MENU_NAME_TRACKER.toString();
		tracker= Bukkit.createInventory(PLAYER, 9 * 4, title);
	}

	/**
	 * This will open the menu.
	 * @param player The layer we will send the menu to.
	 */
	public void openMenu(Player player) {
		makeMenuItems();

		player.openInventory(tracker);
	}

	/**
	 * This is toggled when a player clicks a menu item.
	 * @param player The player who clicked a menu item.
	 * @param menuName The name of the menu the player has open.
	 * @param item The item that the player has clicked.
	 * @return Returns true if ItemStack was successful interact click.
	 */
	public boolean playerInteractMenu(Player player, String menuName, ItemStack item) {
		String playerTrackerMenu = Messages.MENU_NAME_TRACKER.toString();

		//If the menu clicked, is the spectator menu, continue.
		if (menuName.equalsIgnoreCase(playerTrackerMenu)) {

			for (int i = 0; i < playerHeads.size(); i++) {
				String playerHeadName = ChatColor.stripColor(playerHeads.get(i).getItemMeta().getDisplayName());
				Player gamePlayer = Bukkit.getPlayer(playerHeadName);
						
				if (item.getItemMeta().getDisplayName().equals(playerHeadName)) {
					player.teleport(gamePlayer.getLocation());
					player.sendMessage(ChatColor.GREEN + "You were teleported to " + gamePlayer.getName() + ".");
					return true;
				}
			}
		}
		return false;
	}
}