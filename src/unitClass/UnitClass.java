package unitClass;

import java.io.File;

import processing.core.PApplet;
import processing.core.PImage;

public class UnitClass {
	
	private String jobName;
	private String[] spritePaths;
	private transient PImage[] sprites;
	private int[] statGrowths;
	private int mounted;
	private int movement;
	private int[] weaponTypes;
	private int[] baseStats;
	private int upgradeStage;
	private String[] upgradeList;
	private int cost;
	//Maybe add abilities?
	
	public UnitClass(String name, String[] spriteFilePaths, int[] statGrowths, int mounted, int movement, int[] weaponTypes) {
		this.jobName = name;
		this.spritePaths = spriteFilePaths;
		this.statGrowths = statGrowths;
		this.mounted = mounted;
		this.movement = movement;
		this.weaponTypes = weaponTypes;
		this.sprites = new PImage[4];
	}
	
	public void loadSprites(final PApplet pa) {
		this.sprites = new PImage[5];
		for(int i = 0; i < 5; i++) {
			File tempFile = new File(spritePaths[i]);
			if(tempFile.exists()) {
				sprites[i] = pa.loadImage(pa.sketchPath(spritePaths[i]));
			}
		}
	}

	//Name of the class - e.g. Mage, Dragoon etc.
	public String getName() {
		return jobName;
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
	
	public int getUpgradeStage() {
		return upgradeStage;
	}
	
	public String[] getUpgradeList() {
		return upgradeList;
	}
	
	public int getCost() {
		return cost;
	}
}
