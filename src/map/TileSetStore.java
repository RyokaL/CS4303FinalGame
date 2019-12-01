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

import processing.core.PApplet;
import unitClass.UnitClass;

public class TileSetStore {

	static HashMap<String, Tile> tileList;
	
	public TileSetStore(String pathToClassFiles, final PApplet pa) throws IOException {
		Gson gson = new GsonBuilder().create();
		tileList = new HashMap<String, Tile>();
		
		List<String> classFiles = Files.walk(Paths.get(pathToClassFiles))
			.filter(Files::isRegularFile)
			.map(x -> x.toString())
			.collect(Collectors.toList());
		for(String file : classFiles) {
			Tile nextTile = gson.fromJson(new FileReader(file), Tile.class);
			tileList.put(nextTile.getName(), nextTile);
		}
		
	}
	
	public static Tile getTileObj(String className) {
		return tileList.get(className);
	}
	
	public Set<String> getTileNames() {
		return tileList.keySet();
	}
}
