package com.forgestorm.mgfw.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameArena;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.gui.SpectatorMenu;

public class InventoryClick implements Listener {

	private MGFramework PLUGIN;

	public InventoryClick(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onInventroyClick(InventoryClickEvent event) {
		GameManager gameManager = PLUGIN.getGameManager();
		boolean isRunning = gameManager.isMinigameRunning();

		Bukkit.broadcastMessage("ClickEvent: invName -> " + event.getInventory().getName());
		Bukkit.broadcastMessage("ClickEvent: invTitle -> " + event.getInventory().getTitle());
		
		Bukkit.broadcastMessage("ClickInvEvent: invName -> " + event.getClickedInventory().getName());
		Bukkit.broadcastMessage("ClickInvEvent: invTitle -> " + event.getClickedInventory().getTitle());
		
		if (isRunning) {
			Player player = (Player) event.getWhoClicked();
			boolean isSpectator = PLUGIN.getGameLobby().getPlayerProfile().get(player).isSpectator();
			
			//If the player is a spectator lets continue.
			if (isSpectator) {

				GameArena gameArena = PLUGIN.getGameArena();
				SpectatorMenu spectatorMenu = gameArena.getSpectatorMenu();

				String menuName = event.getClickedInventory().getTitle();
				ItemStack item = event.getCurrentItem();
				
				Bukkit.broadcastMessage("Event: " + menuName);
				boolean spectatorMenuClick = spectatorMenu.playerInteractMenu(player, menuName, item);
				
				//Player is trying to open spectator menu.
				if (item.getType().equals(Material.REDSTONE)) {
					spectatorMenu.openMenu(player);

					//Play confirmation sound.
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, .5f);
				}
				
				//Test if an inventory item was clicked.
				if (spectatorMenuClick) {
					//Prevent item from being moved.
					event.setCancelled(true);

					//Play confirmation sound.
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, .5f);
				}
			}
		}
	}
}
