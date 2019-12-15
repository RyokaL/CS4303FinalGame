package gameManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import helpers.Pair;
import playerUnits.Unit;
import weapons.Weapon;

public class Player {

	private int team;
	private int gold;
	private ArrayList<Unit> units;
	private ArrayList<Weapon> inventory;
	
	public Player(int team, Unit initialUnit) {
		this.team = team;
		this.gold = 0;
		this.units = new ArrayList<Unit>();
		this.units.add(initialUnit);
		this.inventory = new ArrayList<Weapon>();
	}
	
	public Player(int team) {
		this.team = team;
		this.gold = 0;
		this.units = new ArrayList<Unit>();
	}
	
	public Unit getUnitAtPos(Pair pos) {
		for(Unit u : units) {
			if(u.getPos().equals(pos)) {
				return u;
			}
		}
		return null;
	}
	
	public long getUnitsToMove() {
		return units.stream().filter(((Predicate<Unit>)Unit::hasMoved).negate()).count();
	}
	
	public List<Unit> getUnmovedUnits() {
		return units.stream().filter(((Predicate<Unit>)Unit::hasMoved).negate()).collect(Collectors.toList());
	}
	
	public boolean isEmpty() {
		return units.isEmpty();
	}
	
	public int getTeam() {
		return team;
	}
	
	public int getGold() {
		return gold;
	}
	
	public ArrayList<Unit> getUnits() {
		return units;
	}
	
	public void addGold(int toAdd) {
		gold += toAdd;
	}
	
	public void spendGold(int spent) {
		gold -= spent;
	}
	
	public void addUnit(Unit newUnit) {
		units.add(newUnit);
	}
	
	public boolean removeUnit(Unit toRemove) {
		return units.remove(toRemove);
	}
	
	public ArrayList<Weapon> getInventory() {
		return inventory;
	}
	
	public void addToInventory(Weapon toAdd) {
		inventory.add(toAdd);
	}
}
