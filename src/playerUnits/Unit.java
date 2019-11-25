package playerUnits;

import constants.Contstants;
import processing.core.PApplet;
import processing.core.PVector;
import unitClass.UnitClass;
import weapons.Weapon;

public class Unit {

	//Maybe have name personalisation?
	//private String name;
	
	private int posX;
	private int posY;
	private int[] stats;
	private int healthPoints;
	private Weapon[] inventory;
	private Weapon equipped;
	private UnitClass assignedClass;
	private int level;
	private int experiencePoints;
	private PApplet pa;
	
	public Unit(int initPosX, int initPosY, UnitClass assignedClass, final PApplet pa) {
		this.posX = initPosX;
		this.posY = initPosY;
		this.assignedClass = assignedClass;
		this.stats = assignedClass.getBaseStats().clone();
		this.healthPoints = stats[Contstants.HP];
		this.level = 1;
		this.experiencePoints = 0;
		this.pa = pa;
	}
	
	public void addExp(int exp) {
		experiencePoints += exp;
		if(experiencePoints >= 100) {
			experiencePoints -= 100;
			levelUp();
		}
	}
	
	private void levelUp() {
		level += 1;
		for(int i = 0; i > stats.length; i++) {
			if(pa.random(100) < assignedClass.getStatGrowth(i)) {
				stats[i] = stats[i] + 1;
			}
		}
	}
	
	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int[] getStats() {
		return stats;
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
