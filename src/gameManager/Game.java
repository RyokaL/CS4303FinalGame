package gameManager;

import map.Map;
import map.Resource;
import map.Tile;
import playerUnits.Unit;
import processing.core.PApplet;
import processing.core.PImage;
import unitClass.ClassStore;
import unitClass.UnitClass;

import java.util.ArrayList;
import java.util.HashMap;

import constants.Constants;
import helpers.Pair;
import helpers.Triple;

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
	
	private Unit[][] easyAccessMap;
	
	private boolean unitSelected;
	private Unit selectedUnit;
	
	public Game(Map map, boolean red, boolean blue, boolean green, boolean yellow, int startTurn, final PApplet pa, ClassStore classes) {
		this.pa = pa;
		this.classes = classes;
		this.map = map;
		this.red = red;
		this.blue = blue;
		this.green = green;
		this.yellow = yellow;
		
		easyAccessMap = new Unit[map.getMap().length][map.getMap()[0].length];
		
		if(red) {
			Unit redPlayer = initUnit(Constants.RED);
			easyAccessMap[redPlayer.getPos().x][redPlayer.getPos().y] = redPlayer;
			rTeam = new Player(Constants.RED, redPlayer);
			System.out.println(rTeam.getUnits());
		}
		if(blue) {
			Unit bluePlayer = initUnit(Constants.BLUE);
			easyAccessMap[bluePlayer.getPos().x][bluePlayer.getPos().y] = bluePlayer;
			bTeam = new Player(Constants.BLUE, bluePlayer);
		}
		if(green) {
			Unit greenPlayer = initUnit(Constants.GREEN);
			easyAccessMap[greenPlayer.getPos().x][greenPlayer.getPos().y] = greenPlayer;
			gTeam = new Player(Constants.GREEN, greenPlayer);
		}
		if(yellow) {
			Unit yellowPlayer = initUnit(Constants.YELLOW);
			easyAccessMap[yellowPlayer.getPos().x][yellowPlayer.getPos().y] = yellowPlayer;
			yTeam = new Player(Constants.YELLOW, yellowPlayer);
		}
		
		for(Unit[] uArray : easyAccessMap) {
			for(Unit u : uArray) {
				if(u == null) {
					continue;
				}
				u.setSpacesNewTurn(map.getMovementSpaces(u, easyAccessMap));
			}
		}
		
		enemy = new Player(Constants.ENEMY);
		teams.add(rTeam); teams.add(bTeam); teams.add(gTeam); teams.add(yTeam); teams.add(enemy);
		System.out.println(teams);
		for(Player p : teams) {
			System.out.println(p);
			if(p == null) {
				continue;
			}
			System.out.println(p.getUnits());
		}
		
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
		
		//Draw selected tile:
		cam.drawSelectedGrid();
		
		pa.fill(255);
		pa.text(turn, pa.width/2, pa.height/2);
		
		//Check if selected tile is a unit and draw reach:
		Unit selected;
		if((selected = isPosUnit()) != null) {
			Pair[] toDraw = map.getMovementSpaces(selected, easyAccessMap);
			pa.fill(0x441f75ff);
			for(Pair p : toDraw) {
				Triple t = cam.convertToDrawPos(p);
				pa.rect(t.X, t.Y, t.Z, t.Z);
			}
		}
		
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
				
				PImage sprite = u.getAssignedClass().getSprite(i);
				
				pa.image(sprite, actXPos + sprite.width/4, actYPos + sprite.height/4);
				
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
	
	private Unit isPosUnit() {
		for(Player p : teams) {
			if(p == null) {
				continue;
			}
			Unit u;
			if((u = p.getUnitAtPos(cam.getSelectedGridPos())) != null) {
				return u;
			}
		}
		return null;
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
		
		for(Unit[] uArray : easyAccessMap) {
			for(Unit u : uArray) {
				//Update moveable spaces
				if(u == null) {
					continue;
				}
				u.setSpacesNewTurn(map.getMovementSpaces(u, easyAccessMap));
				u.setMoved(false);
			}
		}
		//TODO: Skip enemy turn for now/camera movement
		if(turn != Constants.ENEMY) {
			Pair camNewTurnPos = teams.get(turn).getUnits().get(0).getPos();
			cam.updateGridPos(camNewTurnPos.x, camNewTurnPos.y);
		}
		
		if(turn == startTurn) {
			//Also update each unit if on a healing tile (or trap)
			for(int i = 0; i < teams.size(); i++) {
				if(teams.get(i) == null) {
					continue;
				}
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
			return new Unit(startPos.x, startPos.y, initClass, Constants.RED, pa);
		}
		else if(team == Constants.BLUE) {
			Pair startPos = map.getBlueStartPos();
			return new Unit(startPos.x, startPos.y, initClass, Constants.BLUE, pa);
		}
		else if(team == Constants.GREEN) {
			Pair startPos = map.getGreenStartPos();
			return new Unit(startPos.x, startPos.y, initClass, Constants.GREEN, pa);
		}
		else if(team == Constants.YELLOW) {
			Pair startPos = map.getYellowStartPos();
			return new Unit(startPos.x, startPos.y, initClass, Constants.YELLOW, pa);
		}
		return null;
	}
	
	public void changeSelectedPos(int posXChange, int posYChange) {
		Pair currentPos = cam.getSelectedGridPos();
		int newX = currentPos.x + posXChange;
		int newY = currentPos.y + posYChange;
		
		if(currentPos.x + posXChange > map.getMap().length - 1 || currentPos.x + posXChange < 0) {
			newX = currentPos.x;
		}
		if(currentPos.y + posYChange > map.getMap()[0].length - 1 || currentPos.y + posYChange < 0) {
			newY = currentPos.y;
		}
		cam.updateGridPos(newX, newY);
	}
	
	public void selectCurrentPos() {
		Pair currentPos = cam.getSelectedGridPos();
		if(unitSelected) {
			for(Pair p : selectedUnit.getMoveableSpaces()) {
				if(currentPos.equals(p)) {
					//Valid space
					//Bring up action menu - for now just move
					if(easyAccessMap[currentPos.x][currentPos.y] != null) {
						break;
					}
					
					easyAccessMap[selectedUnit.getPos().x][selectedUnit.getPos().y] = null;
					selectedUnit.setNewPos(currentPos.x, currentPos.y);
					selectedUnit.setMoved(true);
					easyAccessMap[selectedUnit.getPos().x][selectedUnit.getPos().y] = selectedUnit;
					
					unitSelected = false;
					selectedUnit = null;
					
					//Update everyone's spaces as unit moving may have blocked access
					for(Unit[] uArray : easyAccessMap) {
						for(Unit u : uArray) {
							if(u == null) {
								continue;
							}
							u.setSpacesNewTurn(map.getMovementSpaces(u, easyAccessMap));
						}
					}
					break;
				}
				
			}
		}
		else {
			selectedUnit = easyAccessMap[currentPos.x][currentPos.y];
			if(selectedUnit == null || selectedUnit.hasMoved() || selectedUnit.getTeam() != turn) {
				selectedUnit = null;
				unitSelected = false;
			}
			else {
				unitSelected = true;
			}
		}
	}
	
	public void cancelSelection() {
		unitSelected = false;
		selectedUnit = null;
	}
}