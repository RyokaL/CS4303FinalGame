package playerUnits;

import java.util.Arrays;

import constants.Constants;
import helpers.Pair;
import items.Item;
import items.Weapon;
import processing.core.PApplet;
import unitClass.UnitClass;

public class Unit {

	//Maybe have name personalisation?
	//private String name;
	
	private Pair pos;
	private int[] stats;
	private int healthPoints;
	private Item[] inventory;
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
	
	private int turnTimer;
	private boolean effect = false;
	
	private int[] currentEffectedStats;
	private int[] currentEffects;
	
	public Unit(int initPosX, int initPosY, UnitClass assignedClass, int team, final PApplet pa) {
		this.pos = new Pair(initPosX, initPosY);
		this.assignedClass = assignedClass;
		this.stats = assignedClass.getBaseStats().clone();
		this.healthPoints = stats[Constants.HP];
		this.level = 1;
		this.experiencePoints = 0;
		this.pa = pa;
		this.inventory = new Item[Constants.MAX_INVENTORY];
		this.team = team;
		movePos = new Pair(initPosX, initPosY);
	}
	
	public void updateEffects() {
		if(effect) {
			turnTimer -= 1;
			if(turnTimer <= 0) {
				int j = 0;
				for(int i : currentEffectedStats) {
					stats[i] -= currentEffects[j];
					if(i == Constants.HP) {
						healthPoints -= currentEffects[j];
						if(healthPoints < 0) {
							healthPoints = 1;
						}
					}
					j++;
				}
				effect = false;
			}
		}
	}
	
	public int addExp(int exp) {
		if(team == Constants.ENEMY) {
			return 0;
		}
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
	
	public boolean equipWeapon(Weapon toEquip) {
		boolean equippable = false;
		if(toEquip != null) {
			int[] types = assignedClass.getEquipableWeapons();
			for(int i = 0; i < types.length; i++) {
				if(toEquip.getWeaponType() == types[i]) {
					equippable = true;
					break;
				}
			}	
		}
		else {
			equippable = true;
		}
		if(equippable) {
			equipped = toEquip;
		}
		return equippable;
	}
	
	public Pair[] getMoveableSpaces() {
		return moveableSpaces;
	}
	
	public boolean hasEffect() {
		return effect;
	}
	
	private void levelUp() {
		level += 1;
		for(int i = 0; i < stats.length; i++) {
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
	
	public boolean addToInventory(Item toAdd) {
		if(inventorySize >= Constants.MAX_INVENTORY) {
			return false;
		}
		else {
			if(inventorySize == -1) {
				inventorySize += 1;
			}
			inventory[inventorySize] = toAdd;
			inventorySize += 1;
			return true;
		}
	}
	
	private void resortInventory() {
		int nullIndex = 0;
		boolean found = false;
		for(int i = 0; i < inventory.length; i++) {
			if(found) {
				inventory[nullIndex] = inventory[i];
				inventory[i] = null;
				break;
			}
			if(inventory[i] == null) {
				nullIndex = i;
				found = true;
			}
		}
	}
	
	public void removeFromInventory(int index) {
		inventorySize -= 1;
		inventory[index] = null;
		resortInventory();
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

	public Item[] getInventory() {
		return inventory;
	}
	
	public int getInventorySize() {
		return inventorySize;
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
	
	public void applyEffect(int[] statsAffected, int[] effects, int time) {
		int j = 0;
		for(int i : statsAffected) {
			if(i == Constants.HP && time == -1) {
				stats[i] += effects[j];
				healthPoints += effects[j];
			}
			else if (i == Constants.HP && time == 0) {
				healthPoints += effects[j];
				if(healthPoints >= stats[Constants.HP]) {
					healthPoints = stats[Constants.HP];
				}
			}
			else {
				stats[i] += effects[j];
			}
			j++;
		}
		if(time > 0) {
			//Set timer
			currentEffectedStats = statsAffected;
			currentEffects = effects;
			turnTimer = time;
			effect = true;
		}
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

	public void damageWeapon() {
		if(equipped == null) {
			return;
		}
		if(team == Constants.ENEMY) {
			return;
		}
		equipped.damage();
		if(equipped.getDurability() <= 0) {
			boolean found = false;
			for(int i = 0; i < inventory.length; i++) {
				if(inventory[i] instanceof Weapon) {
					equipped = (Weapon)inventory[i];
					removeFromInventory(i);
					found = true;
					break;
				}
			}
			if(!found) {
				equipped = null;
			}
		}
	}
	
	public String toString() {
		String info = "Str: " + stats[Constants.STR] + "\n"
				+ "Mag: " + stats[Constants.MAG] + "\n"
				+ "Def: " + stats[Constants.DEF] + "\n"
				+ "MDef: " + stats[Constants.MDEF] + "\n"
				+ "Dex: " + stats[Constants.DEX] + "\n"
				+ "Spd: " + stats[Constants.SPD] + "\n";
		info = info + "Weapons: \n";
		for(int i : assignedClass.getEquipableWeapons()) {
			info = info + Constants.WEAPON_TYPES[i] + "\n";
		}
		return info;
	}
}