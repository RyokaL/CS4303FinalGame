package gameManager;

import helpers.Pair;
import helpers.Triple;
import processing.core.PApplet;
import processing.core.PVector;

public class Camera {
	
	private final float yPosOffset = 0;
	private final float xPosOffset = 0;
	
	private PVector actualPos;
	private Pair gridPos;
	private float tileSize;
	
	private float initActPosX, initActPosY;
	private final PApplet pa;
	
	public Camera(int initGridX, int initGridY, float tileSize, float initActPosX, float initActPosY, final PApplet pa) {
		this.pa = pa;
		this.gridPos = new Pair(initGridX, initGridY);
		this.actualPos = new PVector(initActPosX + (initGridX * tileSize) + tileSize / 2, initActPosY + (initGridY * tileSize) + tileSize / 2);
		this.tileSize = tileSize;
		this.initActPosX = initActPosX;
		this.initActPosY = initActPosY;
	}
	
	public void updateGridPos(int newX, int newY) {
		gridPos.x = newX;
		gridPos.y = newY;
		
		actualPos.x = initActPosX + (newX * tileSize) + tileSize / 2;
		actualPos.y = initActPosY + (newY * tileSize) + tileSize / 2;
	}
	
	public PVector getActualPos() {
		return actualPos;
	}
	
	public PVector getTransPos() {
		return new PVector(-(-2* initActPosX + actualPos.x - tileSize/2 - xPosOffset), -(-2* initActPosY + actualPos.y - tileSize/2 - yPosOffset));
	}
	
	public Pair getSelectedGridPos() {
		return gridPos;
	}
	
	public Triple convertToDrawPos(Pair gridPos) {
		return new Triple((gridPos.x * tileSize), (gridPos.y * tileSize), tileSize);
	}
	
	public void drawSelectedGrid() {
		//TODO: Change these to actual colours
		pa.stroke(255);
		pa.fill(255, 100);
		pa.rect(actualPos.x - tileSize / 2 - initActPosX, actualPos.y - tileSize / 2 - initActPosY, tileSize, tileSize);
	}
}
