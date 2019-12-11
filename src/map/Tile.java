package map;

import constants.Constants;

public class Tile {

	private String name;
	private int affectMove;
	//What units will be affected by this tile
	private int affectedUnits;
	//What units can use this tile
	private int unitsCanOccupy;
	private boolean heal;
	private int healPercent;
	private boolean trap;
	private int damagePercent;
	
	private transient String tileDetail = null;
	
	
	public int getAffectMove() {
		return affectMove;
	}
	public int getAffectedUnits() {
		return affectedUnits;
	}
	public int getUnitsCanOccupy() {
		return unitsCanOccupy;
	}
	public String getName() {
		return name;
	}
	
	public boolean isHealing() {
		return heal;
	}
	
	public int getHealPercent() {
		return healPercent;
	}
	
	public boolean isTrap() {
		return trap;
	}
	
	public int getDamagePercent() {
		return damagePercent;
	}
	
	public String toString() {
		if(tileDetail != null) {
			return tileDetail;
		}
		
		StringBuilder details = new StringBuilder();
		details.append("Usable by ");
		switch (unitsCanOccupy) {
			case Constants.NO_UNITS:
				details.append("no units.");
				break;
			case Constants.NO_MOUNT:
				details.append("non-mounted units only. ");
				break;
			case Constants.GROUND_MOUNT:
				details.append("ground mounted units only. ");
				break;
			case Constants.FLYING_MOUNT:
				details.append("flying units only. ");
				break;
			case Constants.AFFECT_ALL:
				details.append("all units. ");
				break;
			case Constants.AFFECT_MOUNTED:
				details.append("mounted units only. ");
				break;
			case Constants.AFFECT_GROUND_UNITS:
				details.append("ground units only. ");
				break;
		}
		
		switch (affectedUnits) {
			case Constants.NO_UNITS:
				break;
			case Constants.NO_MOUNT:
				details.append("Non-mounted units suffer ");
				break;
			case Constants.GROUND_MOUNT:
				details.append("Ground mounted units suffer ");
				break;
			case Constants.FLYING_MOUNT:
				details.append("flying units suffer ");
				break;
			case Constants.AFFECT_ALL:
				details.append("all units suffer ");
				break;
			case Constants.AFFECT_MOUNTED:
				details.append("mounted units suffer ");
				break;
			case Constants.AFFECT_GROUND_UNITS:
				details.append("ground units suffer ");
				break;
		}
		if(!(affectedUnits == Constants.NO_UNITS)) {
			details.append(affectMove + "movement.");
		}
		
		if(heal) {
			details.append("Units recieve " + healPercent + "% healing.");
		}
		
		if(trap) {
			details.append("Units take " + damagePercent + "% damage.");
		}
		
		tileDetail = details.toString();
		return tileDetail;
	}
}
