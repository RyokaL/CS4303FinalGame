package gameManager;

import map.Map;
import playerUnits.Unit;
import java.util.ArrayList;

public class Game {

	private Map map;
	private int numPlayers;
	
	private ArrayList<Unit> rTeam, bTeam, gTeam, yTeam, enemy;
	private int turn;
	
	public Game(Map map, boolean red, boolean blue, boolean green, boolean yellow, int startTurn) {
		if(red) {
			rTeam = new ArrayList<Unit>();
			//Init pos && unit on map
		}
		if(blue) {
			bTeam = new ArrayList<Unit>();
			//Init pos && unit on map
		}
		if(green) {
			gTeam = new ArrayList<Unit>();
			//Init pos && unit on map
		}
		if(yellow) {
			yTeam = new ArrayList<Unit>();
			//Init pos && unit on map
		}
		
		enemy = new ArrayList<Unit>();
		turn = startTurn;
		this.map = map;
	}
}