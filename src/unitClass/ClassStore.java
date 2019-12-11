package unitClass;

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

public class ClassStore {
	
	HashMap<String, UnitClass> classList;
	HashMap<Integer, ArrayList<UnitClass>> classStageTable;
	
	private int maxStrength;
	
	public ClassStore(String pathToClassFiles, final PApplet pa) throws IOException {
		Gson gson = new GsonBuilder().create();
		classList = new HashMap<String, UnitClass>();
		classStageTable = new HashMap<Integer, ArrayList<UnitClass>>();
		
		List<String> classFiles = Files.walk(Paths.get(pathToClassFiles))
			.filter(Files::isRegularFile)
			.map(x -> x.toString())
			.collect(Collectors.toList());
		for(String file : classFiles) {
			//Create all class list
			UnitClass nextClass = gson.fromJson(new FileReader(file), UnitClass.class);
			nextClass.loadSprites(pa);
			classList.put(nextClass.getName(), nextClass);
			
			//Create stage/strength table
			int strength = nextClass.getUpgradeStage();
			if(maxStrength < strength) {
				maxStrength = strength;
			}
			ArrayList<UnitClass> strengthList = classStageTable.get(strength);
			if(strengthList == null) {
				strengthList = new ArrayList<UnitClass>();
				classStageTable.put(strength, strengthList);
			}
			strengthList.add(nextClass);
		}
		
	}
	
	public UnitClass getClassObj(String className) {
		return classList.get(className);
	}
	
	public Set<String> getClassNames() {
		return classList.keySet();
	}
}
