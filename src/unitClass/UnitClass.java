package unitClass;

import processing.core.PApplet;
import processing.core.PImage;

public class UnitClass {
	
	private String className;
	private String[] spritePaths;
	private PImage[] sprites;
	private int[] statGrowths;
	private int mounted;
	private int movement;
	private int[] weaponTypes;
	private int[] baseStats;
	
	//Maybe add abilities?
	
	public UnitClass(String name, String[] spriteFilePaths, int[] statGrowths, int mounted, int movement, int[] weaponTypes, final PApplet pa) {
		this.className = name;
		this.spritePaths = spriteFilePaths;
		this.statGrowths = statGrowths;
		this.mounted = mounted;
		this.movement = movement;
		this.weaponTypes = weaponTypes;
		for(int i = 0; i < 4; i++) {
			sprites[i] = pa.loadImage(spriteFilePaths[i]);
		}
	}

	//Name of the class - e.g. Mage, Dragoon etc.
	public String getName() {
		return className;
	}
	
	//Sprite to display based on controlling player
	public PImage getSprite(int player) {
		return sprites[player];
	}
	
	//Get stat growths on level up
	public int getStatGrowth(int stat) {
		return statGrowths[stat];
	}
	
	//Return mounted status:
	//0 - No mount
	//1 - Ground mount
	//2 - Flying mount
	public int getMounted() {
		return mounted;
	}
	
	//Return number of movement points - amount of spaces able to move
	public int getMovement() {
		return movement;
	}
	
	public int[] getEquipableWeapons() {
		return weaponTypes;
	}
	
	public int[] getBaseStats() {
		return baseStats;
	}
}
