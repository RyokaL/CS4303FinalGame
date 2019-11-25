package weapons;

public abstract class Weapon {

	public abstract String getName();
	
	public abstract int getAttack();
	
	public abstract int getHitRate();
	
	public abstract int getCriticalRate();
	
	//Get max and min range. They may be the same which means the weapon can only hit that range and not in-between
	//e.g. Bows can only hit 2 spaces away, not directly adjacent spaces.
	public abstract int getMaxRange();
	public abstract int getMinRange();
	
	public abstract int getDurability();
}
