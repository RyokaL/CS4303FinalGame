package items;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import processing.core.PApplet;

public class Blacksmith {
	
	HashMap<String, Weapon> weaponList;
	HashMap<Integer, ArrayList<Weapon>> rarityTable;
	
	HashMap<String, UseItem> itemList;
	HashMap<Integer, ArrayList<UseItem>> useRarityTable;
	int maxRarity = -1;
	int itemMaxRarity = -1;
	
	public Blacksmith(String pathToClassFiles, String pathToItems, final PApplet pa) throws IOException {
		Gson gson = new GsonBuilder().create();
		weaponList = new HashMap<String, Weapon>();
		rarityTable = new HashMap<Integer, ArrayList<Weapon>>();
		
		List<String> classFiles = Files.walk(Paths.get(pathToClassFiles))
			.filter(Files::isRegularFile)
			.map(x -> x.toString())
			.collect(Collectors.toList());
		for(String file : classFiles) {
			//Create all weapon list
			Weapon nextWeapon = gson.fromJson(new FileReader(file), Weapon.class);
			weaponList.put(nextWeapon.getName(), nextWeapon);
			
			//Create loot table
			int rarity = nextWeapon.getRarity();
			if(rarity == -1) {
				continue;
			}
			
			if(maxRarity < rarity) {
				maxRarity = rarity;
			}
			ArrayList<Weapon> rarityList = rarityTable.get(rarity);
			if(rarityList == null) {
				rarityList = new ArrayList<Weapon>();
				rarityTable.put(rarity, rarityList);
			}
			rarityList.add(nextWeapon);
		}
		
		itemList = new HashMap<String, UseItem>();
		useRarityTable = new HashMap<Integer, ArrayList<UseItem>>();
		
		List<String> itemFiles = Files.walk(Paths.get(pathToItems))
				.filter(Files::isRegularFile)
				.map(x -> x.toString())
				.collect(Collectors.toList());
			for(String file : itemFiles) {
				//Create all weapon list
				UseItem nextItem = gson.fromJson(new FileReader(file), UseItem.class);
				itemList.put(nextItem.getName(), nextItem);
				
				//Create loot table
				int rarity = nextItem.getRarity();
				if(rarity == -1) {
					continue;
				}
				
				if(itemMaxRarity < rarity) {
					itemMaxRarity = rarity;
				}
				ArrayList<UseItem> rarityList = useRarityTable.get(rarity);
				if(rarityList == null) {
					rarityList = new ArrayList<UseItem>();
					useRarityTable.put(rarity, rarityList);
				}
				rarityList.add(nextItem);
			}
		
	}
	
	public ArrayList<Weapon> getRarityList(int rarity) {
		return rarityTable.get(rarity);
	}
	
	public ArrayList<UseItem> getItemRarityList(int rarity) {
		return useRarityTable.get(rarity);
	}
	
	public int getMaxItemRarity() {
		return itemMaxRarity;
	}
	
	public int getMaxRarity() {
		return maxRarity;
	}
	
	public Weapon getWeaponObj(String weaponName) {
		Weapon toCopy =  weaponList.get(weaponName);
		Weapon newCopy = new Weapon(toCopy);
		return newCopy;
	}
	
	public UseItem getItemObj(String itemName) {
		UseItem toCopy = itemList.get(itemName);
		UseItem newCopy = new UseItem(toCopy);
		return newCopy;
	}
	
	public List<Weapon> getWeaponList() {
		return weaponList.values().stream().collect(Collectors.toList());
	}
	
	public List<UseItem> getItemList() {
		return itemList.values().stream().collect(Collectors.toList());
	}
	
	public Set<String> getWeaponNames() {
		return weaponList.keySet();
	}
	
	public Set<String> getItemNames() {
		return itemList.keySet();
	}
}
