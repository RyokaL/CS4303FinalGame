package gui;

import gameManager.Camera;
import gameManager.Game;
import helpers.Pair;
import helpers.Triple;
import playerUnits.Unit;
import processing.core.PApplet;
import processing.core.PConstants;

public class UnitBattleMenu {
	private final int ACTION_BUTTONS = 3;
	private final int ACTION_ATTACK = 0;
	private final int ACTION_INV = 1;
	private final int ACTION_WAIT = 2;
	
	private Button[] actions;
	private int selectedIndex = 0;
	
	private final PApplet pa;
	private Game returnGame;
	
	public UnitBattleMenu(final PApplet pa, Game returnGame, Unit selectedUnit, Camera gameCam) {
		this.pa = pa;
		this.returnGame = returnGame;
		
		actions = new Button[ACTION_BUTTONS];
		
		Triple unitPos = gameCam.convertToDrawPos(selectedUnit.getPos());
		actions[ACTION_ATTACK] = new Button(unitPos.X - 150, unitPos.Y + 50, 100, 50, 255, 128, 255, "Attack", pa);
		actions[ACTION_INV] = new Button(unitPos.X, unitPos.Y + 50, 100, 50, 255, 128, 255, "Items", pa);
		actions[ACTION_WAIT] = new Button(unitPos.X + 150, unitPos.Y + 50, 100, 50, 255, 128, 255, "Wait", pa);
		
		actions[selectedIndex].setHighlighted();
		//Add buttons
	}
	
	public void update() {
		//Draw buttons
		for(int i = 0; i < ACTION_BUTTONS; i++) {
			//Maybe alter to allow clicks too
			actions[i].display();
		}
		
		//Check key pressed
		if(pa.keyPressed == true) {
			pa.keyPressed = false;
			if(pa.key == PConstants.CODED) {
				switch(pa.keyCode) {
					case PConstants.LEFT:
						actions[selectedIndex].setHighlighted();
						selectedIndex = (selectedIndex - 1);
						if(selectedIndex < 0) {
							selectedIndex = ACTION_BUTTONS - 1;
						}
						actions[selectedIndex].setHighlighted();
						break;
					case PConstants.RIGHT:
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
							case ACTION_ATTACK:
								//Bring up attack selection
								break;
							case ACTION_INV:
								//Bring up inventory menu
								break;
							case ACTION_WAIT:
								returnGame.moveAndUpdateSelection();
								break;
						}
						break;
					case 'q':
						returnGame.cancelSelection();
						break;
				}
			}
		}
	}
}
