package zildo.monde;

/**
 * Modelizes a saved game, or start game.
 * 
 * For now, it just describes two situations:
 * -simple game in a given map
 * -minimum management for map editing (ZEditor)
 * 
 * @author tchegito
 *
 */
public class Game {

	public boolean editing;
	public String mapName;
	
	public Game(String p_mapName, boolean p_editing) {
		mapName=p_mapName;
		editing=p_editing;
	}
}
