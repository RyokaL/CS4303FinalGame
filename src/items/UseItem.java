package items;

import constants.Constants;

public class UseItem implements Item {
	
	private String name;
	private int[] statsAffected;
	private int[] statEffects;
	private int durability;
	private transient int maxDurability;
	private int effectTime; //-1 means permanent, 0 is reserved for HP and will apply on current HP only
	private int rarity;
	private int cost;
	
	public UseItem(UseItem toCopy) {
		this.name = toCopy.getName();
		this.statsAffected = toCopy.getAffectedStats();
		this.statEffects = toCopy.getEffects();
		this.durability = toCopy.getDurability();
		this.effectTime = toCopy.getEffectTime();
		this.rarity = toCopy.getRarity();
		this.cost = toCopy.getCost();
		maxDurability = durability;
	}
	
	public int getCost() {
		return cost;
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
	
	public String toString() {
		String info = "";
		for(int i = 0; i < statsAffected.length; i++) {
			info = info + Constants.STATS[statsAffected[i]] + ": + " + statEffects[i] + "\n";
		}
		info = info + "Durability: " + durability + "/" + maxDurability;
		return info;
	}

	@Override
	public int getRarity() {
		return rarity;
	}
}
