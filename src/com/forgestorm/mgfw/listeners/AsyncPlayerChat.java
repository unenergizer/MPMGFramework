package com.forgestorm.mgfw.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.api.MinigameTeams;
import com.forgestorm.servercore.api.ProfileAPI;

public class AsyncPlayerChat implements Listener {

	private final MGFramework PLUGIN;
	
	public AsyncPlayerChat(MGFramework plugin) {
		PLUGIN = plugin;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String prefix = new ProfileAPI(player).getProfile().getPrefix();
		ChatColor messageColor = ChatColor.WHITE;
		
		
		//Get players team color for chat messages.
		if(!PLUGIN.getProfile(player).isSpectator()) {
			MinigameTeams minigameTeams = PLUGIN.getMinigamePluginManager().getMinigameTeams();
			
			int playerTeam = PLUGIN.getGameManager().getTeamSelector().getPlayerTeam(player);
			messageColor = minigameTeams.getTeamColors().get(playerTeam);
		} else  {
			//Sectator messages will be gray.
			prefix = prefix.concat(ChatColor.DARK_GRAY + "[Spectator] ");
			messageColor = ChatColor.GRAY;
		}
		
		//Set message format.
		event.setFormat(prefix + ChatColor.GRAY + "%s" + ChatColor.DARK_GRAY + ": " + messageColor + "%s");
	}
}
