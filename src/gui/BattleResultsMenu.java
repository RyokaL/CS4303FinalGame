package gui;

import constants.Constants;
import gameManager.Game;
import helpers.BattleResult;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class BattleResultsMenu {
	private final int STATE_BATTLE = 0;
	private final int STATE_ATK_LEVEL = 1;
	private final int STATE_DEF_LEVEL = 2;
	
	private String atkFirstHit = "";
	private String atkSecondHit = "";
	
	private String defFirstHit = "";
	private String defSecondHit = "";
	
	private String atkDead = "";
	private String defDead = "";
	
	private String atkLevelUp = "";
	private String atkStatChanges = "";
	
	private String defLevelUp = "";
	private String defStatChanges = "";
	
	private boolean levelUpAtk;
	private boolean levelUpDef;
	
	private int state = STATE_BATTLE;
	
	private final PApplet pa;
	private Game returnGame;

	public BattleResultsMenu(final PApplet pa, BattleResult result, Game returnGame) {
		this.returnGame = returnGame;
		this.pa = pa;
		
		if(result.atkLevelUp > 0) {
			levelUpAtk = true;
			int preLevel = result.atk.getLevel() - result.atkLevelUp;
			atkLevelUp = result.attacker + " levelled up!\nlv. " + preLevel + " -> lv. " + result.atk.getLevel();
			int[] atkStats = result.preStatsAtk;
			int[] newStats = result.atk.getStats();
			atkStatChanges = "HP: " + atkStats[Constants.HP] + " -> " + newStats[Constants.HP] + "\n"
						   + "Str: " + atkStats[Constants.STR] + " -> " + newStats[Constants.STR] + "\n"
						   + "Mag: " + atkStats[Constants.MAG] + " -> " + newStats[Constants.MAG] + "\n"
						   + "Def: " + atkStats[Constants.DEF] + " -> " + newStats[Constants.DEF] + "\n"
						   + "MDef: " + atkStats[Constants.MDEF] + " -> " + newStats[Constants.MDEF] + "\n"
						   + "Dex: " + atkStats[Constants.DEX] + " -> " + newStats[Constants.DEX] + "\n"
						   + "Spd: " + atkStats[Constants.SPD] + " -> " + newStats[Constants.SPD];
					
		}
		if(result.defLevelUp > 0) {
			levelUpDef = true;
			int preLevel = result.def.getLevel() - result.defLevelUp;
			defLevelUp = result.defender + " levelled up!\nlv. " + preLevel + " -> lv. " + result.def.getLevel();
			int[] defStats = result.preStatsDef;
			int[] newStats = result.atk.getStats();
			defStatChanges = "HP: " + defStats[Constants.HP] + " -> " + newStats[Constants.HP] + "\n"
						   + "Str: " + defStats[Constants.STR] + " -> " + newStats[Constants.STR] + "\n"
						   + "Mag: " + defStats[Constants.MAG] + " -> " + newStats[Constants.MAG] + "\n"
						   + "Def: " + defStats[Constants.DEF] + " -> " + newStats[Constants.DEF] + "\n"
						   + "MDef: " + defStats[Constants.MDEF] + " -> " + newStats[Constants.MDEF] + "\n"
						   + "Dex: " + defStats[Constants.DEX] + " -> " + newStats[Constants.DEX] + "\n"
						   + "Spd: " + defStats[Constants.SPD] + " -> " + newStats[Constants.SPD];
		}
		//TODO: pretty printing if bothered
		if(result.atkFirstHit) {
			if(result.atkFirstCrit) {
				atkFirstHit = result.attacker + " lands a critical blow on " + result.defender + " for " + result.damageDealtAtkFirst + " points of damage!";
			}
			else {
				if(result.damageDealtAtkFirst < 0) {
					atkFirstHit = result.attacker + " heals ally " + result.defender + " by " + (-result.damageDealtAtkFirst) + " points!";
				}
				else {
					atkFirstHit = result.attacker + " hits " + result.defender + " for " + result.damageDealtAtkFirst + " points of damage!";
				}
			}
		}
		else {
			atkFirstHit = result.attacker + " misses their mark!";
		}
		
		if(result.counter) {
			if(result.defFirstHit) {
				if(result.defFirstCrit) {
					defFirstHit = result.defender + " counters and gets a lucky blow on " + result.attacker + " for " + result.damageDealtDefFirst + " points of damage!";
				}
				else {
					defFirstHit = result.defender + " counters " + result.attacker + "'s attack for " + result.damageDealtDefFirst + " points of damage!";
				}
			}
			else {
				defFirstHit = result.attacker + " dodges " + result.defender + " counter-attack gracefully!";
			}
		}
		
		if(result.atk2) {
			if(result.atkSecondHit) {
				if(result.atkSecondCrit) {
					atkSecondHit = result.attacker + " strikes again and deals a critical blow to  " + result.defender + " for " + result.damageDealtAtkSecond + " points of damage!";
				}
				else {
					atkSecondHit = result.attacker + " follows up their attack on " + result.defender + " for " + result.damageDealtAtkSecond + " points of damage!";
				}
			}
			else {
				atkSecondHit = result.attacker + " misses their mark!";
			}
		}

		if(result.def2) {
			if(result.defSecondHit) {
				if(result.defSecondCrit) {
					defSecondHit = result.defender + " strikes again with a lucky blow on  " + result.attacker + " for " + result.damageDealtDefSecond + " points of damage!";
				}
				else {
					defSecondHit = result.defender + " follows up their counter on " + result.attacker + " for " + result.damageDealtDefSecond + " points of damage!";
				}
			}
			else {
				defSecondHit = result.attacker + " dodges the attack!";
			}
		}
		
		if(result.atkDied) {
			levelUpAtk = false;
			atkDead = result.attacker + " succumbed to their wounds...";
		}
		
		if(result.defDied) {
			levelUpDef = false;
			defDead = result.defender + " was slain in battle by " + result.attacker + "!";
		}
	}
	
	public void update(PVector currTrans) {
		pa.pushMatrix();
		pa.translate(-currTrans.x, -currTrans.y);
		
		pa.fill(0, 250);
		pa.rect(0,0, pa.width, pa.height);
		
		pa.fill(255);
		pa.textAlign(PConstants.CENTER);
		
		if(state == STATE_BATTLE) {
			pa.text(atkFirstHit, pa.width/2, pa.height * 0.1f);
			pa.text(defFirstHit, pa.width/2, pa.height * 0.2f);
			pa.text(atkSecondHit, pa.width/2, pa.height * 0.3f);
			pa.text(defSecondHit, pa.width/2, pa.height * 0.4f);
			pa.text(atkDead, pa.width/2, pa.height * 0.5f);
			pa.text(defDead, pa.width/2, pa.height * 0.6f);
		}
		if(state == STATE_ATK_LEVEL) {
			pa.text(atkLevelUp, pa.width/2, pa.height * 0.1f);
			pa.text(atkStatChanges, pa.width/2, pa.height * 0.3f);
		}
		if(state == STATE_DEF_LEVEL) {
			pa.text(defLevelUp, pa.width/2, pa.height * 0.1f);
			pa.text(defStatChanges, pa.width/2, pa.height * 0.3f);
		}
		
		pa.popMatrix();
		
		if(pa.keyPressed) {
			pa.keyPressed = false;
			if(pa.key == 'q') {
				if(levelUpAtk) {
					levelUpAtk = false;
					state = STATE_ATK_LEVEL;
				}
				else if(levelUpDef) {
					levelUpDef = false;
					state = STATE_DEF_LEVEL;
				}
				else {
					returnGame.cancelSelection();
				}
			}
		}
	}
}
