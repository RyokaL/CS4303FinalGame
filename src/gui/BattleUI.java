package gui;

import constants.Constants;
import gameManager.Player;
import processing.core.PApplet;
import processing.core.PVector;

public class BattleUI {
	
	private final PApplet pa;

	public BattleUI(final PApplet pa) {
		this.pa = pa;
	}
	
	public void update(Player currPlayer) {
		pa.fill(0);
		pa.stroke(0);
		//pa.rect(0, 0, pa.width, pa.height * 0.1f);
		pa.rect(0, pa.height * 0.9f, pa.width, pa.height * 0.1f);
//		pa.rect(0, 0, pa.width * 0.1f, pa.height);
//		pa.rect(pa.width * 0.9f, 0, pa.width * 0.1f, pa.height);
		
		pa.fill(0xFFe3b73d);
		pa.text("Gold: " + currPlayer.getGold(), pa.width * 0.95f, pa.height * 0.95f);
		pa.fill(Constants.TEAM_COLOURS[currPlayer.getTeam()]);
		pa.text("Untapped: " + currPlayer.getUnitsToMove(), pa.width * 0.1f, pa.height * 0.95f);
	}
}
