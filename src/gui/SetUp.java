package gui;

import constants.Constants;
import gameManager.DunScaith;
import processing.core.PApplet;
import processing.core.PConstants;

public class SetUp {
	private final int MENU_SETUP_BUTTONS = 7;
	private final int SETUP_PLAYER1_TEAM = 0;
	private final int SETUP_PLAYER2_TEAM = 1;
	private final int SETUP_PLAYER3_TEAM = 2;
	private final int SETUP_PLAYER4_TEAM = 3;
	
	private final int SETUP_MAP_PLUS = 4;
	private final int SETUP_MAP_MINUS = 5;
	
	private final int SETUP_START_GAME = 6;
	
	private final PApplet pa;
	private DunScaith returnClass;
	
	private boolean teams[];
	
	private int p1TeamIndex = 5;
	private int p2TeamIndex = 5;
	private int p3TeamIndex = 5;
	private int p4TeamIndex = 5;
	
	private String[] mapNames;
	private int mapIndex;
	
	private Button[] menuButtonSetup;
	//private Button[] menuButtonMap;
	
	public SetUp(final PApplet pa, DunScaith controller, String[] mapNames) {
		this.pa = pa;
		this.returnClass = controller;
		
		teams = new boolean[4];
		
		menuButtonSetup = new Button[MENU_SETUP_BUTTONS];
		
		//Create setup buttons
		menuButtonSetup[SETUP_PLAYER1_TEAM] = new Button(0, 0, 0.25f * pa.width, 0.8f * pa.height, 0x00FFFFFF, 0x00FFFFFF, 0x00FFFFFF, "", pa);
		menuButtonSetup[SETUP_PLAYER2_TEAM] = new Button(0.25f * pa.width, 0, 0.25f * pa.width, 0.8f * pa.height, 0x00FFFFFF, 0x00FFFFFF, 0x00FFFFFF, "", pa);
		menuButtonSetup[SETUP_PLAYER3_TEAM] = new Button(0.5f * pa.width, 0, 0.25f * pa.width, 0.8f * pa.height, 0x00FFFFFF, 0x00FFFFFF, 0x00FFFFFF, "", pa);
		menuButtonSetup[SETUP_PLAYER4_TEAM] = new Button(0.75f * pa.width, 0, 0.25f * pa.width, 0.8f * pa.height, 0x00FFFFFF, 0x00FFFFFF, 0x00FFFFFF, "", pa);
		
		//Create map buttons
		menuButtonSetup[SETUP_MAP_PLUS] = new Button(0.6f * pa.width, 0.85f * pa.height, 100, 100, 255, 128, 255, ">", pa);
		menuButtonSetup[SETUP_MAP_MINUS] = new Button(0.05f * pa.width, 0.85f * pa.height, 100, 100, 255, 128, 255, "<", pa);
		
		//Create start game button
		menuButtonSetup[SETUP_START_GAME] = new Button(0.8f * pa.width, 0.825f * pa.height, 200, 150, 255, 128, 255, "START", pa);
		
		mapIndex = 0;
		this.mapNames = mapNames;
	}
	
	public void update() {
		//Screen separators:
		pa.stroke(255);
		pa.line(0.25f * pa.width, 0, 0.25f * pa.width, 0.8f * pa.height);
		pa.line(0.5f * pa.width, 0, 0.5f * pa.width, 0.8f * pa.height);
		pa.line(0.75f * pa.width, 0, 0.75f * pa.width, 0.8f * pa.height);
		pa.line(0, 0.8f * pa.height, pa.width, 0.8f * pa.height);
		
		//draw player/team select
		for(int i = 0; i < MENU_SETUP_BUTTONS; i++) {
			menuButtonSetup[i].update();
			if(menuButtonSetup[i].isClicked()) {
				setupEventHandle(i);
			}
		}
		
		//Draw selected team box
		pa.noStroke();
		//P1
		pa.fill(Constants.TEAM_COLOURS[p1TeamIndex]);
		pa.rect(0, 0, 0.25f * pa.width, 0.8f * pa.height);
		//P2
		pa.fill(Constants.TEAM_COLOURS[p2TeamIndex]);
		pa.rect(0.25f * pa.width, 0, 0.25f * pa.width, 0.8f * pa.height);
		//P3
		pa.fill(Constants.TEAM_COLOURS[p3TeamIndex]);
		pa.rect(0.5f * pa.width, 0, 0.25f * pa.width, 0.8f * pa.height);
		//P4
		pa.fill(Constants.TEAM_COLOURS[p4TeamIndex]);
		pa.rect(0.75f * pa.width, 0, 0.25f * pa.width, 0.8f * pa.height);
		
		//Draw selected map box
		pa.fill(200);
		pa.stroke(255);
		pa.rect(0.05f * pa.width + 100, 0.85f * pa.height, 0.5f * pa.width, 100);
		pa.textAlign(PConstants.CENTER);
		pa.textSize(32);
		pa.stroke(0);
		pa.fill(0);
		pa.text(mapNames[mapIndex], 0.325f * pa.width, 0.85f * pa.height + 50);
	}
	
	private void setupEventHandle(int index) {
		switch(index) {
			case SETUP_PLAYER1_TEAM:
				p1TeamIndex = (p1TeamIndex + 1) % 6;
				while(p1TeamIndex != 5 && p1TeamIndex == p2TeamIndex || p1TeamIndex == p3TeamIndex || p1TeamIndex == p4TeamIndex) {
					p1TeamIndex = (p1TeamIndex + 1) % 6;
				}
				if(p1TeamIndex == 4) {
					p1TeamIndex = 5;
					break;
				}
				break;
			case SETUP_PLAYER2_TEAM:
				p2TeamIndex = (p2TeamIndex + 1) % 6;
				while(p2TeamIndex != 5 && p2TeamIndex == p1TeamIndex || p2TeamIndex == p3TeamIndex || p2TeamIndex == p4TeamIndex) {
					p2TeamIndex = (p2TeamIndex + 1) % 6;
				}
				if(p2TeamIndex == 4) {
					p2TeamIndex = 5;
					break;
				}
				break;
			case SETUP_PLAYER3_TEAM:
				p3TeamIndex = (p3TeamIndex + 1) % 6;
				while(p3TeamIndex != 5 && p3TeamIndex == p1TeamIndex || p3TeamIndex == p2TeamIndex || p3TeamIndex == p4TeamIndex) {
					p3TeamIndex = (p3TeamIndex + 1) % 6;
				}
				if(p3TeamIndex == 4) {
					p3TeamIndex = 5;
					break;
				}
				break;
			case SETUP_PLAYER4_TEAM:
				p4TeamIndex = (p4TeamIndex + 1) % 6;
				while(p4TeamIndex != 5 && p4TeamIndex == p1TeamIndex || p4TeamIndex == p2TeamIndex || p4TeamIndex == p3TeamIndex) {
					p4TeamIndex = (p4TeamIndex + 1) % 6;
				}
				if(p4TeamIndex == 4) {
					p4TeamIndex = 5;
					break;
				}
				break;
			case SETUP_MAP_PLUS:
				mapIndex = (mapIndex + 1) % mapNames.length;
				break;
			case SETUP_MAP_MINUS:
				mapIndex = mapIndex - 1;
				if(mapIndex < 0) {
					mapIndex = mapNames.length - 1;
				}
				break;
			case SETUP_START_GAME:
				//Set team variables to true/false
				int players = 0;
				//P1
				if(p1TeamIndex != 5) {
					teams[p1TeamIndex] = true;
					players++;
				}
				//P2
				if(p2TeamIndex != 5) {
					teams[p2TeamIndex] = true;
					players++;
				}
				//P3
				if(p3TeamIndex != 5) {
					teams[p3TeamIndex] = true;
					players++;
				}
				//P4
				if(p4TeamIndex != 5) {
					teams[p4TeamIndex] = true;
					players++;
				}
				
				if(players == 0) {
					//Display some type of message
					return;
				}
				
				//Get selected map name
				String selectedMap = mapNames[mapIndex];
				
				//Randomly select starting player
				int j = 0;
				int[] selectTurn = new int[players];
				for(int i = 0; i < teams.length; i++) {
					if(teams[i]) {
						selectTurn[j] = i;
						j++;
					}
				}
				int startTurn = selectTurn[(int)pa.random(selectTurn.length)];
				
				//pass back startTurn, selectedMap and teams
				returnClass.startGame(teams, selectedMap, startTurn);
				break;
		}
	}
}
