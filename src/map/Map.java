package map;

import java.util.ArrayList;
import java.util.HashSet;

import constants.Constants;
import helpers.Pair;
import helpers.Triple;
import playerUnits.Unit;
import processing.core.PApplet;
import processing.core.PImage;

public class Map {

	private String name;
	private transient PImage mapImg;
	private String mapPath;
	
	//String array index must match up with map values
	private String[] tileNames;
	private transient Tile[] tiles;
	
	private int[][] map;
	private float mapStartPosX;
	private float mapStartPosY;
	private float tileSize;
	
	private Pair redStartPos;
	private Pair blueStartPos;
	private Pair greenStartPos;
	private Pair yellowStartPos;
	
	public Pair[] getAttackSpaces(Unit unitToCheck) {
		HashSet<Pair> listOfSpaces = new HashSet<Pair>();
		int minRange = unitToCheck.getEquipped().getMinRange();
		int maxRange = unitToCheck.getEquipped().getMaxRange();
		
		Pair initPos = unitToCheck.getPos();
		for(int i = minRange; i <= maxRange; i++) {
			if(initPos.x + i < map[0].length) {
				listOfSpaces.add(new Pair(initPos.x + i, initPos.y));
			}
			if(initPos.x - i >= 0) {
				listOfSpaces.add(new Pair(initPos.x - i, initPos.y));
			}
			
			if(initPos.y + i < map.length) {
				listOfSpaces.add(new Pair(initPos.x, initPos.y + 1));
			}
			if(initPos.y - i >= 0) {
				listOfSpaces.add(new Pair(initPos.x, initPos.y - i));
			}
		}
		return (Pair[])listOfSpaces.toArray();
	}
	
	public Pair[] getMovementSpaces(Unit unitToCheck, Unit[][] unitMap) {
		//TODO: Alter so enemies count as unmovable
		HashSet<Pair> listOfSpace = new HashSet<Pair>();
		int maxMove = unitToCheck.getAssignedClass().getMovement();
		ArrayList<Triple> spacesToCheck = new ArrayList<Triple>();
		
		Pair initPos = unitToCheck.getPos();
		spacesToCheck.add(new Triple(maxMove, initPos.x, initPos.y));
		while(!spacesToCheck.isEmpty()) {
			Triple t = spacesToCheck.get(0);
			spacesToCheck.remove(t);
			if(t.x >= 0) {
				listOfSpace.add(new Pair(t.y, t.z));
			}
			else {
				continue;
			}
			//Check x + 1;
			if(t.y + 1 < map[0].length) {
				int mapTile = map[t.y + 1][t.z];
				int movement;
				if(unitMap[t.y + 1][t.z] != null && unitMap[t.y + 1][t.z].getTeam() != unitToCheck.getTeam()) {
					movement = 1;
				}
				else {
					movement = getTileEffects(unitToCheck, mapTile);
				}
				
				if(movement != 1) {
					spacesToCheck.add(new Triple(t.x -1 + movement, t.y + 1, t.z));
				}
			}
			//Check x - 1;
			if(t.y - 1 >= 0) {
				int mapTile = map[t.y - 1][t.z];
				int movement;
				if(unitMap[t.y - 1][t.z] != null && unitMap[t.y - 1][t.z].getTeam() != unitToCheck.getTeam()) {
					movement = 1;
				}
				else {
					movement = getTileEffects(unitToCheck, mapTile);
				}
				
				if(movement != 1) {
					spacesToCheck.add(new Triple(t.x -1 + movement, t.y - 1, t.z));
				}
			}
			//Check y + 1;
			if(t.z + 1 < map.length) {
				int mapTile = map[t.y][t.z + 1];
				int movement;
				if(unitMap[t.y][t.z + 1] != null && unitMap[t.y][t.z + 1].getTeam() != unitToCheck.getTeam()) {
					movement = 1;
				}
				else {
					movement = getTileEffects(unitToCheck, mapTile);
				}
				
				if(movement != 1) {
					spacesToCheck.add(new Triple(t.x -1 + movement, t.y, t.z + 1));
				}
			}
			//Check y - 1;
			if(t.z - 1 >= 0) {
				int mapTile = map[t.y][t.z - 1];
				int movement;
				if(unitMap[t.y][t.z - 1] != null && unitMap[t.y][t.z - 1].getTeam() != unitToCheck.getTeam()) {
					movement = 1;
				}
				else {
					movement = getTileEffects(unitToCheck, mapTile);
				}
				
				if(movement != 1) {
					spacesToCheck.add(new Triple(t.x -1 + movement, t.y, t.z - 1));
				}
			}
		}
		Pair[] toReturn = new Pair[listOfSpace.size()];
		listOfSpace.toArray(toReturn);
		return toReturn;
	}
	
	public Tile getTileAtPos(Pair pos) {
		int mapIndex = map[pos.x][pos.y];
		return tiles[mapIndex];
	}
	
	public int getTileEffects(Unit unitToCheck, int mapTile) {
		Tile currTile = tiles[mapTile];
		int unitMount = unitToCheck.getAssignedClass().getMounted();
		boolean canMove = false;
		
		switch (currTile.getUnitsCanOccupy()) {
			case Constants.GROUND_MOUNT:
				canMove = (unitMount == Constants.GROUND_MOUNT);
				break;
			case Constants.FLYING_MOUNT:
				canMove = (unitMount == Constants.FLYING_MOUNT);
				break;
			case Constants.AFFECT_GROUND_UNITS:
				canMove = (unitMount == Constants.NO_MOUNT || unitMount == Constants.GROUND_MOUNT);
				break;
			case Constants.AFFECT_MOUNTED:
				canMove = (unitMount == Constants.GROUND_MOUNT || unitMount == Constants.FLYING_MOUNT);
				break;
			case Constants.AFFECT_ALL:
				canMove = true;
				break;
		}
		if(canMove) {
			int affectedMovement = currTile.getAffectMove();
			switch (currTile.getAffectedUnits()) {
				case Constants.AFFECT_ALL:
					return currTile.getAffectMove();
				case Constants.AFFECT_GROUND_UNITS:
					return (unitMount == Constants.NO_MOUNT || unitMount == Constants.GROUND_MOUNT) ? affectedMovement : 0;
				case Constants.AFFECT_MOUNTED:
					return (unitMount == Constants.GROUND_MOUNT || unitMount == Constants.FLYING_MOUNT) ? affectedMovement : 0;
				case Constants.GROUND_MOUNT:
					return (unitMount == Constants.GROUND_MOUNT) ? affectedMovement : 0;
				case Constants.FLYING_MOUNT:
					return (unitMount == Constants.FLYING_MOUNT) ? affectedMovement : 0;
				case Constants.NO_UNITS:
					return 0;
			}
		}
		return 1;
	}
	
	public String getName() {
		return name;
	}

	public PImage getMapImg() {
		return mapImg;
	}

	public String[] getTileNames() {
		return tileNames;
	}

	public int[][] getMap() {
		return map;
	}

	public float getMapStartPosX() {
		return mapStartPosX;
	}

	public float getMapStartPosY() {
		return mapStartPosY;
	}

	public float getTileSize() {
		return tileSize;
	}

	public Pair getRedStartPos() {
		return redStartPos;
	}

	public Pair getBlueStartPos() {
		return blueStartPos;
	}

	public Pair getGreenStartPos() {
		return greenStartPos;
	}

	public Pair getYellowStartPos() {
		return yellowStartPos;
	}

	public void loadMap(final PApplet pa) {
		mapImg = pa.loadImage(mapPath);
	}
	
	public void loadTiles() {
		tiles = new Tile[tileNames.length];
		for(int i = 0; i < tileNames.length; i++) {
			tiles[i] = TileSetStore.getTileObj(tileNames[i]);
		}
	}
}