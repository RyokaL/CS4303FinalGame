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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import constants.Constants;
import gui.BattleMenu;
import gui.BattleResultsMenu;
import gui.ItemMenu;
import gui.PurchaseMenu;
import gui.UnitBattleMenu;
import helpers.BattleObj;
import helpers.BattleResult;
import helpers.Pair;
import helpers.Triple;
import items.Blacksmith;
import items.Item;
import items.Weapon;

public class Game {

	private Map map;
	//private int numPlayers;
	private final int NO_MENU = 0;
	private final int ACTION_MENU = 1;
	private final int PLAYER_MENU = 2;
	private final int TRADE_MENU = 3;
	private final int STATE_BATTLE = 4;
	private final int STATE_BUY = 5;
	private final int STATE_NEXT_TURN = 6;
	
	private final int SELECT_NORMAL = 0;
	private final int SELECT_ATTACK = 1;
	private final int SELECT_TRADE = 2;
	
	private final int BASE_ENEMY_SPAWN = 35;
	private final int INC_ENEMY_SPAWN = 10;
	private final int COOLDOWN_ENEMY_SPAWN = 2; //no of turns
	
	private final int BASE_ITEM_SPAWN = 10;
	private final int INC_ITEM_SPAWN = 5;
	private final int COOLDOWN_ITEM_SPAWN = 5; //no of turns
	
	private final int LOOT_CHANCE = 30;
	
	private int enemySpawnChance = BASE_ENEMY_SPAWN;
	private int enemySpawnCooldown = 0;
	
	private int itemSpawnChance = BASE_ITEM_SPAWN;
	private int itemSpawnCooldown = 0;
	
	private int difficulty = 0;
	private int wepDifficulty = 0;
	
	private UnitBattleMenu unitMenu = null;
	private BattleMenu playerMenu = null;
	private ItemMenu tradeMenu = null;
	private PurchaseMenu buyMenu = null;
	
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
	
	private BattleResultsMenu lastBattle;
	
	private boolean showExtra = true;
	
	private Item tradeFirst;
	private boolean startedTrade;
	private Unit otherUnit;
	private int tradeIndex;
	
	private int openMenuState = 0;
	
	private int selectState = 0;
	
	private int selectedUnitIndex = 0;
	
	private int winState = -1;
	
	private AIController ai;
	
	public Game(Map map, boolean red, boolean blue, boolean green, boolean yellow, int startTurn, final PApplet pa, ClassStore classes, Blacksmith weapons) {
		this.pa = pa;
		this.classes = classes;
		this.weapons = weapons;
		this.map = map;
		this.red = red;
		this.blue = blue;
		this.green = green;
		this.yellow = yellow;
		this.ai = new AIController();
		
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
//		Unit enemyTest = new Unit(2, 2, classes.getClassObj("Tactician"), Constants.ENEMY, pa);
//		easyAccessMap[2][2] = enemyTest;
//		enemyTest.equipWeapon(weapons.getWeaponObj("Training Sword"));
//		enemyTest.addToInventory(weapons.getWeaponObj("Worn Bow"));
//		enemy.addUnit(enemyTest);
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
	
	public Unit getHoverUnit() {
		return isPosUnit();
	}
	
	public Unit getSelectedUnit() {
		return selectedUnit;
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
				if(toDraw == null) {
					toDraw = map.getMovementSpaces(selectedUnit, easyAccessMap);
					selectedUnit.setSpacesNewTurn(toDraw);
				}
				for(Pair p : toDraw) {
					Triple t = cam.convertToDrawPos(p);
					pa.rect(t.X, t.Y, t.Z, t.Z);
				}
				
			}
		}
		if((selected = isPosUnit()) != null) {
			if(selectState == SELECT_ATTACK) {
				if(selectedUnit.getEquipped() != null && selectedUnit.getEquipped().isHealing() && selected.getTeam() == selectedUnit.getTeam()) {
					//TODO: display info on healing changes
					if(showExtra) {
						pa.pushMatrix();
						PVector currTrans = cam.getTransPos();
						pa.translate(-currTrans.x, -currTrans.y);
						
						pa.fill(255, 200);
						pa.rect(3* pa.width/4, pa.height/2, 250, 250);
						pa.fill(0, 200);
						pa.rect(3* pa.width/4, pa.height/2 - 100, 250, 100);
						
						BattleObj damage = getDamage(selectedUnit, selected);
						
						pa.textAlign(PConstants.LEFT);
						pa.fill(Constants.TEAM_COLOURS[selected.getTeam()]);
						pa.text(selected.getAssignedClass().getName(), 3* pa.width/4, pa.height/2);
						pa.fill(0);
						String x2To = damage.x2To ? " x2" : "";
						if(damage.damageTo < 0) {
							pa.text("Heal Rec: " + (-damage.damageTo) + x2To, 3* pa.width/4, pa.height/2 + 50);
						}
						else {
							pa.text("Dmg Rec: " + damage.damageTo + x2To, 3* pa.width/4, pa.height/2 + 50);
						}
						pa.text("Hit: " + damage.hitTo + "%", 3* pa.width/4, pa.height/2 + 100);
						
						pa.popMatrix();
					}
				}
				else if(selected.getTeam() != selectedUnit.getTeam()) {
					if(showExtra) {
						pa.pushMatrix();
						PVector currTrans = cam.getTransPos();
						pa.translate(-currTrans.x, -currTrans.y);
						
						pa.fill(255, 200);
						pa.rect(2* pa.width/4, pa.height/2, 250, 250);
						pa.fill(0, 200);
						pa.rect(2* pa.width/4, pa.height/2 - 100, 250, 100);
						
						pa.fill(255, 200);
						pa.rect(3* pa.width/4, pa.height/2, 250, 250);
						pa.fill(0, 200);
						pa.rect(3* pa.width/4, pa.height/2 - 100, 250, 100);
						
						BattleObj damage = getDamage(selectedUnit, selected);
						
						pa.textAlign(PConstants.LEFT);
						pa.fill(Constants.TEAM_COLOURS[selectedUnit.getTeam()]);
						pa.text(selectedUnit.getAssignedClass().getName(), 2* pa.width/4, pa.height/2);
						pa.fill(Constants.TEAM_COLOURS[selected.getTeam()]);
						pa.text(selected.getAssignedClass().getName(), 3* pa.width/4, pa.height/2);
						pa.fill(0);
						if(damage.counter) {
							String x2From = damage.x2From ? " x2" : "";
							pa.text("Dmg Rec: " + damage.damageFrom + x2From, 2* pa.width/4, pa.height/2 + 50);
							pa.text("Hit: " + damage.hitFrom + "%", 2* pa.width/4, pa.height/2 + 100);
							pa.text("Crit: " + damage.critFrom + "%", 2* pa.width/4, pa.height/2 + 150);
						}
						String x2To = damage.x2To ? " x2" : "";
						if(damage.damageTo < 0) {
							pa.text("Heal Rec: " + (-damage.damageTo) + x2To, 3* pa.width/4, pa.height/2 + 50);
						}
						else {
							pa.text("Dmg Rec: " + damage.damageTo + x2To, 3* pa.width/4, pa.height/2 + 50);
						}
						pa.text("Hit: " + damage.hitTo + "%", 3* pa.width/4, pa.height/2 + 100);
						pa.text("Crit: " + damage.critTo + "%", 3* pa.width/4, pa.height/2 + 150);
						
						pa.popMatrix();
					}
				}
			}
			else {
				Pair[] toDraw = map.getMovementSpaces(selected, easyAccessMap);
				pa.fill(0x441f75ff);
				for(Pair p : toDraw) {
					Triple t = cam.convertToDrawPos(p);
					pa.rect(t.X, t.Y, t.Z, t.Z);
				}
				
				if(showExtra) {
					pa.pushMatrix();
					PVector currTrans = cam.getTransPos();
					pa.translate(-currTrans.x, -currTrans.y);
					
					pa.fill(255, 200);
					pa.rect(3* pa.width/4, pa.height/2, 250, 350);
					pa.fill(0, 200);
					pa.rect(3* pa.width/4, pa.height/2 - 100, 250, 100);
					
					pa.textAlign(PConstants.LEFT);
					pa.fill(Constants.TEAM_COLOURS[selected.getTeam()]);
					pa.text(selected.getAssignedClass().getName(), 3* pa.width/4, pa.height/2);
					pa.fill(0);
					pa.text("Lv. "  + selected.getLevel(), 3* pa.width/4, pa.height/2 + 25);
					pa.text("HP: " + selected.getHealthPoints() + "/" + selected.getStats()[Constants.HP], 3* pa.width/4, pa.height/2 + 50);
					pa.textSize(14);
					pa.text(selected.toString(), 3* pa.width/4, pa.height/2 + 70);
					pa.textSize(32);
					pa.popMatrix();
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
				
				if(sprite != null) {
					pa.image(sprite, actXPos + sprite.width/4, actYPos + sprite.height/4);
					//Draw health bars
					pa.fill(10);
					pa.stroke(10);
					pa.rect(actXPos + sprite.width/4, actYPos + sprite.height * 1.25f, 40, 7);
					
					if(i == turn) {
						pa.fill(0xFF22ff12);
					}
					else {
						pa.fill(0xFFdb0000);
					}
					pa.stroke(0);
					float healthPercentage = 40 * ((float)u.getHealthPoints()/(float)u.getStats()[Constants.HP]);
					pa.rect(actXPos + sprite.width/4, actYPos + sprite.height * 1.25f + 0.5f, healthPercentage, 6);
				}
				else {
					pa.fill(Constants.TEAM_COLOURS[u.getTeam()]);
					pa.rect(actXPos - map.getTileSize()/2, actYPos -map.getTileSize()/2, map.getTileSize()/2, map.getTileSize()/2);
					//Draw health bars
					pa.fill(10);
					pa.stroke(10);
					pa.rect(actXPos +  map.getTileSize()/4, actYPos +  map.getTileSize() * 1.25f, 40, 7);
					
					if(i == turn) {
						pa.fill(0xFF22ff12);
					}
					else {
						pa.fill(0xFFdb0000);
					}
					pa.stroke(0);
					float healthPercentage = 40 * ((float)u.getHealthPoints()/(float)u.getStats()[Constants.HP]);
					pa.rect(actXPos + map.getTileSize()/4, actYPos + map.getTileSize() * 1.25f + 0.5f, healthPercentage, 6);
				}
			}
		}
		
		for(Resource r : loot.values()) {
			Pair rPos = r.getPos();
			float tileSize = map.getTileSize();
			float actXPos = (rPos.x * tileSize);
			float actYPos = (rPos.y * tileSize);
			
			pa.fill(0xFF693400);
			pa.rect(actXPos + tileSize/4, actYPos + tileSize/4, tileSize/2, tileSize/2);
		}
		
		//Draw current tile info:
		Tile selectedTile = map.getTileAtPos(cam.getSelectedGridPos());
		if(showExtra) {
			pa.pushMatrix();
			PVector currTrans = cam.getTransPos();
			pa.translate(-currTrans.x, -currTrans.y);
			
			pa.fill(255, 200);
			pa.rect(pa.width - 200, 0, 200, 100);
			pa.fill(0);
			pa.textAlign(PConstants.LEFT, PConstants.TOP);
			pa.textSize(12);
			pa.text(selectedTile.toString(), pa.width - 200, 30);
			pa.textSize(26);
			pa.text(selectedTile.getName(), pa.width - 200, 0);
			pa.popMatrix();
			pa.textSize(32);
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
		
		if(openMenuState == TRADE_MENU) {
			tradeMenu.update();
		}
		
		if(openMenuState == STATE_BATTLE) {
			lastBattle.update(cam.getTransPos());
		}
		
		if(openMenuState == STATE_BUY) {
			buyMenu.update();
		}
		
		if(openMenuState == STATE_NEXT_TURN) {
			pa.pushMatrix();
			PVector currTrans = cam.getTransPos();
			pa.translate(-currTrans.x, -currTrans.y);
			pa.fill(0, 200);
			pa.rect(0, 0, pa.width, pa.height);
			
			String nextTurn = "";
	    	switch(turn) {
	    		case Constants.RED:
	    			nextTurn = "Red Team";
	    			break;
	    		case Constants.BLUE:
	    			nextTurn = "Blue Team";
	    			break;
	    		case Constants.GREEN:
	    			nextTurn = "Green Team";
	    			break;
	    		case Constants.YELLOW:
	    			nextTurn = "Yellow Team";
	    			break;
	    		case Constants.ENEMY:
	    			nextTurn = "Enemy Team";
	    			break;
	    	}
	    	
	    	pa.fill(Constants.TEAM_COLOURS[turn]);
	    	pa.textAlign(PConstants.CENTER);
	    	pa.text(nextTurn + "'s Turn \n Press any key", pa.width/2, pa.height/2);
			
			pa.popMatrix();
		}
		
		if(pa.keyPressed && openMenuState == STATE_NEXT_TURN) {
			pa.keyPressed = false;
			openMenuState = NO_MENU;
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
						Player current = teams.get(turn);
						List<Unit> toMove = current.getUnmovedUnits();
						selectedUnitIndex -= 1;
						if(selectedUnitIndex < 0) {
							if(toMove.size() == 0) {
								break;
							}
							selectedUnitIndex = toMove.size() - 1;
						}
						Pair next = toMove.get(selectedUnitIndex).getPos();
						cam.updateGridPos(next.x, next.y);
						break;
					case 'd':
						//Move to next unmoved unit
						Player currentD = teams.get(turn);
						List<Unit> toMoveD = currentD.getUnmovedUnits();
						if(toMoveD.size() == 0) {
							break;
						}
						selectedUnitIndex = (selectedUnitIndex + 1) % (toMoveD.size());
						Pair nextD = toMoveD.get(selectedUnitIndex).getPos();
						cam.updateGridPos(nextD.x, nextD.y);
						break;
					case 's':
						showExtra = !showExtra;
						break;
				}
			}
		}
		
		pa.popMatrix();
	}
	
	private void spawnResources() {
		//TODO: Spawn in resources
		if(itemSpawnCooldown <= 0 && pa.random(100) < itemSpawnChance) {
			int posX;
			int posY;
			
			//Pick random pos without a resource
			posX = (int)pa.random(easyAccessMap.length);
			posY = (int)pa.random(easyAccessMap[0].length);
			Pair pos = new Pair(posX, posY);
			if(!loot.containsKey(pos)) {
				int gold = (int)pa.random(100 * (wepDifficulty + 1));
				if(pa.random(100) < LOOT_CHANCE) {
					//pick weapon
					int wepDiff = (int)pa.random(wepDifficulty + 1);
					ArrayList<Weapon> wepsRand = weapons.getRarityList(wepDiff);
					int weaponPick = (int)pa.random(wepsRand.size());
					loot.put(pos, new Resource(gold, pos.x, pos.y, wepsRand.get(weaponPick)));
				}
				else {
					loot.put(pos, new Resource(gold, pos.x, pos.y));
				}
				itemSpawnCooldown = COOLDOWN_ITEM_SPAWN;
				itemSpawnChance = BASE_ITEM_SPAWN;
			}
		}
		else {
			if(itemSpawnCooldown <= 0) {
				itemSpawnChance += INC_ITEM_SPAWN;
			}
			else {
				itemSpawnCooldown -= 1;
			}
		}
	}
	
	private void spawnEnemies() {
		//Roll to spawn
		if(enemySpawnCooldown <= 0 && pa.random(100) < enemySpawnChance) {
			int posX;
			int posY;
			
			//Pick class to spawn
			int randomDiff = (int)pa.random(difficulty + 1);
			ArrayList<UnitClass> diffClasses = classes.getStage(randomDiff);
			int randClass = (int)pa.random(diffClasses.size());
			UnitClass toSpawn = diffClasses.get(randClass);
			
			while(true) {
				posX = (int)pa.random(easyAccessMap.length);
				posY = (int)pa.random(easyAccessMap[0].length);
				if(easyAccessMap[posX][posY] == null) {
					Tile currTile = map.getTileAtPos(new Pair(posX, posY));
					int occupy = currTile.getUnitsCanOccupy();
					int mount = toSpawn.getMounted();
					boolean goodSpaceAndClass = false;
					switch(occupy) {
						case Constants.AFFECT_ALL:
							goodSpaceAndClass = true;
							break;
						case Constants.NO_UNITS:
							break;
						case Constants.NO_MOUNT:
						case Constants.GROUND_MOUNT:
						case Constants.FLYING_MOUNT:
							goodSpaceAndClass = mount == occupy;
							break;
						case Constants.AFFECT_MOUNTED:
							goodSpaceAndClass = mount == Constants.GROUND_MOUNT || mount == Constants.FLYING_MOUNT;
							break;
						case Constants.AFFECT_GROUND_UNITS:
							goodSpaceAndClass = mount == Constants.NO_MOUNT || mount == Constants.GROUND_MOUNT;
							break;
					}
					
					if(goodSpaceAndClass) {
						//Add unit
						Unit newEnemy = new Unit(posX, posY, toSpawn, Constants.ENEMY, pa);
						
						//TODO: random amount of level-ups
						
						boolean spawnEnemy = false;
						//Pick weapon(s)
						boolean weaponPicked = false;
						int weaponTries = 0;
						while(!weaponPicked) {
							if(weaponTries > 10) {
								break;
							}
							int[] weaponTypes = toSpawn.getEquipableWeapons();
							int randType = (int)pa.random(weaponTypes.length);
							int type = weaponTypes[randType];
							
							int wepDiff = (int)pa.random(wepDifficulty + 1);
							ArrayList<Weapon> wepsRand = weapons.getRarityList(wepDiff);
							List<Weapon> correctType = wepsRand.stream().filter(w -> w.getWeaponType() == type).collect(Collectors.toList());
							
							if(correctType.size() == 0) {
								weaponTries++;
								continue;
							}
							
							int pickWeapon = (int)pa.random(correctType.size());
							Weapon picked = correctType.get(pickWeapon);
							newEnemy.equipWeapon(picked);
							//Could allow additional weapons to be given, for now just basic
							weaponPicked = true;
							spawnEnemy = true;
						}
						if(spawnEnemy && weaponPicked) {
							enemy.addUnit(newEnemy);
							easyAccessMap[posX][posY] = newEnemy;
							enemySpawnChance = BASE_ENEMY_SPAWN;
							enemySpawnCooldown = COOLDOWN_ENEMY_SPAWN;
						}
						break;
					}
				}
			}
		}
		else {
			if(enemySpawnCooldown <= 0) {
				enemySpawnChance += INC_ENEMY_SPAWN;
			}
			else {
				enemySpawnCooldown -= 1;
			}
		}
	}
	
	private Unit isPosUnit() {
		Pair pos = cam.getSelectedGridPos();
		return easyAccessMap[pos.x][pos.y];
	}
	
	public int getWinner() {
		//TODO: Definitely better way of checking this
		if(red) {
			if(!rTeam.hasLost() && (!blue || bTeam.hasLost()) && (!green || gTeam.hasLost()) && (!yellow || yTeam.hasLost())) {
				return Constants.RED;
			}
		}
		
		if(blue) {
			if((!red || rTeam.hasLost()) && (!bTeam.hasLost()) && (!green || gTeam.hasLost()) && (!yellow || yTeam.hasLost())) {
				return Constants.BLUE;
			}
		}
		
		if(green) {
			if((!red || rTeam.hasLost()) && (!blue || bTeam.hasLost()) && (!gTeam.hasLost()) && (!yellow || yTeam.hasLost())) {
				return Constants.GREEN;
			}
		}
		
		if(yellow) {
			if((!red || rTeam.hasLost()) && (!blue || bTeam.hasLost()) && (!green || gTeam.hasLost()) && (!yTeam.hasLost())) {
				return Constants.YELLOW;
			}
		}
		
		if((!red || rTeam.hasLost()) && (!blue || bTeam.hasLost()) && (!green || gTeam.hasLost()) && (!yellow || yTeam.hasLost())) {
			return Constants.ENEMY;
		}
		
		return -1;
	}
	
	private void nextTurn() {
		//Pass turn here
		turn = (turn + 1) % 5;
		if(turn == Constants.RED && (!red || rTeam.isEmpty())) {
			nextTurn();
			return;
		}
		else if(turn == Constants.BLUE && (!blue || bTeam.isEmpty())) {
			nextTurn();
			return;
		}
		else if(turn == Constants.GREEN && (!green || gTeam.isEmpty())) {
			nextTurn();
			return;
		}
		else if(turn == Constants.YELLOW && (!yellow || yTeam.isEmpty())) {
			nextTurn();
			return;
		}
		else if(turn == Constants.ENEMY && enemy.isEmpty()) {
			nextTurn();
			return;
		}
	}
	
	public void endCurrentTurn() {
		cancelSelection();
		//Check win
		winState = getWinner();
		
		nextTurn();
		
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
		
		if(turn == Constants.ENEMY) {
			for(Unit u : enemy.getUnits()) {
				ai.getNextMove(u, easyAccessMap, map, this);
			}
		}
		//TODO: Skip enemy turn camera movement for now, change to show movements
		if(turn != Constants.ENEMY) {
			Pair camNewTurnPos = teams.get(turn).getUnits().get(0).getPos();
			cam.updateGridPos(camNewTurnPos.x, camNewTurnPos.y);
		}
		
		selectedUnitIndex = 0;
		
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
					u.updateEffects();
					Pair unitPos = u.getPos();
					Tile currTile = map.getTileAtPos(unitPos);
					if(currTile.isHealing()) {
						int healing = u.getStats()[Constants.HP] * (currTile.getHealPercent() / 100);
						u.heal(healing);
					}
					//Unit cannot die from being on a trap tile, only be left with 1 health
					if(currTile.isTrap()) {
						//TODO: Maybe change so flying units can get hurt by some traps
						if(u.getAssignedClass().getMounted() == Constants.FLYING_MOUNT) {
							continue;
						}
						int damage = (int)(u.getStats()[Constants.HP] * ((float)currTile.getDamagePercent() / 100));
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
			
			//Difficulty increase should and could be better, but time running out
			if(pa.random(100) < 100) {
				difficulty = (difficulty + 1);
				if(difficulty >= classes.getMaxStrength()) {
					difficulty = classes.getMaxStrength();
				}
				wepDifficulty = (wepDifficulty + 1);
				if(wepDifficulty >= weapons.getMaxRarity()) {
					wepDifficulty = weapons.getMaxRarity();
				}
			}
			
		}
		if(turn == Constants.ENEMY) {
			endCurrentTurn();
		}
		openMenuState = STATE_NEXT_TURN;
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
			toReturn.addToInventory(weapons.getItemObj("Healing Potion"));
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
			if(selectState == SELECT_TRADE) {
				for(Pair p : map.getReach(selectedUnit, 1, 1)) {
					if(currentPos.equals(p)) {
						Unit toTrade;
						if((toTrade = easyAccessMap[currentPos.x][currentPos.y]) != null) {
							if(toTrade.getTeam() == turn) {
								tradeMenu = new ItemMenu(pa, this, selectedUnit, cam.getTransPos());
								openMenuState = TRADE_MENU;
								otherUnit = toTrade;
							}
						}
					}
				}
			}
			if(selectState == SELECT_ATTACK) {
				for(Pair p : map.getAttackSpaces(selectedUnit)) {
					if(currentPos.equals(p)) {
						Unit toAttack;
						if((toAttack = easyAccessMap[currentPos.x][currentPos.y]) != null) {
							if(toAttack.getTeam() != turn) {
								BattleResult result = attackUnit(selectedUnit, toAttack, false);
								lastBattle = new BattleResultsMenu(pa, result, this);
								selectState = SELECT_NORMAL;
								if(!result.atkDied) {
									moveAndUpdateSelection(selectedUnit, selectedUnit.getPos());
								}
								if(result.atkDied && teams.get(turn).hasLost()) {
									endCurrentTurn();
								}
								
								if(result.atkDied && result.atk.getTeam() == Constants.ENEMY) {
									teams.get(result.def.getTeam()).addGold(100);
								}
								else if(result.defDied && result.def.getTeam() == Constants.ENEMY) {
									teams.get(result.atk.getTeam()).addGold(100);
								}
								openMenuState = STATE_BATTLE;
							}
							else if(toAttack.getTeam() == turn) {
								if(selectedUnit.getEquipped() != null && selectedUnit.getEquipped().isHealing()) {
									lastBattle = new BattleResultsMenu(pa, attackUnit(selectedUnit, toAttack, true), this);
									selectState = SELECT_NORMAL;
									moveAndUpdateSelection(selectedUnit, selectedUnit.getPos());
									openMenuState = STATE_BATTLE;
								}
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
	
	private void chooseItemToRemove() {
		
	}
	
	public void moveAndUpdateSelection(Unit chosen, Pair newPos) {
		chosen.setMoving(false);
		easyAccessMap[chosen.getPos().x][chosen.getPos().y] = null;
		chosen.setNewPos(newPos.x, newPos.y);
		chosen.setMoved(true);
		easyAccessMap[chosen.getPos().x][chosen.getPos().y] = chosen;
		
		//If the unit has moved to a spot with loot, collect it
		if(loot.containsKey(newPos)) {
			Resource obtained = loot.remove(newPos);
			teams.get(turn).addGold(obtained.getGold());
			if(obtained.hasLoot()) {
				if(!chosen.addToInventory(obtained.getLoot())) {
					//TODO: Do something? - make choose an item to remove?
				}
			}
		}
		
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
	
	public BattleObj getDamage(Unit attack, Unit defend) {
		int chanceToHitAtk;
		int critChanceAtk;
		int damageAtk;
		boolean x2Atk = false;
		
		boolean counter = false;
		int chanceToHitDef;
		int critChanceDef;
		int damageDef;
		boolean x2Def = false;
		
		Tile atkTile = map.getTileAtPos(attack.getPos());
		Tile defTile = map.getTileAtPos(defend.getPos());
		
		//Attacker
		if(attack.getEquipped() != null) {
			chanceToHitAtk = attack.getEquipped().getHitRate() - defTile.getAvoidPercent();
			critChanceAtk = (int) (attack.getEquipped().getCriticalRate() + 0.25 * attack.getStats()[Constants.DEX]);
			if(attack.getEquipped().getWeaponType() == Constants.MAGIC) {
				damageAtk = attack.getEquipped().getAttack() + attack.getStats()[Constants.MAG] - defend.getStats()[Constants.MDEF];
			}
			else if(attack.getEquipped().isHealing()) {
				damageAtk = -(attack.getEquipped().getAttack() + (int)(0.5f * attack.getStats()[Constants.MAG]));
				return new BattleObj(damageAtk, 0, chanceToHitAtk, x2Atk, false, 0, 0, 0, false);
			}
			else {
				damageAtk = attack.getEquipped().getAttack() + attack.getStats()[Constants.STR] - (defend.getStats()[Constants.DEF] + defTile.getDefUp());
			}
			
			if(attack.getStats()[Constants.SPD] >= (defend.getStats()[Constants.SPD] + 5)) {
				x2Atk = true;
				x2Def = false;
			}
		}
		else {
			chanceToHitAtk = 100;
			critChanceAtk = 0;
			damageAtk = 1 + attack.getStats()[Constants.STR] - (int)(1.5f * defend.getStats()[Constants.DEF]);
		}
		
		//Defender counter
		//Check if can counter first
		Pair[] defenderReach = map.getAttackSpaces(defend);
		for(Pair p : defenderReach) {
			if(p.equals(attack.getPos())) {
				counter = true;
				break;
			}
		}
		
		if(defend.getEquipped() != null) {
			chanceToHitDef = defend.getEquipped().getHitRate() - atkTile.getAvoidPercent(); //Maybe alter this
			critChanceDef = (int) (defend.getEquipped().getCriticalRate() + 0.25 * defend.getStats()[Constants.DEX]);
			if(defend.getEquipped().getWeaponType() == Constants.MAGIC) {
				damageDef = defend.getEquipped().getAttack() + defend.getStats()[Constants.MAG] - attack.getStats()[Constants.MDEF];
			}
			else {
				damageDef = defend.getEquipped().getAttack() + defend.getStats()[Constants.STR] - (attack.getStats()[Constants.DEF] + atkTile.getDefUp());
			}
			
			if(defend.getStats()[Constants.SPD] >= (attack.getStats()[Constants.SPD] + 5)) {
				x2Def = true;
				x2Atk = false;
			}
		}
		else {
			chanceToHitDef = 100;
			critChanceDef = 0;
			damageDef = 1 + defend.getStats()[Constants.STR] - (int)(1.5f * attack.getStats()[Constants.DEF]);
		}
		if(damageAtk < 0) {
			damageAtk = 0;
		}
		if(damageDef < 0) {
			damageDef = 0;
		}
		return new BattleObj(damageAtk, critChanceAtk, chanceToHitAtk, x2Atk, counter, damageDef, critChanceDef, chanceToHitDef, x2Def);
	}
	
	public BattleResult attackUnit(Unit attack, Unit other, boolean heal) {
		BattleResult result = new BattleResult();
		result.attacker = attack.getAssignedClass().getName();
		result.defender = other.getAssignedClass().getName();
		
		result.atk = attack;
		result.def = other;
		
		result.preStatsAtk = attack.getStats().clone();
		result.preStatsDef = other.getStats().clone();
		
		BattleObj battleResult = getDamage(attack, other);
		//Do initial attack
		if(pa.random(100) > battleResult.hitTo) {
			result.atkFirstHit = false;
			battleResult.damageTo = 0;
			//Missed message!
		}
		if(pa.random(100) < battleResult.critTo && !heal) {
			result.atkFirstCrit = true;
			battleResult.damageTo *= 3;
		}		
		other.takeDamage(battleResult.damageTo);
		attack.damageWeapon();
		result.damageDealtAtkFirst = battleResult.damageTo;
		result.expGainedAtk += (int)(battleResult.damageTo * 2.5);
		
		if(other.getHealthPoints() <= 0) {
			teams.get(other.getTeam()).removeUnit(other);
			easyAccessMap[other.getPos().x][other.getPos().y] = null;
			result.defDied = true;
			result.expGainedAtk += 150;
			result.atkLevelUp = attack.addExp(result.expGainedAtk);
			return result;
		}
		
		//If counter do other attack:
		if(battleResult.counter) {
			result.counter = true;
			result.defFirstHit = true;
			if(pa.random(100) > battleResult.hitFrom) {
				result.defFirstHit = false;
				battleResult.damageFrom = 0;
				//Missed message!
			}
			if(pa.random(100) < battleResult.critFrom) {
				result.defFirstCrit = true;
				battleResult.damageFrom *= 3;
			}		
			attack.takeDamage(battleResult.damageFrom);
			other.damageWeapon();
			result.damageDealtDefFirst = battleResult.damageTo;
			result.expGainedDef += (int)(battleResult.damageFrom * 2.5);
			
			if(attack.getHealthPoints() <= 0) {
				result.atkDied = true;
				teams.get(attack.getTeam()).removeUnit(attack);
				easyAccessMap[attack.getPos().x][attack.getPos().y] = null;
				result.expGainedDef += 150;
				result.defLevelUp = other.addExp(result.expGainedDef);
				return result;
			}
		}
		
		//Now check if anyone can attack twice
		if(battleResult.x2To) {
			result.atk2 = true;
			result.atkSecondHit = true;
			if(result.atkFirstCrit) {
				battleResult.damageTo /= 3;
			}
			if(pa.random(100) > battleResult.hitTo) {
				result.atkSecondHit = false;
				 battleResult.damageTo = 0;
				 //Missed message!
			}
			if(pa.random(100) < battleResult.critTo) {
				result.atkSecondCrit = true;
				battleResult.damageTo *= 3;
			}		
			other.takeDamage(battleResult.damageTo);
			attack.damageWeapon();
			result.damageDealtAtkSecond = battleResult.damageTo;
			result.expGainedAtk += (int)(battleResult.damageTo * 2.5);
			
			if(other.getHealthPoints() <= 0) {
				result.defDied = true;
				teams.get(other.getTeam()).removeUnit(other);
				easyAccessMap[other.getPos().x][other.getPos().y] = null;
				result.expGainedAtk += 150;
				result.atkLevelUp = attack.addExp(result.expGainedAtk);
				return result;
			}
		}
		
		if(battleResult.x2From) {
			result.def2 = true;
			result.defSecondHit = true;
			if(result.defFirstCrit) {
				battleResult.damageFrom /= 3;
			}
			if(pa.random(100) > battleResult.hitFrom) {
				result.defSecondHit = false;
				 battleResult.damageFrom = 0;
				 //Missed message!
			}
			if(pa.random(100) < battleResult.critFrom) {
				result.defSecondCrit = true;
				battleResult.damageFrom *= 3;
			}		
			attack.takeDamage(battleResult.damageFrom);
			other.damageWeapon();
			result.damageDealtDefSecond = battleResult.damageFrom;
			result.expGainedDef += (int)(battleResult.damageFrom * 2.5);
			
			if(attack.getHealthPoints() <= 0) {
				result.atkDied = true;
				teams.get(attack.getTeam()).removeUnit(attack);
				easyAccessMap[attack.getPos().x][attack.getPos().y] = null;
				result.expGainedDef += 150;
				result.defLevelUp = other.addExp(result.expGainedDef);
				return result;
			}
		}
		result.defLevelUp = other.addExp(result.expGainedDef);
		result.atkLevelUp = attack.addExp(result.expGainedAtk);
		return result;
	}
	
	public void cancelSelection() {
		if(selectedUnit != null) {
			selectedUnit.setMoving(false);
			cam.updateGridPos(selectedUnit.getPos().x, selectedUnit.getPos().y);
		}
		
		unitSelected = false;
		selectedUnit = null;
		startedTrade = false;
		openMenuState = NO_MENU;
		selectState = SELECT_NORMAL;
	}

	public void selectTrade() {
		selectState = SELECT_TRADE;
		openMenuState = NO_MENU;
	}
	
	public void addGold(int team) {
		teams.get(team).addGold(100);
	}

	public void confirmTrade(Item item, int index) {
		if(startedTrade) {
			if(tradeIndex > -1) {
				selectedUnit.removeFromInventory(tradeIndex);
			}
			if(index > -1) {
				otherUnit.removeFromInventory(index);
			}
			
			if(item != null) {
				selectedUnit.addToInventory(item);
			}
			if(tradeFirst != null) {
				otherUnit.addToInventory(tradeFirst);
			}
			cancelSelection();
		}
		else {
			tradeFirst = item;
			tradeIndex = index;
			startedTrade = true;
			tradeMenu = new ItemMenu(pa, this, otherUnit, cam.getTransPos());
		}
	}
	
	public void showPurchaseMenu() {
		openMenuState = STATE_BUY;
		buyMenu = new PurchaseMenu(pa, teams.get(turn), cam.getTransPos(), this, classes, weapons, easyAccessMap, map);
	}

	public int getWinState() {
		return winState;
	}
}