package gui;

import java.util.Set;

import gameManager.Player;
import items.Blacksmith;
import processing.core.PApplet;
import unitClass.ClassStore;

public class ShopMenu {
	
	private final PApplet pa;
	private Blacksmith items;
	private ClassStore classes;
	private Player player;
	
	private Set<String> weaponNames;
	private Set<String> itemNames;
	private Set<String> classNames;

	public ShopMenu(final PApplet pa, Blacksmith items, ClassStore classes, Player player) {
		this.pa = pa;
		this.items = items;
		this.classes = classes;
		this.player = player;
		
		this.weaponNames = items.getWeaponNames();
		this.itemNames = items.getItemName();
		this.classNames = classes.getClassNames();
	}
}
