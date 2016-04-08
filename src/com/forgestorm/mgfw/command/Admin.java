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

		//Check if the command sender is a server Operator
		if (!commandSender.isOp() && (commandSender instanceof Player)) {
			commandSender.sendMessage(Messages.COMMAND_ADMIN_NOT_OP.toString());
			return false;
		}

		GameManager gameManager = PLUGIN.getGameManager();
		GameLobby gameLobby = gameManager.getGAME_LOBBY();
		
		if (args.length == 1) {
			switch (args[0].toLowerCase()) {
			case "help":
				//Messages.USAGE_GAME.send(player);
				break;
			case "endgame":
				//End the game, if the game is actually running.
				if (gameManager.isMinigameRunning()) {
					commandSender.sendMessage(Messages.COMMAND_ADMIN_END_GAME.toString());
					gameManager.endGame(true);
				} else {
					//The game was not running. Send error message.
					commandSender.sendMessage(Messages.COMMAND_ADMIN_END_ERROR.toString());
				}
				break;
			case "gamesplayed":
				//Send the number of games played.
				commandSender.sendMessage(Messages.COMMAND_ADMIN_GAMES_PLAYED.toString().replace("%s", Integer.toString(gameManager.getGamesPlayed())));
				break;
			case "forcestart":
				commandSender.sendMessage(Messages.COMMAND_ADMIN_FORCE_START.toString());
				gameManager.startGame();
				break;
			case "pause":
				//TODO: create the ability to pause a game and freeze players.
				break;
			default:
				commandSender.sendMessage(Messages.COMMAND_ADMIN_UNKNOWN.toString());
			}
		} else if (args.length == 2) {
			switch (args[0].toLowerCase()) {
			case "maxplayers":
				//Set the maximum amount of players allowed in the server.
				gameManager.setMaxPlayers(Integer.valueOf(args[1]));
				
				//Update every players scoreboard.
				gameLobby.getScoreboard().updateAllPlayerScoreboards();
				break;
			case "minplayers":
				//Set the minimal amount of players to start game.
				gameManager.setMinPlayers(Integer.valueOf(args[1]));
				
				//Update every players scoreboard.
				gameLobby.getScoreboard().updateAllPlayerScoreboards();
				
				//If the game has enough players to start, lets do that now.
				if (gameManager.shouldMinigameStart() && !gameManager.getGameState().equals(GameState.LOBBY_COUNTDOWN)) {
					gameManager.startCountdown();
				}
				break;
			default:
				commandSender.sendMessage(Messages.COMMAND_ADMIN_UNKNOWN.toString());
			}
		}
		return false;
	}

}
