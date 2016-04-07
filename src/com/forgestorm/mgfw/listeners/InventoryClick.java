package com.forgestorm.mgfw.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameArena;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.gui.PlayerTracker;
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

		if (isRunning) {
			Player player = (Player) event.getWhoClicked();
			boolean isSpectator = PLUGIN.getGameLobby().getPlayerProfile().get(player).isSpectator();
			
			//If the player is a spectator lets continue.
			if (isSpectator && event.getClickedInventory() != null) {

				GameArena gameArena = PLUGIN.getGameArena();
				SpectatorMenu optionsMenu = gameArena.getSpectatorMenu(player);
				PlayerTracker trackerMenu = gameArena.getSpectatorTrackerMenu(player);

				String menuName = event.getClickedInventory().getTitle();
				ItemStack item = event.getCurrentItem();
				
				boolean optionMenuClick = optionsMenu.playerInteractMenu(player, menuName, item);
				boolean trackerMenuClick = trackerMenu.playerInteractMenu(player, menuName, item);
				
				//Test if an inventory item was clicked.
				if (optionMenuClick || trackerMenuClick) {
					//Prevent item from being moved.
					event.setCancelled(true);

					//Play confirmation sound.
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, .5f);
				}
			}
		}
	}
}
