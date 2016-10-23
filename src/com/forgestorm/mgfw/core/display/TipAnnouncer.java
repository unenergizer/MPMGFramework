package com.forgestorm.mgfw.core.display;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.forgestorm.mgfw.MGFramework;

import net.md_5.bungee.api.ChatColor;

public class TipAnnouncer {

	private final MGFramework PLUGIN;
	private final int gameTipTime = 20 * 30;

	private int tipDisplayed;
	private boolean showTips;

	public TipAnnouncer(MGFramework plugin) {
		PLUGIN = plugin;
		showTips = true;
	}

	/**
	 * This starts the thread that will loop over and over displaying tips and
	 * other useful information to the player.
	 */
	public void startTipMessages(ArrayList<String> tips) {
		showTips = true;
		int numberOfTips = tips.size();

		//Start a repeating task.
		new BukkitRunnable() {
			
			@Override
			public void run() {

				if (showTips) {
					String gameTip = tips.get(tipDisplayed);

					//Show the tip.
					sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tip" 
							+ ChatColor.YELLOW + " #"
							+ Integer.toString(tipDisplayed + 1)
							+ ChatColor.DARK_GRAY + ChatColor.BOLD + ": " 
							+ ChatColor.WHITE + gameTip);

					//Setup to display the next tip.
					if ((tipDisplayed + 1) == numberOfTips) {
						//Reset the tip count.  All tips have been displayed.
						tipDisplayed = 0;
					} else {
						//Increment the tip count to display the next tip.
						tipDisplayed++;
					}
				} else {
					//Cancel the tip rotation.
					cancel();
				}
			}
		}.runTaskTimerAsynchronously(PLUGIN, 0, gameTipTime);
	}
	
	public void sendMessage(String message) {
		for (Player players: Bukkit.getOnlinePlayers()) {
			//Send Message
			players.sendMessage(message);
			
			//Play Sound
        	players.playSound(players.getEyeLocation(), Sound.UI_BUTTON_CLICK, .5F, .2f);
		}
	}
	
	/**
	 * Set showTips to false to cancel all tip messages.
	 * @param showTips A boolean value that can stop tip messages form being displayed.
	 */
	public void setShowTips(boolean showTips) {
		this.showTips = showTips;
	}
}
