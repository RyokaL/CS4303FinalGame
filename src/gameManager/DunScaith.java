package gameManager;
import java.io.IOException;

import constants.Constants;
import gui.MainMenu;
import gui.SetUp;
import items.Blacksmith;
import map.MapStore;
import map.TileSetStore;
import processing.core.PApplet;
import processing.core.PConstants;
import unitClass.ClassStore;

public class DunScaith extends PApplet {
	ClassStore classes;
	Blacksmith weapons;
	TileSetStore tiles;
	MapStore maps;
	
	private int gameState = Constants.STATE_MAIN_MENU;
	
	private int lastWinner = -1;
	
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
		System.out.println(sketchPath("Resources/sprites/Archer/Red.png"));
		loadImage(sketchPath("data/Resources/sprites/Archer/Red.png"));
        try {
			classes = new ClassStore("Resources/jobClasses", this);
			weapons = new Blacksmith("Resources/weaponStats", "Resources/useItems", this);
			tiles = new TileSetStore("Resources/tiles", this);
			maps = new MapStore("Resources/maps", this);
		} catch (Exception e) {
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
    			updateGame();
    			break;
    		case Constants.STATE_END:
    			endGame();
    			break;
    		default:
    			background(0);
    			break;
    	}
    }
    
    private void updateGame() {
    	activeGame.update();
    	if((lastWinner = activeGame.getWinState()) != -1) {
    		gameState = Constants.STATE_END;
    	}
    }
    
    private void endGame() {
    	String winner = "";
    	switch(lastWinner) {
    		case Constants.RED:
    			winner = "Red Team";
    			break;
    		case Constants.BLUE:
    			winner = "Blue Team";
    			break;
    		case Constants.GREEN:
    			winner = "Green Team";
    			break;
    		case Constants.YELLOW:
    			winner = "Yellow Team";
    			break;
    		case Constants.ENEMY:
    			winner = "... no-one";
    			break;
    	}
    	textSize(32);
    	textAlign(PConstants.CENTER);
    	fill(255);
    	text("The winner is... ", width/2, 0.2f * height);
    	fill(Constants.TEAM_COLOURS[lastWinner]);
    	text(winner, width/2, height/2);
    	fill(255);
    	text("...Press Any Key to Continue...", width/2, height * 0.8f);
    	textSize(12);
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
    	activeGame = new Game(maps.getMapObj(mapName), teams[Constants.RED], teams[Constants.BLUE], teams[Constants.GREEN], teams[Constants.YELLOW], startTurn, this, classes, weapons);
    	gameState = Constants.STATE_GAME;
    }
    
    public void keyPressed() {
    	if(gameState == Constants.STATE_END) {
    		gameState = Constants.STATE_MAIN_MENU;
    	}
    }
}
