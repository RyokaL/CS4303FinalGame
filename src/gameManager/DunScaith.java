package gameManager;
import java.io.IOException;

import constants.Constants;
import gui.MainMenu;
import gui.SetUp;
import map.MapStore;
import map.TileSetStore;
import processing.core.PApplet;
import processing.core.PVector;
import unitClass.ClassStore;
import weapons.Blacksmith;

public class DunScaith extends PApplet {
	ClassStore classes;
	Blacksmith weapons;
	TileSetStore tiles;
	MapStore maps;
	
	private int gameState = Constants.STATE_MAIN_MENU;
	
	private MainMenu main;
	private SetUp setup;
	
	private Game activeGame;
	
	// The argument passed to main must match the class name
    public static void main(String[] args) {
        PApplet.main("gameManager.DunScaith");
    }

    //Set up but only for screen size
    public void settings(){
        fullScreen();
    }

    //Set up variables etc
    public void setup(){
        try {
			classes = new ClassStore("Resources/jobClasses", this);
			weapons = new Blacksmith("Resources/weaponStats", this);
			tiles = new TileSetStore("Resources/tiles", this);
			maps = new MapStore("Resources/maps", this);
			System.out.println(classes.getClassNames());
			System.out.println(weapons.getWeaponNames());
			System.out.println(tiles.getTileNames());
			System.out.println(maps.getMapNames());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    //Draw to screen
    public void draw() {
    	background(0);
    	switch(gameState) {
    		case Constants.STATE_MAIN_MENU:
    			drawMainMenu();
    			break;
    		case Constants.STATE_POST_MAIN:
    			drawSetupMenu();
    			break;
    		case Constants.STATE_GAME:
    			pushMatrix();
    			PVector gameCamPos = activeGame.getCamera().getTransPos();
    			translate(gameCamPos.x, gameCamPos.y);
    			updateGame();
    			popMatrix();
    			//TOOD: Add UI drawing here (turn etc..)
    			break;
    		default:
    			background(0);
    			break;
    	}
    }
    
    private void updateGame() {
    	activeGame.update();
    }
    
    private void drawMainMenu() {
    	if(main == null) {
    		main = new MainMenu(this, this);
    	}
    	main.update();
    }
    
    private void drawSetupMenu() {
    	if(setup == null) {
    		setup = new SetUp(this,this, (maps.getMapNames().toArray(new String[maps.getMapNames().size()])) );
    	}
    	setup.update();
    }
    
    public void updateState(int newState) {
    	gameState = newState;
    }
    
    public void startGame(boolean[] teams, String mapName, int startTurn) {
    	activeGame = new Game(maps.getMapObj(mapName), teams[Constants.RED], teams[Constants.BLUE], teams[Constants.GREEN], teams[Constants.YELLOW], startTurn, this, classes);
    	gameState = Constants.STATE_GAME;
    }
    
    public void keyPressed() {
    	
    }
}
