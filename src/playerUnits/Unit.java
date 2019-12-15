package playerUnits;

import java.util.Arrays;

import constants.Constants;
import helpers.Pair;
import processing.core.PApplet;
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
	private int team;
	private boolean moved = false;
	
	private boolean moving = false;
	private Pair movePos;
	
	private Pair[] moveableSpaces;
	
	public Unit(int initPosX, int initPosY, UnitClass assignedClass, int team, final PApplet pa) {
		this.pos = new Pair(initPosX, initPosY);
		this.assignedClass = assignedClass;
		this.stats = assignedClass.getBaseStats().clone();
		this.healthPoints = stats[Constants.HP];
		this.level = 1;
		this.experiencePoints = 0;
		this.pa = pa;
		this.inventory = new Weapon[Constants.MAX_INVENTORY];
		this.team = team;
		movePos = new Pair(initPosX, initPosY);
	}
	
	public int addExp(int exp) {
		int levels = 0;
		experiencePoints += exp;
		while(experiencePoints >= 100) {
			experiencePoints -= 100;
			levelUp();
			levels += 1;
		}
		return levels;
	}
	
	public int getTeam() {
		return team;
	}
	
	public boolean hasMoved() {
		return moved;
	}
	
	public boolean isMoving() {
		return moving;
	}
	
	public void setMoved(boolean value) {
		moved = value;
	}
	
	public void setMoving(boolean value) {
		moving = value;
	}
	
	public void setNewPos(int newX, int newY) {
		pos.x = newX;
		pos.y = newY;
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
	
	public void setMovePos(int newX, int newY) {
		movePos.x = newX;
		movePos.y = newY;
	}
	
	public Pair getPos() {
		if(moving) {
			return movePos;
		}
		return pos;
	}

	public int[] getStats() {
		return stats;
	}
	
	public boolean addToInventory(Weapon toAdd) {
		if(inventorySize >= Constants.MAX_INVENTORY) {
			return false;
		}
		else {
			inventory[inventorySize] = toAdd;
			inventorySize += 1;
			return true;
		}
	}
	
	public void takeDamage(int damage) {
		healthPoints -= damage;
		if(healthPoints < 0) {
			healthPoints = 0;
		}
	}
	
	public void heal(int healing) {
		healthPoints += healing;
		if(healthPoints > stats[Constants.HP]) {
			healthPoints = stats[Constants.HP];
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + experiencePoints;
		result = prime * result + healthPoints;
		result = prime * result + level;
		result = prime * result + (moved ? 1231 : 1237);
		result = prime * result + (moving ? 1231 : 1237);
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + Arrays.hashCode(stats);
		result = prime * result + team;
		return result;
	}

	@Override
	//Auto-generated
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Unit other = (Unit) obj;
		if (experiencePoints != other.experiencePoints)
			return false;
		if (healthPoints != other.healthPoints)
			return false;
		if (level != other.level)
			return false;
		if (moved != other.moved)
			return false;
		if (moving != other.moving)
			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (!Arrays.equals(stats, other.stats))
			return false;
		if (team != other.team)
			return false;
		return true;
	}
}