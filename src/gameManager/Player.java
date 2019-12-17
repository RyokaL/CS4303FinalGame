package gameManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import helpers.Pair;
import items.Item;
import items.Weapon;
import playerUnits.Unit;

public class Player {

	private int team;
	private int gold;
	private ArrayList<Unit> units;
	
	public Player(int team, Unit initialUnit) {
		this.team = team;
		this.gold = 0;
		this.units = new ArrayList<Unit>();
		this.units.add(initialUnit);
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
	
	public boolean hasLost() {
		return units.isEmpty() || !units.get(0).getAssignedClass().getName().equals("Tactician");
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
}
