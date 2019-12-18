package helpers;

import playerUnits.Unit;

public class BattleResult {

	public String attacker;
	public String defender;
	
	public Unit atk;
	public Unit def;
	
	public int[] preStatsAtk;
	public int[] preStatsDef;
	
	public int damageDealtAtkFirst = 0;
	public int damageDealtAtkSecond = 0;
	
	public int damageDealtDefFirst = 0;
	public int damageDealtDefSecond = 0;
	
	public int expGainedAtk = 0;
	public int expGainedDef = 0;
	
	public int atkLevelUp;
	public int defLevelUp;
	
	public boolean counter;
	
	public boolean atkFirstHit = true;
	public boolean atkFirstCrit = false;
	public boolean atkSecondHit = false;
	public boolean atkSecondCrit = false;
	
	public boolean atk2 = false;
	
	public boolean defFirstHit = false;
	public boolean defFirstCrit = false;
	public boolean defSecondHit = false;
	public boolean defSecondCrit = false;
	
	public boolean def2 = false;
	
	public boolean atkDied = false;
	public boolean defDied = false;
	
	public BattleResult() {
	}
}
