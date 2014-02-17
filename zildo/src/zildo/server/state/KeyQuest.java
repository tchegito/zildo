package zildo.server.state;

import zildo.monde.map.ChainingPoint;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.util.Point;

/**
 * This class provides quest name generation for all special events which need to be saved in player game.
 * 
 * @author ebn
 *
 */
public class KeyQuest {

	
	/**
	 * Build a quest's keyname about a chaining point between 2 maps.
	 * @param p_mapName
	 * @param p_ch
	 * @return String
	 */
	public String buildKeyDoor(String p_mapName, ChainingPoint p_ch) {
		String map2=p_ch.getMapname();
		String key=p_mapName.compareTo(map2) < 0 ? p_mapName+map2 : map2+p_mapName;
		key+=p_ch.getOrderX()+p_ch.getOrderY();

		return key;
	}
	
	public String buildChest(String p_mapName, Point p_location) {
		return p_mapName+p_location.toString();
	}

	/**
	 * Build a quest's keyname about a taken item.
	 * @return String
	 */
	public String buildKeyItem(String p_mapName, int p_x, int p_y, ElementDescription p_desc) {
		return p_mapName + p_x + p_y + p_desc.toString();
	}
	
	public String buildExplosion(String p_mapName, Point p_loc) {
		return p_mapName + p_loc.toString();
	}
}
