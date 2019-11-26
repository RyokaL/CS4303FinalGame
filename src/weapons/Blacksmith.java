package weapons;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import processing.core.PApplet;

public class Blacksmith {
	
	HashMap<String, Weapon> weaponList;
	
	public Blacksmith(String pathToClassFiles, final PApplet pa) throws IOException {
		Gson gson = new GsonBuilder().create();
		weaponList = new HashMap<String, Weapon>();
		
		List<String> classFiles = Files.walk(Paths.get(pathToClassFiles))
			.filter(Files::isRegularFile)
			.map(x -> x.toString())
			.collect(Collectors.toList());
		for(String file : classFiles) {
			Weapon nextWeapon = gson.fromJson(new FileReader(file), Weapon.class);
			weaponList.put(nextWeapon.getName(), nextWeapon);
		}
		
	}
	
	public Weapon getWeaponObj(String weaponName) throws CloneNotSupportedException {
		return weaponList.get(weaponName).clone();
	}
	
	public Set<String> getWeaponNames() {
		return weaponList.keySet();
	}
}
