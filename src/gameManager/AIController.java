package gameManager;

import java.util.ArrayList;

import helpers.BattleObj;
import helpers.EnemyMove;
import helpers.Pair;
import items.Item;
import items.Weapon;
import map.Map;
import playerUnits.Unit;

public class AIController {

	private final int scoreThres = -500; //How risky will the AI play
	
	public AIController() {
		
	}
	
	public void getNextMove(Unit currUnit, Unit[][] easyAccessMap, Map gameMap, Game game) {
		//TODO: Allow enemy to heal other enemy units and itself
		//Get movement spaces
		Pair[] movementSpaces = gameMap.getMovementSpaces(currUnit, easyAccessMap);
		currUnit.setMoving(true);
		//Check if any units are within attack range of movement spaces
		ArrayList<EnemyMove> attackSpaces = new ArrayList<EnemyMove>();
		Weapon currEquipped = currUnit.getEquipped();
		Item[] enemyInv = currUnit.getInventory();
		for(int j = -1; j < currUnit.getInventorySize(); j++) {
			if(j > -1) {
				if(enemyInv[j] instanceof Weapon) {
					currUnit.equipWeapon((Weapon)enemyInv[j]);
				}
			}
			Weapon currentWep = currUnit.getEquipped();
			for(int i = 0; i < movementSpaces.length; i++) {
				Pair[] attackSpacesFromSpace = gameMap.getReachFromPos(movementSpaces[i], currentWep.getMinRange(), currentWep.getMaxRange());
				for(Pair p : attackSpacesFromSpace) {
					Unit atSpace;
					if(p.x < easyAccessMap.length && p.y < easyAccessMap[0].length) {
						atSpace = easyAccessMap[p.x][p.y];
						if(atSpace != null && atSpace.getTeam() != currUnit.getTeam()) {
							attackSpaces.add(new EnemyMove(j, p, movementSpaces[i]));
						}
					}
				}
			}
		}
		//Reset equipped weapon so we can mess with inventory properly later
		currUnit.equipWeapon(currEquipped);
		
		//If so, pick the highest damage dealt, least damage received and attack
		if(!attackSpaces.isEmpty()) {
			EnemyMove bestMove = null;
			float highestScoringSpace = scoreThres; //Spaces are scored by damageDealt * hitChance/100 - damageReceived * enemyHitChance/100;
			for(EnemyMove e : attackSpaces) {
				if(e.currEquipIndex > 0) {
					currUnit.equipWeapon((Weapon)enemyInv[e.currEquipIndex]);
				}
				else {
					currUnit.equipWeapon(currEquipped);
				}
				currUnit.setMovePos(e.posToMove.x, e.posToMove.y);
				BattleObj moveResult = game.getDamage(currUnit, easyAccessMap[e.posToAttack.x][e.posToAttack.y]);
				float score = getUnitAttackScore(moveResult);
				
				System.out.println("[" + e.posToMove.x + " , " + e.posToMove.y + "], [" + e.posToAttack.x + " , " + e.posToAttack.y + "] :: " + e.currEquipIndex);
				
				if(score > highestScoringSpace) {
					highestScoringSpace = score;
					bestMove = e;
				}
			}
			currUnit.setMoving(false);
			//Again reset equipped weapon for inventory messing
			currUnit.equipWeapon(currEquipped);
			if(bestMove != null) {
				if(bestMove.currEquipIndex != -1) {
					Item toReAdd = currUnit.getEquipped();
					currUnit.equipWeapon((Weapon)enemyInv[bestMove.currEquipIndex]);
					currUnit.removeFromInventory(bestMove.currEquipIndex);
					currUnit.addToInventory(toReAdd);
				}
				game.attackUnit(currUnit, easyAccessMap[bestMove.posToAttack.x][bestMove.posToAttack.y], false);
				//TODO: Show battle result menu
				game.moveAndUpdateSelection(currUnit, bestMove.posToMove);
				return;
			}
		}
		else {
			//Otherwise pick weakest unit and move as close as possible - map tile in movement with shortest distance to unit
			float highestScore = scoreThres;
			Unit toMoveTo = null;
			for(Unit[] uArray : easyAccessMap) {
				for(Unit u : uArray) {
					if(u == null) {
						continue;
					}
					float score = getUnitAttackScore(game.getDamage(currUnit, u));
					if(score > highestScore) {
						toMoveTo = u;
					}
				}
			}
			if(toMoveTo != null) {
				Pair unitPos = toMoveTo.getPos();
				int currDis = Math.abs(currUnit.getPos().x - unitPos.x) + Math.abs(currUnit.getPos().y - unitPos.y);
				Pair bestPos = null;
				for(Pair p : movementSpaces) {
					int newDis = Math.abs(p.x - unitPos.x) + Math.abs(p.y - unitPos.y);
					if(newDis < currDis) {
						currDis = newDis;
						bestPos = p;
					}
				}
				if(bestPos != null) {
					game.moveAndUpdateSelection(currUnit, bestPos);
				}
				else {
					System.out.println("wot");
				}
			}
			else {
				game.moveAndUpdateSelection(currUnit, currUnit.getPos());
			}
		}
	}
	
	private float getUnitAttackScore(BattleObj moveResult) {
		float score = 0;
		if(moveResult.x2To) {
			score += (moveResult.damageTo * 2) * Math.pow(((float)(moveResult.hitTo)/100), 2);
		}
		else {
			score += (moveResult.damageTo) * ((float)(moveResult.hitTo)/100);
		}
		if(moveResult.x2From) {
			score -= (moveResult.damageFrom * 2) * Math.pow(((float)(moveResult.hitFrom)/100), 2); 
		}
		else {
			if(!moveResult.counter) {
				score += moveResult.damageTo;
			}
			else {
				score -= (moveResult.damageFrom) * ((float)(moveResult.hitTo)/100);
			}
		}
		return score;
	}
}
