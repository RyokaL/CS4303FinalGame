package gui;

import gameManager.Game;
import gameManager.Player;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class BattleMenu {
	private final int ACTION_BUTTONS = 5;
	private final int ACTION_UNITS = 0;
	private final int ACTION_ADD_UNITS = 1;
	private final int ACTION_INVENTORY = 2;
	private final int ACTION_END_TURN = 3;
	private final int ACTION_SUSPEND = 4;

	private final PApplet pa;
	
	private Game returnGame;
	
	private Player currentPlayer;
	
	private Button[] actions;
	private int selectedIndex;
	
	private BattleUI extraInfo;
	
	PVector currentTrans;
	
	public BattleMenu(final PApplet pa, Game returnGame, Player currentPlayer, PVector currentTrans) {
		this.pa = pa;
		this.returnGame = returnGame;
		this.currentPlayer = currentPlayer;
		this.currentTrans = currentTrans;
		
		actions = new Button[ACTION_BUTTONS];
		
		actions[ACTION_UNITS] = new Button(pa.width/2, 0.1f * pa.height, 200, 100, 255, 128, 255, "Units", pa);
		actions[ACTION_ADD_UNITS] = new Button(pa.width/2, 0.2f * pa.height, 200, 100, 255, 128, 255, "Shops", pa);
		actions[ACTION_INVENTORY] = new Button(pa.width/2, 0.3f * pa.height, 200, 100, 255, 128, 255, "Items", pa);
		actions[ACTION_END_TURN] = new Button(pa.width/2, 0.4f * pa.height, 200, 100, 255, 128, 255, "End", pa);
		actions[ACTION_SUSPEND] = new Button(pa.width/2, 0.5f * pa.height, 200, 100, 255, 128, 255, "Suspend", pa);
		
		actions[selectedIndex].setHighlighted();
		
		extraInfo = new BattleUI(pa);
	}
	
	public void update() {
		pa.pushMatrix();
		pa.translate(-currentTrans.x, -currentTrans.y);
		
		pa.fill(128, 128);
		pa.rect(0, 0, pa.width, pa.height);
		
		for(int i = 0; i < ACTION_BUTTONS; i++) {
			actions[i].display();
		}
		
		//Check key pressed
		if(pa.keyPressed == true) {
			pa.keyPressed = false;
			if(pa.key == PConstants.CODED) {
				switch(pa.keyCode) {
					case PConstants.UP:
						actions[selectedIndex].setHighlighted();
						selectedIndex = (selectedIndex - 1);
						if(selectedIndex < 0) {
							selectedIndex = ACTION_BUTTONS - 1;
						}
						actions[selectedIndex].setHighlighted();
						break;
					case PConstants.DOWN:
						actions[selectedIndex].setHighlighted();
						selectedIndex = (selectedIndex + 1) % ACTION_BUTTONS;
						actions[selectedIndex].setHighlighted();
						break;
				}
			}
			else {
				switch(pa.key) {
					case ' ':
						//Selected an option, carry out
						switch(selectedIndex) {
							case ACTION_UNITS:
								//Bring up unit list info
								break;
							case ACTION_ADD_UNITS:
								//Bring up purchase screen
								break;
							case ACTION_INVENTORY:
								//Bring up inventory menu
								break;
							case ACTION_END_TURN:
								returnGame.endCurrentTurn();
								break;
							case ACTION_SUSPEND:
								//Save current game (not in minimum reqs)
								break;
						}
						break;
					case 'e':
					case 'q':
						returnGame.cancelSelection();
						break;
				}
			}
		}
		extraInfo.update(currentPlayer);
		
		pa.popMatrix();
	}
}
