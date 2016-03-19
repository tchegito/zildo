/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package zildo.server.state;

import zildo.monde.map.ChainingPoint;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.util.Point;

/**
 * This class provides quest name generation for all special events which need to be saved in player game.
 * 
 * @author tchegito
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
	public String buildKeyItem(String p_mapName, Point p_location, String p_persoName, ElementDescription p_desc) {
		String ident = "";
		if (p_location != null) {
			ident += p_location.x;
			ident += p_location.y; 
		} else if (p_persoName != null) {
			ident += p_persoName;
		} else {
			throw new RuntimeException("Location or character's name should be provided on map "+p_mapName+" for element "+p_desc+" !");
		}
		return p_mapName + ident + p_desc.toString();
	}
	
	public String buildExplosion(String p_mapName, Point p_loc) {
		String s = p_mapName + p_loc.toString();
		return s;
	}
}
