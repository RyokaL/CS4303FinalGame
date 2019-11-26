import java.io.IOException;

import processing.core.PApplet;
import unitClass.ClassStore;
import weapons.Blacksmith;

public class DunScaith extends PApplet {
	
	ClassStore classes;
	Blacksmith weapons;
	
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
			System.out.println(classes.getClassNames());
			System.out.println(weapons.getWeaponNames());
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
