package map;

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
import com.google.gson.JsonSyntaxException;

import processing.core.PApplet;

public class MapStore {
HashMap<String, Map> mapList;
	
	public MapStore(String pathToClassFiles, final PApplet pa) throws IOException {
		Gson gson = new GsonBuilder().create();
		mapList = new HashMap<String, Map>();
		
		List<String> classFiles = Files.walk(Paths.get(pathToClassFiles))
			.filter(Files::isRegularFile)
			.map(x -> x.toString())
			.collect(Collectors.toList());
		for(String file : classFiles) {
			Map nextMap = gson.fromJson(new FileReader(file), Map.class);
			nextMap.loadMap(pa);
			nextMap.loadTiles();
			mapList.put(nextMap.getName(), nextMap);
		}
		
	}
	
	public Map getMapObj(String mapName) {
		return mapList.get(mapName);
	}
	
	public Set<String> getMapNames() {
		return mapList.keySet();
	}
}
