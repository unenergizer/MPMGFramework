package com.forgestorm.mgfw.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

public class ScoresToPlaces {

	/**
	 * This will convert a HashMap of player and scores to an array list of final places.
	 * @param scoreMap The HashMap of player and scores.
	 * @return Returns an array list of player places. (Example 1st place, 2nd place, etc)
	 */
	public ArrayList<String> scoresToPlaces(HashMap<Player, Integer> scoreMap) {

		ArrayList<String> places = new ArrayList<String>();
		ArrayList<Player> sortedPlayer = new ArrayList<Player>();
		
		//Convert the scoreMap values into an array.
		ArrayList<Integer> scoresList = new ArrayList<Integer>(scoreMap.values());
		
		//Sort the array.
		Collections.sort(scoresList);
		
		//Reverse the array.
		Collections.reverse(scoresList);
		
		//Get the player for the finished place.
		for (int i = 0; i < scoresList.size(); i++) {
			for (Entry<Player, Integer> entry : scoreMap.entrySet()) {
				Player player = entry.getKey();
				int score = entry.getValue();
				
				if (score == scoresList.get(i) && !sortedPlayer.contains(player)) {
					places.add(entry.getKey().getName());
					sortedPlayer.add(entry.getKey());
				}
			}
		}
		
		return places;
	}

}
