package playerUnits;

import java.util.ArrayList;

import constants.Constants;
import helpers.Pair;
import processing.core.PApplet;
import processing.core.PVector;
import unitClass.UnitClass;
import weapons.Weapon;

public class Unit {

	//Maybe have name personalisation?
	//private String name;
	
	private Pair pos;
	private int[] stats;
	private int healthPoints;
	private Weapon[] inventory;
	private int inventorySize;
	private Weapon equipped;
	private UnitClass assignedClass;
	private int level;
	private int experiencePoints;
	private PApplet pa;
	
	private Pair[] moveableSpaces;
	
	public Unit(int initPosX, int initPosY, UnitClass assignedClass, final PApplet pa) {
		this.pos = new Pair(initPosX, initPosY);
		this.assignedClass = assignedClass;
		this.stats = assignedClass.getBaseStats().clone();
		this.healthPoints = stats[Constants.HP];
		this.level = 1;
		this.experiencePoints = 0;
		this.pa = pa;
		this.inventory = new Weapon[Constants.MAX_INVENTORY];
	}
	
	public void addExp(int exp) {
		experiencePoints += exp;
		if(experiencePoints >= 100) {
			experiencePoints -= 100;
			levelUp();
		}
	}
	
	public void setSpacesNewTurn(Pair[] newSpaces) {
		moveableSpaces = newSpaces;
	}
	
	public void equipWeapon(Weapon toEquip) {
		equipped = toEquip;
	}
	
	public Pair[] getMoveableSpaces() {
		return moveableSpaces;
	}
	
	private void levelUp() {
		level += 1;
		for(int i = 0; i > stats.length; i++) {
			if(pa.random(100) < assignedClass.getStatGrowth(i)) {
				stats[i] = stats[i] + 1;
			}
		}
	}
	
	public Pair getPos() {
		return pos;
	}

	public int[] getStats() {
		return stats;
	}
	
	public void takeDamage(int damage) {
		healthPoints -= damage;
		if(healthPoints < 0) {
			healthPoints = 0;
		}
	}

	public int getHealthPoints() {
		return healthPoints;
	}

	public Weapon[] getInventory() {
		return inventory;
	}

	public Weapon getEquipped() {
		return equipped;
	}

	public UnitClass getAssignedClass() {
		return assignedClass;
	}

	public int getLevel() {
		return level;
	}
}