package gui;

import gameManager.Camera;
import gameManager.Game;
import helpers.Triple;
import playerUnits.Unit;
import processing.core.PApplet;
import processing.core.PConstants;

public class UnitBattleMenu {
	private final int ACTION_BUTTONS = 4;
	private final int ACTION_ATTACK = 0;
	private final int ACTION_INV = 1;
	private final int ACTION_TRADE = 3;
	private final int ACTION_WAIT = 2;
	
	private Button[] actions;
	private int selectedIndex = 0;
	
	private Unit selectedUnit;
	
	private final PApplet pa;
	private Game returnGame;
	
	private Camera gameCam;
	
	private ItemMenu itemMenu;
	private boolean items;
	
	public UnitBattleMenu(final PApplet pa, Game returnGame, Unit selectedUnit, Camera gameCam) {
		this.pa = pa;
		this.returnGame = returnGame;
		this.gameCam = gameCam;
		this.selectedUnit = selectedUnit;
		
		actions = new Button[ACTION_BUTTONS];
		
		Triple unitPos = gameCam.convertToDrawPos(selectedUnit.getPos());
		actions[ACTION_ATTACK] = new Button(unitPos.X - 150, unitPos.Y + 50, 100, 50, 255, 128, 255, "Attack", pa);
		actions[ACTION_INV] = new Button(unitPos.X, unitPos.Y + 50, 100, 50, 255, 128, 255, "Items", pa);
		actions[ACTION_TRADE] = new Button(unitPos.X, unitPos.Y + 110, 100, 50, 255, 128, 255, "Trade", pa);
		actions[ACTION_WAIT] = new Button(unitPos.X + 150, unitPos.Y + 50, 100, 50, 255, 128, 255, "Wait", pa);
		
		actions[selectedIndex].setHighlighted();
		//Add buttons
	}
	
	public void update() {
		if(items) {
			itemMenu.update();
		}
		else {
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
									returnGame.selectAttack();
									break;
								case ACTION_INV:
									items = true;
									itemMenu = new ItemMenu(pa, this, selectedUnit, gameCam.getTransPos());
									break;
								case ACTION_TRADE:
									returnGame.selectTrade();
									break;
								case ACTION_WAIT:
									returnGame.moveAndUpdateSelection(selectedUnit, gameCam.getSelectedGridPos());
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
	
	public void back(boolean actionTaken) {
		items = false;
		itemMenu = null;
		if(actionTaken) {
			returnGame.moveAndUpdateSelection(selectedUnit, gameCam.getSelectedGridPos());
		}
	}
}
