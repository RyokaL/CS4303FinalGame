package gameManager;
import java.io.IOException;

import map.MapStore;
import map.TileSetStore;
import processing.core.PApplet;
import unitClass.ClassStore;
import weapons.Blacksmith;

public class DunScaith extends PApplet {
	
	ClassStore classes;
	Blacksmith weapons;
	TileSetStore tiles;
	MapStore maps;
	
	// The argument passed to main must match the class name
    public static void main(String[] args) {
        PApplet.main("DunScaith");
    }

    //Set up but only for screen size
    public void settings(){
        fullScreen();
    }

    //Set up variables etc
    public void setup(){
        try {
			classes = new ClassStore("src/jobClasses", this);
			weapons = new Blacksmith("src/weaponStats", this);
			tiles = new TileSetStore("src/map/tiles", this);
			maps = new MapStore("src/map/maps", this);
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
    public void draw(){
    	background(0);
    }
}
