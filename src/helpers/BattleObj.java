package helpers;

public class BattleObj {

	public int damageTo;
	public int critTo;
	public int hitTo;
	public boolean x2To;
	
	public boolean counter;
	public int damageFrom;
	public int critFrom;
	public int hitFrom;
	public boolean x2From;
	
	public BattleObj(int damageTo, int critTo, int hitTo, boolean x2To, boolean counter, int damageFrom, int critFrom, int hitFrom, boolean x2From) {
		this.damageTo = damageTo;
		this.critTo = critTo;
		this.hitTo = hitTo;
		this.x2To = x2To;
		this.counter = counter;
		this.damageFrom = damageFrom;
		this.critFrom = critFrom;
		this.hitFrom = hitFrom;
		this.x2From = x2From;
	}
}
