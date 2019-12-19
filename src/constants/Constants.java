package constants;


public final class Constants {
	public static final int STATE_MAIN_MENU = 0;
	public static final int STATE_POST_MAIN = 1;
	public static final int STATE_GAME = 2;
	public static final int STATE_END = 3;
	
	public static final int NO_MOUNT = 0;
	public static final int GROUND_MOUNT = 1;
	public static final int FLYING_MOUNT = 2;
	
	public static final int AFFECT_ALL = 3;
	public static final int AFFECT_MOUNTED = 4;
	public static final int AFFECT_GROUND_UNITS = 5;
	public static final int NO_UNITS = -1;
	
	public static final int HP = 0;
	public static final int STR = 1;
	public static final int MAG = 2;
	public static final int DEF = 3;
	public static final int MDEF = 4;
	public static final int SPD = 5;
	public static final int DEX = 6;
	
	public static final int SWORDS = 0;
	public static final int LANCES = 1;
	public static final int AXES = 2;
	public static final int BOWS = 3;
	public static final int MAGIC = 4;
	public static final int HEAL = 5;
	public static final int BEAST = 6;
	
	public static final int MAX_INVENTORY = 5;
	
	public static final int RED = 0;
	public static final int BLUE = 1;
	public static final int GREEN = 2;
	public static final int YELLOW = 3;
	public static final int ENEMY = 4;
	
	public static final int[] TEAM_COLOURS = {0xFFe30000, 0xFF0000c9, 0xFF00c900, 0xFFf7f700, 0xFF7f009c, 0x00FFFFFF};
	public static final String[] WEAPON_TYPES = {"Sword", "Lance", "Axe", "Bow", "Magic", "Heal", "Beast"};
	public static final String[] STATS = {"HP", "Str" , "Mag", "Def", "MDef", "SPD", "DEX"};
	
	private Constants() {
		throw new IllegalStateException();
	}
}
