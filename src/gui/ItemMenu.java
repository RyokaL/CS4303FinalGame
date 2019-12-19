package gui;

import constants.Constants;
import gameManager.Game;
import items.Item;
import items.UseItem;
import items.Weapon;
import playerUnits.Unit;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class ItemMenu {
	private final int ACTION_BUTTONS = 4;
	private final int ACTION_USE = 0;
	private final int ACTION_MOVE = 1;
	private final int ACTION_DISCARD = 2;
	private final int ACTION_BACK = 3;

	private boolean isUnit;
	
	private Button[] itemButtons;
	private Button[] actionButtons;
	
	private int noItems;
	private boolean wasEmpty;
	private Item[] items;
	
	private Weapon equipped;
	private Unit unitInv;
	
	private Button equippedWeapon;
	
	private int selectedIndex;
	private int secondSelectedIndex;
	
	private boolean moving;
	
	private int equipIndex = -1;
	
	private int currSelectedIndex;
	private Item selectedItem;
	private boolean selectionMade;
	
	private boolean trade;
	
	private PVector transPos;
	private UnitBattleMenu returnUMenu;
	private Game returnMenu;
	
	private final PApplet pa;
	
	public ItemMenu(final PApplet pa, UnitBattleMenu returnUMenu, Unit unitInv, PVector transPos) {
		isUnit = true;
		this.pa = pa;
		this.returnUMenu = returnUMenu;
		this.transPos = transPos;
		this.unitInv = unitInv;

		regenButtons();
	}
	
	public ItemMenu(final PApplet pa, Game returnMenu, Unit unitInv, PVector transPos) {
		isUnit = true;
		this.pa = pa;
		this.returnMenu = returnMenu;
		this.transPos = transPos;
		this.unitInv = unitInv;
		this.trade = true;

		regenButtons();
	}
	
	private void regenButtons() {
		items = unitInv.getInventory();
		noItems = unitInv.getInventorySize();
		
		equipped = unitInv.getEquipped();
		if(equipped != null) {
			equippedWeapon = new Button(pa.width/2 - 400, (0.1f) * pa.height, 400, 100, 255, 128, 255, equipped.getName(), pa);
		}
		else {
			equippedWeapon = new Button(pa.width/2 - 400, (0.1f) * pa.height, 400, 100, 255, 128, 255,"<empty>", pa);
		}
	
		if(noItems > 0) {
			if(trade && noItems < Constants.MAX_INVENTORY) {
				itemButtons = new Button[noItems + 1];
				itemButtons[noItems] = new Button(pa.width/2 - 400, (0.3f + (noItems * 0.05f)) * pa.height, 400, 100, 255, 128, 255, "<empty>", pa);
			}
			else {
				itemButtons = new Button[noItems];
			}
			for(int i = 0; i < noItems; i++) {
				itemButtons[i] = new Button(pa.width/2 - 400, (0.3f + (i * 0.05f)) * pa.height, 400, 100, 255, 128, 255, items[i].getName(), pa);
			}
		}
		else {
			itemButtons = new Button[1];
			noItems = 1;
			itemButtons[0] = new Button(pa.width/2 - 400, (0.3f) * pa.height, 400, 100, 255, 128, 255, "<empty>", pa);
		}
		selectedIndex = -1;
		equippedWeapon.setHighlighted();
	}
	
	private void setupItemButtons() {
		actionButtons = new Button[ACTION_BUTTONS];
		String useString = "";
		String moveString = "Move";
		if(trade) {
			moveString = "Trade";
		}
		if(selectedItem instanceof Weapon) {
			if(selectedIndex == -1) {
				useString = "Unequip";
			}
			else {
				useString = "Equip";
			}
		}
		else if(selectedItem instanceof UseItem){
			useString = "Use";
		}
		
		actionButtons[ACTION_USE] = new Button(pa.width/2 + 400, (0.3f + (ACTION_USE * 0.05f)) * pa.height, 200, 100, 255, 128, 255, useString, pa);
		actionButtons[ACTION_MOVE] = new Button(pa.width/2 + 400, (0.3f + (ACTION_MOVE) * 0.05f) * pa.height, 200, 100, 255, 128, 255, moveString, pa);
		actionButtons[ACTION_DISCARD] = new Button(pa.width/2 + 400, (0.3f + (ACTION_DISCARD) * 0.05f) * pa.height, 200, 100, 255, 128, 255, "Discard", pa);
		actionButtons[ACTION_BACK] = new Button(pa.width/2 + 400, (0.3f + (ACTION_BACK) * 0.05f) * pa.height, 200, 100, 255, 128, 255, "Back", pa);
		
		secondSelectedIndex = 0;
		actionButtons[secondSelectedIndex].setHighlighted();
	}
	
	public void update() {
		pa.pushMatrix();
		pa.translate(-transPos.x, -transPos.y);
		pa.fill(128, 128);
		pa.rect(0, 0, pa.width, pa.height);
		
		if(selectionMade) {
			for(int i = 0; i < ACTION_BUTTONS; i++) {
				actionButtons[i].display();
			}
		}
		
		for(int i = 0; i < itemButtons.length; i++) {
			itemButtons[i].display();
		}
		equippedWeapon.display();
		
		if(selectedIndex == -1) {
			displayItemInfo(equipped);
		}
		else {
			displayItemInfo(items[selectedIndex]);
		}
		
		pa.popMatrix();
		
		//Check key pressed
		if(pa.keyPressed == true) {
			pa.keyPressed = false;
			if(pa.key == PConstants.CODED) {
				switch(pa.keyCode) {
					case PConstants.UP:
						if(selectionMade) {
							actionButtons[secondSelectedIndex].setHighlighted();
							secondSelectedIndex = (secondSelectedIndex - 1);
							if(secondSelectedIndex < 0) {
								secondSelectedIndex = ACTION_BUTTONS - 1;
							}
							actionButtons[secondSelectedIndex].setHighlighted();
						}
						else {
							if(selectedIndex == -1) {
								equippedWeapon.setHighlighted();
							}
							else {
								itemButtons[selectedIndex].setHighlighted();
							}
							
							selectedIndex = (selectedIndex - 1);
							
							if(selectedIndex < -1) {
								selectedIndex = itemButtons.length - 1;
							}
							if(selectedIndex == -1) {
								equippedWeapon.setHighlighted();
							}
							else {
								itemButtons[selectedIndex].setHighlighted();
							}
						}
						break;
					case PConstants.DOWN:
						if(selectionMade) {
							actionButtons[secondSelectedIndex].setHighlighted();
							secondSelectedIndex = (secondSelectedIndex + 1) % ACTION_BUTTONS;
							actionButtons[secondSelectedIndex].setHighlighted();
						}
						else {
							if(selectedIndex == -1) {
								equippedWeapon.setHighlighted();
							}
							else {
								itemButtons[selectedIndex].setHighlighted();
							}
							
							if(isUnit && selectedIndex + 1 == itemButtons.length) {
								selectedIndex = -1;
							}
							else {
								selectedIndex = (selectedIndex + 1) % itemButtons.length;
							}
							
							if(selectedIndex == -1) {
								equippedWeapon.setHighlighted();
							}
							else {
								itemButtons[selectedIndex].setHighlighted();
							}
						}
						break;
				}
			}
			else {
				switch(pa.key) {
					case ' ':
						if(!selectionMade) {
							selectItem();
						}
						else {
							selectAction();
						}
						break;
					case 'e':
					case 'q':
						if(selectionMade) {
							selectionMade = false;
							break;
						}
						if(trade) {
							returnMenu.cancelSelection();
						}
						returnUMenu.back(false);
						break;
				}
			}
		}
	}
	
	private void displayItemInfo(Item item) {
		if(item == null) {
			return;
		}
		String display = item.toString();
		pa.fill(0xCC000b69);
		pa.rect(pa.width/2, 0.3f * pa.height, 300, 400);
		pa.fill(255);
		pa.textSize(14);
		pa.text(display, pa.width/2 + 150, 0.3f * pa.height + 50);
		pa.textSize(32);
	}
	
	private void selectItem() {
		if(moving) {
			if(selectedIndex == -1) {
				moving = false;
				return;
			}
			//Move item
			Item swap = items[currSelectedIndex];
			items[currSelectedIndex] = items[selectedIndex];
			items[selectedIndex] = swap;
			regenButtons();
			moving = false;
			selectionMade = false;
			return;
		}
		
		if(wasEmpty && selectedIndex > -1) {
			return;
		}
		
		currSelectedIndex = selectedIndex;
		if(isUnit && selectedIndex == -1) {
			selectedItem = unitInv.getEquipped();
		}
		else {
			selectedItem = items[selectedIndex];
		}
		selectionMade = true;
		setupItemButtons();
	}
	
	private void selectAction() {
		switch(secondSelectedIndex) {
			case ACTION_USE:
				if(selectedItem instanceof Weapon) {
					if(selectedIndex == -1) {
						unitInv.equipWeapon(null);
						unitInv.addToInventory(selectedItem);
						regenButtons();
						break;
					}
					
					if(unitInv.equipWeapon((Weapon)selectedItem)) {
						if(equipped == null) {
							unitInv.removeFromInventory(selectedIndex);
						}
						else {
							items[selectedIndex] = equipped;
						}
						equipped = (Weapon)selectedItem;
					}
					
					regenButtons();
				}
				else if(selectedItem instanceof UseItem) {
				UseItem use = (UseItem) selectedItem;
				if(!unitInv.hasEffect()) {
					unitInv.applyEffect(use.getAffectedStats(), use.getEffects(), use.getEffectTime());
					use.damage();
					if(use.getDurability() <= 0) {
						unitInv.removeFromInventory(selectedIndex);
					}
					unitInv.hasMoved();
					returnUMenu.back(true);
					}
				}
				break;
			case ACTION_MOVE:
				if(trade) {
					if(selectedIndex == -1) {
						return;
					}
					if(selectedIndex >= noItems) {
						returnMenu.confirmTrade(null, -1);
					}
					else {
						returnMenu.confirmTrade(items[selectedIndex], selectedIndex);
					}
					return;
				}
				moving = true;
				selectionMade = false;
				break;
			case ACTION_DISCARD:
				if(selectedIndex == -1) {
					unitInv.equals(null);
				}
				else {
					unitInv.removeFromInventory(selectedIndex);
				}
				regenButtons();
				break;
			case ACTION_BACK:
				selectionMade = false;
				break;
		}
		selectionMade = false;
	}
}
