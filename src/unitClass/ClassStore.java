package unitClass;

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

public class ClassStore {
	
	HashMap<String, UnitClass> classList;
	
	public ClassStore(String pathToClassFiles, final PApplet pa) throws IOException {
		Gson gson = new GsonBuilder().create();
		classList = new HashMap<String, UnitClass>();
		
		List<String> classFiles = Files.walk(Paths.get(pathToClassFiles))
			.filter(Files::isRegularFile)
			.map(x -> x.toString())
			.collect(Collectors.toList());
		for(String file : classFiles) {
			UnitClass nextClass = gson.fromJson(new FileReader(file), UnitClass.class);
			nextClass.loadSprites(pa);
			classList.put(nextClass.getName(), nextClass);
		}
		
	}
	
	public UnitClass getClassObj(String className) {
		return classList.get(className);
	}
	
	public Set<String> getClassNames() {
		return classList.keySet();
	}
}
