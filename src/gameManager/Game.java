package gameManager;

import map.Map;
import map.Resource;
import map.Tile;
import playerUnits.Unit;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import unitClass.ClassStore;
import unitClass.UnitClass;
import weapons.Blacksmith;
import weapons.Weapon;

import java.util.ArrayList;
import java.util.HashMap;

import constants.Constants;
import gui.BattleMenu;
import gui.BattleUI;
import gui.UnitBattleMenu;
import helpers.Pair;
import helpers.Triple;

public class Game {

	private Map map;
	//private int numPlayers;
	private final int NO_MENU = 0;
	private final int ACTION_MENU = 1;
	private final int PLAYER_MENU = 2;
	
	private final int SELECT_NORMAL = 0;
	private final int SELECT_ATTACK = 1;
	private final int SELECT_PLACE = 2;
	
	private UnitBattleMenu unitMenu = null;
	private BattleMenu playerMenu = null;
	
	private BattleUI gameUI;
	
	private Player rTeam, bTeam, gTeam, yTeam, enemy;
	private ArrayList<Player> teams = new ArrayList<Player>(5);
	private HashMap<Pair, Resource> loot;
	
	private int turn;
	private int startTurn;
	
	private boolean red, blue, green, yellow;
	
	private ClassStore classes;
	private Blacksmith weapons;
	
	private Camera cam;
	
	private final PApplet pa;
	
	private Unit[][] easyAccessMap;
	
	private boolean unitSelected;
	private Unit selectedUnit;
	
	private int openMenuState = 0;
	
	private int selectState = 0;
	
	public Game(Map map, boolean red, boolean blue, boolean green, boolean yellow, int startTurn, final PApplet pa, ClassStore classes, Blacksmith weapons) {
		this.pa = pa;
		this.classes = classes;
		this.weapons = weapons;
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
		
		loot = new HashMap<Pair, Resource>();
		
		turn = startTurn;
		this.startTurn = startTurn;
		Pair camStartPos = teams.get(turn).getUnits().get(0).getPos(); 
		cam = new Camera(camStartPos.x, camStartPos.y, map.getTileSize(), map.getMapStartPosX(), map.getMapStartPosY(), pa);
		
		gameUI = new BattleUI(pa);
	}
	
	public Camera getCamera() {
		return cam;
	}
	
	public void update() {
		//Draw map/units etc here
		PVector gameCamPos = cam.getTransPos();
		pa.pushMatrix();
		pa.translate(gameCamPos.x - map.getMapStartPosX(), gameCamPos.y - map.getMapStartPosY());
		pa.image(map.getMapImg(), 0, 0);
		pa.popMatrix();
		
		pa.pushMatrix();
		pa.translate(gameCamPos.x, gameCamPos.y);
		
		//Draw selected tile:
		cam.drawSelectedGrid();
		
		//Current turn - move to UI
		pa.fill(255);
		pa.text(turn, pa.width/2, pa.height/2);
		
		//Check if selected tile is a unit and draw reach:
		//TODO: Make better :)
		Unit selected;
		if(unitSelected) {
			if(selectState == SELECT_ATTACK) {
				Pair[] attackDraw = map.getAttackSpaces(selectedUnit);
				pa.fill(0x44b30000);
				for(Pair p : attackDraw) {
					Triple t = cam.convertToDrawPos(p);
					pa.rect(t.X, t.Y, t.Z, t.Z);
				}
			}
			else {
				Pair[] toDraw = selectedUnit.getMoveableSpaces();
				pa.fill(0x441f75ff);
				for(Pair p : toDraw) {
					Triple t = cam.convertToDrawPos(p);
					pa.rect(t.X, t.Y, t.Z, t.Z);
				}
			}
		}
		if((selected = isPosUnit()) != null) {
			if(selectState == SELECT_ATTACK) {
				if(selectedUnit.getEquipped().isHealing() && selected.getTeam() == selectedUnit.getTeam()) {
					//display info on healing changes
				}
				else if(selected.getTeam() != selectedUnit.getTeam()) {
					//display info on attack
				}
			}
			else {
				Pair[] toDraw = map.getMovementSpaces(selected, easyAccessMap);
				pa.fill(0x441f75ff);
				for(Pair p : toDraw) {
					Triple t = cam.convertToDrawPos(p);
					pa.rect(t.X, t.Y, t.Z, t.Z);
				}
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
				float actXPos = (unitPos.x * map.getTileSize());
				float actYPos = (unitPos.y * map.getTileSize());
				
				PImage sprite = u.getAssignedClass().getSprite(i);
				
				pa.image(sprite, actXPos + sprite.width/4, actYPos + sprite.height/4);
				
				//Draw health bars
				pa.fill(10);
				pa.stroke(10);
				pa.rect(actXPos + sprite.width/4, actYPos + sprite.height * 1.25f, 25, 5);
				
				if(i == turn) {
					pa.fill(0xFF22ff12);
				}
				else {
					pa.fill(0xFFdb0000);
				}
				pa.noStroke();
				float healthPercentage = 25 * ((float)u.getHealthPoints()/(float)u.getStats()[Constants.HP]);
				pa.rect(actXPos + sprite.width/4, actYPos + sprite.height * 1.25f + 0.5f, healthPercentage, 4);
				
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
		
		//Add some sign unit is selected
		
		//Draw battle menu if using
		if(openMenuState == ACTION_MENU && unitMenu != null) {
			unitMenu.update();
		}
		
		//Draw player menu
		if(openMenuState == PLAYER_MENU) {
			playerMenu.update();
		}
		
		//Key input
		if(pa.keyPressed && openMenuState == NO_MENU) {
			pa.keyPressed = false;
			if(pa.key == PConstants.CODED) {
				switch(pa.keyCode) {
					case PConstants.LEFT:
						changeSelectedPos(-1, 0);
						break;
					case PConstants.RIGHT:
						changeSelectedPos(1, 0);
						break;
					case PConstants.UP:
						changeSelectedPos(0, -1);
						break;
					case PConstants.DOWN:
						changeSelectedPos(0, 1);
						break;
				}
			}
			else {
				switch(pa.key) {
					case ' ':
						selectCurrentPos();
						break;
					case 'q':
						cancelSelection();
						break;
					case 'e':
						playerMenu = new BattleMenu(pa, this, teams.get(turn), cam.getTransPos());
						openMenuState = PLAYER_MENU;
						break;
					case 'a':
						//Move to previous unmoved unit
						break;
					case 'd':
						//Move to next unmoved unit
						break;
				}
			}
		}
		
		pa.popMatrix();
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
				//Update movable spaces
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
		
		openMenuState = NO_MENU;
		playerMenu = null;
		
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
		String initialWeapon = "Training Sword";
		
		UnitClass initClass = classes.getClassObj(initialUnit);
		Weapon initWeapon = weapons.getWeaponObj(initialWeapon);
		
		Unit toReturn = null;
		
		if(team == Constants.RED) {
			Pair startPos = map.getRedStartPos();
			toReturn = new Unit(startPos.x, startPos.y, initClass, Constants.RED, pa);
		}
		else if(team == Constants.BLUE) {
			Pair startPos = map.getBlueStartPos();
			toReturn = new Unit(startPos.x, startPos.y, initClass, Constants.BLUE, pa);
		}
		else if(team == Constants.GREEN) {
			Pair startPos = map.getGreenStartPos();
			toReturn =  new Unit(startPos.x, startPos.y, initClass, Constants.GREEN, pa);
		}
		else if(team == Constants.YELLOW) {
			Pair startPos = map.getYellowStartPos();
			toReturn =  new Unit(startPos.x, startPos.y, initClass, Constants.YELLOW, pa);
		}
		
		if(toReturn != null) {
			toReturn.equipWeapon(initWeapon);
		}
		return toReturn;
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
			if(selectState == SELECT_ATTACK) {
				for(Pair p : map.getAttackSpaces(selectedUnit)) {
					if(currentPos.equals(p)) {
						Unit toAttack;
						if((toAttack = easyAccessMap[currentPos.x][currentPos.y]) != null) {
							if(toAttack.getTeam() != turn) {
								//TODO: Add a check for if using a healing weapon
								attackUnit(toAttack);
								selectState = SELECT_NORMAL;
								moveAndUpdateSelection(selectedUnit.getPos());
							}
						}
					}
				}
			}
			else if(selectState == SELECT_NORMAL){
				for(Pair p : selectedUnit.getMoveableSpaces()) {
					if(currentPos.equals(p)) {
						if(easyAccessMap[currentPos.x][currentPos.y] != null) {
							if(easyAccessMap[currentPos.x][currentPos.y] != selectedUnit) {
								break;
							}
						}
						//Valid space
						//Bring up action menu
						selectedUnit.setMoving(true);
						selectedUnit.setMovePos(currentPos.x, currentPos.y);
						unitMenu = new UnitBattleMenu(pa, this, selectedUnit, cam);
						openMenuState = ACTION_MENU;
					}
					
				}
			}
		}
		else {
			openMenuState = NO_MENU;
			unitMenu = null;
			selectedUnit = easyAccessMap[currentPos.x][currentPos.y];
			if(selectedUnit == null) {
				selectedUnit = null;
				unitSelected = false;
				
				playerMenu = new BattleMenu(pa, this, teams.get(turn), cam.getTransPos());
				openMenuState = PLAYER_MENU;
			}
			else if(selectedUnit.hasMoved() || selectedUnit.getTeam() != turn) {
				selectedUnit = null;
				unitSelected = false;
				
			}
			else {
				unitSelected = true;
			}
		}
	}
	
	public void moveAndUpdateSelection(Pair newPos) {
		selectedUnit.setMoving(false);
		easyAccessMap[selectedUnit.getPos().x][selectedUnit.getPos().y] = null;
		selectedUnit.setNewPos(newPos.x, newPos.y);
		selectedUnit.setMoved(true);
		easyAccessMap[selectedUnit.getPos().x][selectedUnit.getPos().y] = selectedUnit;
		
		cancelSelection();
		
		//Update everyone's spaces as unit moving may have blocked access
		for(Unit[] uArray : easyAccessMap) {
			for(Unit u : uArray) {
				if(u == null) {
					continue;
				}
				u.setSpacesNewTurn(map.getMovementSpaces(u, easyAccessMap));
			}
		}
	}
	
	public void selectAttack() {
		selectState = SELECT_ATTACK;
		openMenuState = NO_MENU;
	}
	
	public void attackUnit(Unit other) {
		int chanceToHit = selectedUnit.getEquipped().getHitRate(); //Maybe alter this
		int critChance = (int) (selectedUnit.getEquipped().getCriticalRate() + 0.25 * selectedUnit.getStats()[Constants.DEX]);
		int damage = selectedUnit.getEquipped().getAttack() + selectedUnit.getStats()[Constants.STR] - other.getStats()[Constants.DEF];
		
		//Do stuff with follow-up attacks && counters
		
		if(damage < 0) {
			damage = 0;
		}
		
		if(pa.random(100) > chanceToHit) {
			damage = 0;
		}
		if(pa.random(100) < critChance) {
			damage = damage * 3;
		}		
		other.takeDamage(damage);
		
		if(other.getHealthPoints() < 0) {
			teams.get(other.getTeam()).removeUnit(other);
		}
	}
	
	public void cancelSelection() {
		if(selectedUnit != null) {
			selectedUnit.setMoving(false);
			cam.updateGridPos(selectedUnit.getPos().x, selectedUnit.getPos().y);
		}
		
		unitSelected = false;
		selectedUnit = null;
		openMenuState = NO_MENU;
		selectState = SELECT_NORMAL;
		unitMenu = null;
	}
}