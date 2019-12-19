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
	private int defUp;
	private int avoidPercent;
	
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
				details.append("no units. \n");
				break;
			case Constants.NO_MOUNT:
				details.append("non-mounted units only. \n");
				break;
			case Constants.GROUND_MOUNT:
				details.append("ground mounted units only. \n");
				break;
			case Constants.FLYING_MOUNT:
				details.append("flying units only. \n");
				break;
			case Constants.AFFECT_ALL:
				details.append("all units. \n");
				break;
			case Constants.AFFECT_MOUNTED:
				details.append("mounted units only. \n");
				break;
			case Constants.AFFECT_GROUND_UNITS:
				details.append("ground units only. \n");
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
				details.append("Flying units suffer ");
				break;
			case Constants.AFFECT_ALL:
				details.append("All units suffer ");
				break;
			case Constants.AFFECT_MOUNTED:
				details.append("Mounted units suffer ");
				break;
			case Constants.AFFECT_GROUND_UNITS:
				details.append("Ground units suffer ");
				break;
		}
		if(!(affectedUnits == Constants.NO_UNITS)) {
			details.append(-affectMove + " movement.\n");
		}
		
		if(heal) {
			details.append("Units recieve " + healPercent + "% healing.\n");
		}
		
		if(trap) {
			details.append("Units take " + damagePercent + "% damage.\n");
		}
		
		if(defUp > 0) {
			details.append("DEF +" + defUp + "  ");
		}
		
		if(avoidPercent > 0) {
			details.append("AVO +" + avoidPercent + "  ");
		}
		
		tileDetail = details.toString();
		return tileDetail;
	}
	public int getDefUp() {
		return defUp;
	}
	public int getAvoidPercent() {
		return avoidPercent;
	}
}
