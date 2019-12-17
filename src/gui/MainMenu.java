package gui;

import constants.Constants;
import gameManager.DunScaith;
import processing.core.PApplet;

public class MainMenu {
	private final int START_MENU_INDEX = 0;
	private final int MENU_BUTTONS = 1;

	private Button startGame;
	private Button[] menuButtons;
	
	private DunScaith returnClass;
	
	public MainMenu(final PApplet pa, DunScaith returnClass) {
		this.returnClass = returnClass;
		menuButtons = new Button[MENU_BUTTONS];
		
		startGame = new Button(pa.width/2 - 50, pa.height/2 - 50, 100, 100, 0xFF34ebdb, 0xFFeb3434, 0xFFd634eb, "Start Game", pa);
		
		menuButtons[START_MENU_INDEX] = startGame;
	}
	
	public void update() {
		for(int i = 0; i < MENU_BUTTONS; i++) {
			menuButtons[i].update();
			if(menuButtons[i].isClicked()) {
				eventHandler(i);
			}
		}
	}
	
	private void eventHandler(int buttonIndex) {
		switch(buttonIndex) {
			case START_MENU_INDEX:
				startGameButton();
				break;
			
		}
	}
	
	private void startGameButton() {
		returnClass.updateState(Constants.STATE_POST_MAIN);
	}
}
