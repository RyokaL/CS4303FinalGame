package map;

import helpers.Pair;
import items.Weapon;

public class Resource {

	private int gold;
	private Pair gridPos;
	
	private Weapon loot;
	
	public Resource(int goldAwarded, int gridPosX, int gridPosY) {
		this.gold = goldAwarded;
		this.gridPos = new Pair(gridPosX, gridPosY);
	}
	
	public Resource(int goldAwarded, int gridPosX, int gridPosY, Weapon loot) {
		this.gold = goldAwarded;
		this.gridPos = new Pair(gridPosX, gridPosY);
		this.loot = loot;
	}
	
	public int getGold() {
		return gold;
	}
	
	public Pair getPos() {
		return gridPos;
	}
	
	public boolean hasLoot() {
		return (loot != null);
	}
	
	public Weapon getLoot() {
		return loot;
	}
}
