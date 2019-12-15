package weapons;

public class Weapon implements Cloneable {
	
	private String name;
	private int attack;
	private int hitRate;
	private int critRate;
	private int maxRange;
	private int minRange;
	private int durability;
	private boolean healing;
	private int weaponType;
	private int rarity;

	public Weapon(String name, int attack, int hitRate, int critRate, int maxRange, int minRange, int durability,
			boolean healing, int weaponType, int rarity) {
		this.name = name;
		this.attack = attack;
		this.hitRate = hitRate;
		this.critRate = critRate;
		this.maxRange = maxRange;
		this.minRange = minRange;
		this.durability = durability;
		this.healing = healing;
		this.weaponType = weaponType;
		this.rarity = rarity;
	}

	public String getName() {
		return name;
	}
	
	public int getAttack() {
		return attack;
	}
	
	public int getHitRate() {
		return hitRate;
	}
	
	public int getCriticalRate() {
		return critRate;
	}
	
	//Get max and min range. They may be the same which means the weapon can only hit that range and not in-between
	//e.g. Bows can only hit 2 spaces away, not directly adjacent spaces.
	public int getMaxRange() {
		return maxRange;
	}
	public int getMinRange() {
		return minRange;
	}
	
	public int getDurability() {
		return durability;
	}
	
	public boolean isHealing() {
		return healing;
	}
	
	public int getWeaponType() {
		return weaponType;
	}
	
	public int getRarity() {
		return rarity;
	}
	
	public Weapon clone() throws CloneNotSupportedException {
		return (Weapon) super.clone();
	}
}
