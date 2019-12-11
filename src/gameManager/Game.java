package gameManager;

import map.Map;
import map.Resource;
import map.Tile;
import playerUnits.Unit;
import processing.core.PApplet;
import unitClass.ClassStore;
import unitClass.UnitClass;

import java.util.ArrayList;
import java.util.HashMap;

import constants.Constants;
import helpers.Pair;

public class Game {

	private Map map;
	//private int numPlayers;
	
	private Player rTeam, bTeam, gTeam, yTeam, enemy;
	private ArrayList<Player> teams = new ArrayList<Player>(5);
	private HashMap<Pair, Resource> loot;
	
	private int turn;
	private int startTurn;
	
	private boolean red, blue, green, yellow;
	
	private ClassStore classes;
	
	private Camera cam;
	
	private final PApplet pa;
	
	public Game(Map map, boolean red, boolean blue, boolean green, boolean yellow, int startTurn, final PApplet pa, ClassStore classes) {
		this.pa = pa;
		this.classes = classes;
		this.map = map;
		this.red = red;
		this.blue = blue;
		this.green = green;
		this.yellow = yellow;
		
		if(red) {
			rTeam = new Player(Constants.RED, initUnit(Constants.RED));
		}
		if(blue) {
			bTeam = new Player(Constants.BLUE, initUnit(Constants.BLUE));
		}
		if(green) {
			gTeam = new Player(Constants.GREEN, initUnit(Constants.GREEN));
		}
		if(yellow) {
			yTeam = new Player(Constants.YELLOW, initUnit(Constants.YELLOW));
		}
		
		enemy = new Player(Constants.ENEMY);
		teams.add(rTeam); teams.add(bTeam); teams.add(gTeam); teams.add(yTeam); teams.add(enemy);
		
		loot = new HashMap<Pair, Resource>();
		
		turn = startTurn;
		this.startTurn = startTurn;
		Pair camStartPos = teams.get(turn).getUnits().get(0).getPos(); 
		cam = new Camera(camStartPos.x, camStartPos.y, map.getTileSize(), map.getMapStartPosX(), map.getMapStartPosY(), pa);
	}
	
	public Camera getCamera() {
		return cam;
	}
	
	public void update() {
		//Draw map/units etc here
		pa.image(map.getMapImg(), 0, 0);
		for(int i = 0; i < teams.size(); i++) {
			Player currTeam = teams.get(i);
			if(currTeam == null) {
				continue;
			}
			ArrayList<Unit> currTeamUnits = currTeam.getUnits();
			for(Unit u : currTeamUnits) {
				Pair unitPos = u.getPos();
				float actXPos = map.getMapStartPosX() + (unitPos.x * map.getTileSize());
				float actYPos = map.getMapStartPosY() + (unitPos.y * map.getTileSize());
				pa.image(u.getAssignedClass().getSprite(i), actXPos, actYPos);
				
				//If the unit has moved to a spot with loot, collect it
				if(loot.containsKey(unitPos)) {
					Resource obtained = loot.remove(unitPos);
					currTeam.addGold(obtained.getGold());
					if(obtained.hasLoot()) {
						if(!u.addToInventory(obtained.getLoot())) {
							currTeam.addToInventory(obtained.getLoot());
						}
					}
				}
			}
		}
	}
	
	private void spawnResources() {
		//TODO: Spawn in resources
	}
	
	private void spawnEnemies() {
		//TODO: Spawn in enemies
	}
	
	public int getWinner() {
		//TODO: Definitely better way of checking this
		if(!rTeam.isEmpty() && bTeam.isEmpty() && gTeam.isEmpty() && yTeam.isEmpty()) {
			return Constants.RED;
		}
		else if(rTeam.isEmpty() && !bTeam.isEmpty() && gTeam.isEmpty() && yTeam.isEmpty()) {
			return Constants.BLUE;
		}
		else if(rTeam.isEmpty() && bTeam.isEmpty() && !gTeam.isEmpty() && yTeam.isEmpty()) {
			return Constants.GREEN;
		}
		else if(rTeam.isEmpty() && bTeam.isEmpty() && gTeam.isEmpty() && !yTeam.isEmpty()) {
			return Constants.YELLOW;
		}
		else if(rTeam.isEmpty() && bTeam.isEmpty() && gTeam.isEmpty() && yTeam.isEmpty()) {
			return Constants.ENEMY;
		}
		else {
			return -1;
		}
	}
	
	public void endCurrentTurn() {
		//Pass turn here
		turn = (turn + 1) % 5;
		if(turn == Constants.RED && !red) {
			endCurrentTurn();
			return;
		}
		else if(turn == Constants.BLUE && !blue) {
			endCurrentTurn();
			return;
		}
		else if(turn == Constants.GREEN && !green) {
			endCurrentTurn();
			return;
		}
		else if(turn == Constants.YELLOW && !yellow) {
			endCurrentTurn();
			return;
		}
		
		Pair camNewTurnPos = teams.get(turn).getUnits().get(0).getPos();
		cam.updateGridPos(camNewTurnPos.x, camNewTurnPos.y);
		//Also update each unit if on a healing tile (or trap)
		
		for(int i = 0; i < teams.size(); i++) {
			ArrayList<Unit> currTeam = teams.get(i).getUnits();
			for(Unit u : currTeam) {
				Pair unitPos = u.getPos();
				Tile currTile = map.getTileAtPos(unitPos);
				if(currTile.isHealing()) {
					int healing = u.getStats()[Constants.HP] * (currTile.getHealPercent() / 100);
					u.heal(healing);
				}
				//Unit cannot die from being on a trap tile, only be left with 1 health
				if(currTile.isTrap()) {
					int damage = u.getStats()[Constants.HP] * (currTile.getDamagePercent() / 100);
					if(damage > u.getHealthPoints()) {
						damage = u.getHealthPoints() - 1;
					}
					u.takeDamage(damage);
				}
			}
		}
		if(turn == startTurn) {
			//On every loop of turns try spawning more resources/enemies
			spawnResources();
			spawnEnemies();
		}
	}
	
	public int getCurrentTurn() {
		return turn;
	}
	
	public Player getCurrentPlayer() {
		return teams.get(turn);
	}
	
	private Unit initUnit(int team) {
		//This could maybe be altered?
		String initialUnit = "Tactician";
		UnitClass initClass = classes.getClassObj(initialUnit);
		
		if(team == Constants.RED) {
			Pair startPos = map.getRedStartPos();
			return new Unit(startPos.x, startPos.y, initClass, pa);
		}
		else if(team == Constants.BLUE) {
			Pair startPos = map.getBlueStartPos();
			return new Unit(startPos.x, startPos.y, initClass, pa);
		}
		else if(team == Constants.GREEN) {
			Pair startPos = map.getGreenStartPos();
			return new Unit(startPos.x, startPos.y, initClass, pa);
		}
		else if(team == Constants.YELLOW) {
			Pair startPos = map.getYellowStartPos();
			return new Unit(startPos.x, startPos.y, initClass, pa);
		}
		return null;
	}
}