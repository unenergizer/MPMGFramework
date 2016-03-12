package com.forgestorm.mgfw.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameLobby;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.constants.GameState;
import com.forgestorm.mgfw.core.constants.Messages;

public class Admin implements CommandExecutor {

	private final MGFramework PLUGIN;

	public Admin(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if (!(commandSender instanceof Player)) {
			return false;
		}
		
		//Check if the command sender is a server Operator
		if (!commandSender.isOp()) {
			commandSender.sendMessage(Messages.COMMAND_ADMIN_NOT_OP.toString());
			return false;
		}

		GameManager gameManager = PLUGIN.getGameManager();
		GameLobby gameLobby = PLUGIN.getGameLobby();
		Player player = (Player) commandSender;

		if (args.length == 1) {
			switch (args[0].toLowerCase()) {
			case "help":
				//Messages.USAGE_GAME.send(player);
				break;
			case "gamesplayed":
				player.sendMessage(Messages.COMMAND_ADMIN_GAMES_PLAYED.toString().replace("%s", Integer.toString(gameManager.getGamesPlayed())));
				break;
			case "pause":
				//Messages.CREATED_NUKEROOM.send(player);
				break;
			default:
				player.sendMessage(Messages.COMMAND_ADMIN_UNKNOWN.toString());
			}
		} else if (args.length == 2) {
			switch (args[0].toLowerCase()) {
			case "maxplayers":
				gameManager.setMaxPlayers(Integer.valueOf(args[1]));
				gameLobby.getScoreboard().updateAllPlayerScoreboards();
				break;
			case "minplayers":
				gameManager.setMinPlayers(Integer.valueOf(args[1]));
				//If the game has enough players to start, lets do that now.
				if (gameManager.shouldMinigameStart() && !gameManager.getGameState().equals(GameState.LOBBY_COUNTDOWN)) {
					gameManager.startCountdown();
				}
				break;
			default:
				player.sendMessage(Messages.COMMAND_ADMIN_UNKNOWN.toString());
			}
		}
		return false;
	}

}
