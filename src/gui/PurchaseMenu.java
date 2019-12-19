package gui;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import constants.Constants;
import gameManager.Game;
import gameManager.Player;
import helpers.Pair;
import items.Blacksmith;
import items.UseItem;
import items.Weapon;
import map.Map;
import playerUnits.Unit;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import unitClass.ClassStore;
import unitClass.UnitClass;

public class PurchaseMenu {
	private final int STATE_SELECT_SHOP = 0;
	private final int STATE_UNIT_SHOP = 1;
	private final int STATE_WEAPON_SHOP = 2;
	private final int STATE_ITEM_SHOP = 3;
	
	private int state = STATE_SELECT_SHOP;
	
	private final int SHOP_BUTTONS = 3;
	private Button[] shopButtons;
	private final int SHOP_UNIT = 0;
	private final int SHOP_WEAPON = 1;
	private final int SHOP_ITEM = 2;
	private int selectedIndex = 0;
	
	private final int unitsPerPage = 6;
	private Button[][] unitButtons;
	private int unitPages;
	private int unitPageSelected = 0;
	
	private final int wepsPerPage = 6;
	private Button[][] wepButtons;
	private int wepPages;
	private int wepPageSelected = 0;
	
	private final int itemsPerPage = 6;
	private Button[][] itemButtons;
	private int itemPages;
	private int itemPageSelected = 0;
	
	private Button minus;
	private Button plus;
	
	private List<UnitClass> listClass;
	private List<Weapon> listWeps;
	private List<UseItem> listItems;
	
	private List<String> weaponNames;
	private List<String> itemNames;
	
	private BattleUI playerInfo;

	private Unit[][] easyAccessMap;
	private Map currMap;
	private Player currPlayer;
	private PVector currTrans;
	private Game returnGame;
	private ClassStore classes;
	private Blacksmith items;
	private final PApplet pa;
	
	public PurchaseMenu(final PApplet pa, Player currPlayer, PVector currTrans, Game returnGame, ClassStore classes, Blacksmith items, Unit[][] easyAccessMap, Map currMap) {
		this.pa = pa;
		this.currPlayer = currPlayer;
		this.currTrans = currTrans;
		this.returnGame = returnGame;
		this.classes = classes;
		this.items = items;
		this.easyAccessMap = easyAccessMap;
		this.currMap = currMap;
		
		listClass = classes.getClasses().parallelStream().filter(c -> c.getUpgradeStage() > 0).collect(Collectors.toList());
		listWeps = items.getWeaponList();
		listItems = items.getItemList();
		
		//shop buttons
		shopButtons = new Button[SHOP_BUTTONS];
		shopButtons[SHOP_UNIT] = new Button(pa.width/2 - 200, pa.height * 0.3f, 200, 100, 255, 128, 255, "Units", pa);
		shopButtons[SHOP_WEAPON] = new Button(pa.width/2 - 200, pa.height * 0.5f, 200, 100, 255, 128, 255, "Weapons", pa);
		shopButtons[SHOP_ITEM] = new Button(pa.width/2 - 200, pa.height * 0.7f, 200, 100, 255, 128, 255, "Items", pa);
		shopButtons[selectedIndex].setHighlighted();
		
		//Unit buttons
		List<String> classNames = listClass.stream()
				.map(x -> x.getName())
				.collect(Collectors.toList());
		int classSize = classNames.size();
		unitPages = (classSize / unitsPerPage) + 1;
		unitButtons = new Button[unitPages][unitsPerPage];
		for(int i = 0; i < unitPages; i++) {
			for(int j = 0; j < unitsPerPage; j += 2) {
				if((i * unitsPerPage) + j >= classSize) {
					break;
				}
				unitButtons[i][j] = new Button(((j % unitsPerPage/2) + 1) * pa.width/4 - 300,  pa.height * 0.3f - 200, 300, 200, 255, 128, 255, classNames.get((unitsPerPage * i) + j) + ": \n"
												+ listClass.get((unitsPerPage * i) + j).getCost(), pa);
				if((i * unitsPerPage) + j + 1 >= classSize) {
					break;
				}
				unitButtons[i][j + 1] = new Button(((j % unitsPerPage/2) + 1) * pa.width/4 - 300, pa.height * 0.6f - 200, 300, 200, 255, 128, 255, classNames.get((unitsPerPage * i) + j + 1)+ ": \n"
						+ listClass.get((unitsPerPage * i) + j + 1).getCost(), pa);
			}
		}
		
		//Weapon buttons
		weaponNames = items.getWeaponNames().stream().collect(Collectors.toList());
		int weaponSize = weaponNames.size();
		wepPages = (weaponSize / wepsPerPage) + 1;
		wepButtons = new Button[wepPages][wepsPerPage];
		for(int i = 0; i < wepPages; i++) {
			for(int j = 0; j < wepsPerPage; j += 2) {
				if((i * wepsPerPage) + j >= weaponSize) {
					break;
				}
				wepButtons[i][j] = new Button(((j % wepsPerPage/2) + 1) * pa.width/4 - 300,  pa.height * 0.3f - 200, 300, 200, 255, 128, 255, weaponNames.get((wepsPerPage * i) + j) + ": \n"
						+ listWeps.get((wepsPerPage * i) + j).getCost(), pa);
				if((i * wepsPerPage) + j + 1 >= weaponSize) {
					break;
				}
				wepButtons[i][j + 1] = new Button(((j % wepsPerPage/2) + 1) * pa.width/4 - 300, pa.height * 0.6f - 200, 300, 200, 255, 128, 255, weaponNames.get((wepsPerPage * i) + j + 1) + ": \n"
						+ listWeps.get((wepsPerPage * i) + j + 1).getCost(), pa);
			}
		}
		
		//Item buttons
		itemNames = items.getItemNames().stream().collect(Collectors.toList());
		int itemSize = itemNames.size();
		itemPages = (itemSize / itemsPerPage) + 1;
		itemButtons = new Button[itemPages][itemsPerPage];
		for(int i = 0; i < itemPages; i++) {
			for(int j = 0; j < itemsPerPage; j += 2) {
				if((i * itemsPerPage) + j >= itemSize) {
					break;
				}
				itemButtons[i][j] = new Button(((j % itemsPerPage/2) + 1) * pa.width/4 - 300,  pa.height * 0.3f - 200, 300, 200, 255, 128, 255, itemNames.get((itemsPerPage * i) + j)  + ": \n"
						+ listItems.get((itemsPerPage * i) + j).getCost(), pa);
				if((i * itemsPerPage) + j + 1 >= itemSize) {
					break;
				}
				itemButtons[i][j + 1] = new Button(((j % itemsPerPage/2) + 1) * pa.width/4 - 300, pa.height * 0.6f - 200, 300, 200, 255, 128, 255, itemNames.get((itemsPerPage * i) + j + 1)  + ": \n"
						+ listItems.get((itemsPerPage * i) + j).getCost(), pa);
			}
		}
		
		//Buttons to change page
		plus = new Button(0.8f * pa.width, 0.7f * pa.height, 200, 200, 255, 128, 255, ">", pa);
		minus = new Button(0.05f * pa.width, 0.7f * pa.height, 200, 200, 255, 128, 255, "<", pa);
		
		playerInfo = new BattleUI(pa);
	}
	
	public void update() {
		pa.pushMatrix();
		pa.translate(-currTrans.x, -currTrans.y);
		
		pa.fill(0, 230);
		pa.rect(0, 0, pa.width, pa.height);
		
		if(state == STATE_SELECT_SHOP) {
			for(int i = 0; i < SHOP_BUTTONS; i++) {
				shopButtons[i].display();
			}
		}
		else if(state == STATE_UNIT_SHOP) {
			for(int i = 0; i < unitsPerPage; i++) {
				if(unitButtons[unitPageSelected][i] == null) {
					continue;
				}
				unitButtons[unitPageSelected][i].update();
				if(unitButtons[unitPageSelected][i].isClicked()) {
					unitButtonClicked(unitPageSelected, i);
				}
				if(unitButtons[unitPageSelected][i].isHighlighted()) {
					buttonHighlighted(unitPageSelected, i);
				}
			}
		}
		else if(state == STATE_WEAPON_SHOP) {
			for(int i = 0; i < wepsPerPage; i++) {
				if(wepButtons[wepPageSelected][i] == null) {
					continue;
				}
				wepButtons[wepPageSelected][i].update();
				if(wepButtons[wepPageSelected][i].isClicked()) {
					unitButtonClicked(wepPageSelected, i);
				}
				if(wepButtons[wepPageSelected][i].isHighlighted()) {
					buttonHighlighted(wepPageSelected, i);
				}
			}
		}
		else if(state == STATE_ITEM_SHOP) {
			for(int i = 0; i < itemsPerPage; i++) {
				if(itemButtons[itemPageSelected][i] == null) {
					continue;
				}
				itemButtons[itemPageSelected][i].update();
				if(itemButtons[itemPageSelected][i].isClicked()) {
					unitButtonClicked(itemPageSelected, i);
				}
			}
		}
		
		if(state != STATE_SELECT_SHOP) {
			plus.update();
			if(plus.isClicked()) {
				updateIndex(1);
			}
			minus.update();
			if(minus.isClicked()) {
				updateIndex(-1);
			}
			
			playerInfo.update(currPlayer);
		}
		
		pa.popMatrix();
		
		//Check key pressed
		if(pa.keyPressed == true) {
			pa.keyPressed = false;
			if(pa.key == PConstants.CODED) {
				switch(pa.keyCode) {
					case PConstants.UP:
						if(state == STATE_SELECT_SHOP) {
							shopButtons[selectedIndex].setHighlighted();
							selectedIndex = (selectedIndex - 1);
							if(selectedIndex < 0) {
								selectedIndex = SHOP_BUTTONS - 1;
							}
							shopButtons[selectedIndex].setHighlighted();
						}
						break;
					case PConstants.DOWN:
						if(state == STATE_SELECT_SHOP) {
							shopButtons[selectedIndex].setHighlighted();
							selectedIndex = (selectedIndex + 1) % SHOP_BUTTONS;
							shopButtons[selectedIndex].setHighlighted();
						}
						break;
				}
			}
			else {
				switch(pa.key) {
					case ' ':
						//Selected an option, carry out
						if(state == STATE_SELECT_SHOP) {
							if(selectedIndex == SHOP_UNIT) {
								state = STATE_UNIT_SHOP;
							}
							else if(selectedIndex == SHOP_WEAPON) {
								state = STATE_WEAPON_SHOP;
							}
							else if(selectedIndex == SHOP_ITEM) {
								state = STATE_ITEM_SHOP;
							}
						}
						break;
					case 'e':
					case 'q':
						returnGame.cancelSelection();
						break;
				}
			}
		}
	}
	
	private void buttonHighlighted(int page, int element) {
		if(state == STATE_UNIT_SHOP) {
			UnitClass windowShop = listClass.get(page * unitsPerPage + element);
			int[] baseStats = windowShop.getBaseStats();
			pa.fill(0xCC000b69);
			pa.rect(pa.mouseX, pa.mouseY, 300, 400);
			pa.fill(255);
			pa.textSize(14);
			String info = windowShop.getName() + "\n" + "Base Str: " + baseStats[Constants.STR] + "\n"
					+ "Base Mag: " + baseStats[Constants.MAG] + "\n"
					+ "Base Def: " + baseStats[Constants.DEF] + "\n"
					+ "Base MDef: " + baseStats[Constants.MDEF] + "\n"
					+ "Base Dex: " + baseStats[Constants.DEX] + "\n"
					+ "Base Spd: " + baseStats[Constants.SPD] + "\n";
			int[] weapons = windowShop.getEquipableWeapons();
			info = info + "Weapons: \n";
			for(int i : weapons) {
				info = info + Constants.WEAPON_TYPES[i] + "\n";
			}
			
			pa.text(info, pa.mouseX + 150, pa.mouseY + 50);
			pa.textSize(32);
		}
		
		if(state == STATE_WEAPON_SHOP) {
			Weapon windowShop = listWeps.get(page * wepsPerPage + element);
			pa.fill(0xCC000b69);
			pa.rect(pa.mouseX, pa.mouseY, 300, 400);
			pa.fill(255);
			pa.textSize(14);
			
			String info = windowShop.getName() + "\n" + "Attack: " + windowShop.getAttack() + "\n" 
						+ "Hit: " + windowShop.getHitRate() + "%\n"
						+ "Crit: " + windowShop.getCriticalRate() + "%\n"
						+ "Range: " + windowShop.getMinRange() + "-" + windowShop.getMaxRange() +  "\n"
						+ "Type: " + Constants.WEAPON_TYPES[windowShop.getWeaponType()]		
						+ "Durability: " + windowShop.getDurability() + "/" + windowShop.getDurability();
			pa.text(info, pa.mouseX + 150, pa.mouseY + 50);
			pa.textSize(32);
		}
	}
	
	private void unitButtonClicked(int page, int element) {
		if(state == STATE_UNIT_SHOP) {
			UnitClass purchasing = listClass.get(page * unitsPerPage + element);
			if(currPlayer.getGold() >= purchasing.getCost()) {
				//For ease, bought units go to start pos only if clear
				Pair posToPlace = currMap.getStartPos(currPlayer.getTeam());
				if(easyAccessMap[posToPlace.x][posToPlace.y] == null) {
					currPlayer.addGold(-purchasing.getCost());
					Unit newUnit = new Unit(posToPlace.x, posToPlace.y, purchasing, currPlayer.getTeam(), pa);
					Weapon toEquip = items.getRarityList(0).stream().filter(w -> w.getWeaponType() == purchasing.getEquipableWeapons()[0]).collect(Collectors.toList()).get(0);
					if(toEquip != null) {
						newUnit.equipWeapon(toEquip);
					}
					easyAccessMap[posToPlace.x][posToPlace.y] = newUnit;
					currPlayer.addUnit(newUnit);
					returnGame.cancelSelection();
				}
			}
		}
		
		if(state == STATE_WEAPON_SHOP) {
			Unit luckyGiftee = returnGame.getHoverUnit();
			if(luckyGiftee == null || luckyGiftee.getTeam() != currPlayer.getTeam()) {
				return;
			}
			else {
				Weapon purchasing = items.getWeaponObj(weaponNames.get(page * wepsPerPage + element));
				boolean equippable = false;
				int[] weaponTypes = luckyGiftee.getAssignedClass().getEquipableWeapons();
				for(int i = 0; i > weaponTypes.length; i++) {
					if(weaponTypes[i] == purchasing.getWeaponType()) {
						equippable = true;
						break;
					}
				}
				if(currPlayer.getGold() >= purchasing.getCost() && (luckyGiftee.getInventorySize() <= Constants.MAX_INVENTORY || (luckyGiftee.getEquipped() == null && equippable))) {
					currPlayer.addGold(-purchasing.getCost());
					if(luckyGiftee.getEquipped() == null && equippable) {
						luckyGiftee.equipWeapon(purchasing);
					}
					else {
						luckyGiftee.addToInventory(purchasing);
						returnGame.cancelSelection();
					}
				}
			}
		}
		
		if(state == STATE_ITEM_SHOP) {
			Unit luckyGiftee = returnGame.getHoverUnit();
			if(luckyGiftee == null || luckyGiftee.getTeam() != currPlayer.getTeam()) {
				return;
			}
			else {
				UseItem purchasing = items.getItemObj(itemNames.get(page * itemsPerPage + element));
				if(currPlayer.getGold() >= purchasing.getCost() && luckyGiftee.getInventorySize() <= Constants.MAX_INVENTORY) {
					currPlayer.addGold(-purchasing.getCost());
					luckyGiftee.addToInventory(purchasing);
					returnGame.cancelSelection();
				}
			}
		}
	}
	
	private void updateIndex(int change) {
		if(state == STATE_UNIT_SHOP) {
			unitPageSelected += change;
			if(unitPageSelected > unitPages) {
				unitPageSelected = 0;
			}
			unitPageSelected = unitPageSelected % unitPages;
		}
		
		if(state == STATE_WEAPON_SHOP) {
			wepPageSelected += change;
			if(wepPageSelected > wepPages) {
				wepPageSelected = 0;
			}
			wepPageSelected = wepPageSelected % wepPages;
		}
		
		if(state == STATE_ITEM_SHOP) {
			itemPageSelected += change;
			if(itemPageSelected > itemPages) {
				itemPageSelected = 0;
			}
			itemPageSelected = itemPageSelected % itemPages;
		}
	}
}
