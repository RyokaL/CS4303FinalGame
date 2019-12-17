package items;

public class UseItem implements Item {
	
	private String name;
	private int[] statsAffected;
	private int[] statEffects;
	private int durability;
	private int effectTime; //-1 means permanent, 0 is reserved for HP and will apply on current HP only
	private int rarity;
	
	public UseItem(UseItem toCopy) {
		this.name = toCopy.getName();
		this.statsAffected = toCopy.getAffectedStats();
		this.statEffects = toCopy.getEffects();
		this.durability = toCopy.getDurability();
		this.effectTime = toCopy.getEffectTime();
		this.rarity = toCopy.getRarity();
	}
	
	public int[] getAffectedStats() {
		return statsAffected;
	}
	
	public int[] getEffects() {
		return statEffects;
	}
	
	public int getEffectTime() {
		return effectTime;
	}
	
	public int getDurability() {
		return durability;
	}
	
	public void damage() {
		durability -= 1;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getRarity() {
		return rarity;
	}
}
