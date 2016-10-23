package com.forgestorm.mgfw.bungeecord;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.forgestorm.mgfw.MGFramework;

import net.md_5.bungee.api.ChatColor;

public class BungeeCord implements PluginMessageListener {
	
	private final MGFramework PLUGIN;
	private Logger log = Logger.getLogger("Minecraft");
	
	public BungeeCord(MGFramework plugin) {
		PLUGIN = plugin;
	}
	
	public boolean connectToBungeeServer(Player player, String server) {
		//Send connection message.
		player.sendMessage(ChatColor.RED + "Connecting you to server \"" + ChatColor.YELLOW + server + ChatColor.RED + "\"...");
		
		try {
			Messenger messenger = Bukkit.getMessenger();
			if (!messenger.isOutgoingChannelRegistered(PLUGIN, "BungeeCord")) {
				messenger.registerOutgoingPluginChannel(PLUGIN, "BungeeCord");
			}
			
			if (server.length() == 0) {
				player.sendMessage("&cThe server name was empty!");
				return false;
			}
			
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(byteArray);

			out.writeUTF("Connect");
			out.writeUTF(server);

			player.sendPluginMessage(PLUGIN, "BungeeCord", byteArray.toByteArray());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Could not handle BungeeCord command from " + player.getName() + ": tried to connect to \"" + server + "\".");

			return false;
		}
		return true;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {}
	}

}
