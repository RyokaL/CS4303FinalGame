package weapons;

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
	int maxRarity = -1;
	
	public Blacksmith(String pathToClassFiles, final PApplet pa) throws IOException {
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
		
	}
	
	public ArrayList<Weapon> getRarityList(int rarity) {
		return rarityTable.get(rarity);
	}
	
	public int getMaxRarity() {
		return maxRarity;
	}
	
	public Weapon getWeaponObj(String weaponName) {
		Weapon toCopy =  weaponList.get(weaponName);
		Weapon newCopy = new Weapon(toCopy.getName(), toCopy.getAttack(), toCopy.getHitRate(), toCopy.getCriticalRate(), toCopy.getMaxRange(), toCopy.getMinRange(), toCopy.getDurability(), toCopy.isHealing(), toCopy.getWeaponType(), toCopy.getRarity());
		return newCopy;
	}
	
	public Set<String> getWeaponNames() {
		return weaponList.keySet();
	}
}
