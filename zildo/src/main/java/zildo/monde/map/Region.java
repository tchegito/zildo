/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.monde.map;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import zildo.fwk.ui.UIText;

/**
 * @author Tchegito
 *
 */
public enum Region {

	Lugdunia("coucou", "d4m*", "ferme*", "bucherons", "d5*"),
	LugduniaForest("foret", "foretb", "bosquet", "promenade", "buchforet", "igorlily"),
	LugduniaCastle("prisonext", "chateaucoucou*", "chateausud", "chatcou*"),
	LugduniaCave("foretg", "foretg2", "lugduniag", "bucheronsg"),
	Fishermen("igorvillage", "igorv*"),
	LugduniaPrison("prison*"),
	ClearingOaks("promenade2"),
	ThievesCamp("voleurs", "voleursm*", "voleurscave"),
	CaveFlames("voleursg*", "cavef*"),
	CaveFlamesDragon("dragon*"),
	Polaky("polaky", "polaky1", "polaky2", "polaky3", "polakym", "bosquetm", "bosquetm2"),
	PolakyCave("polaky4", "polaky5", "polakyg*"),
	Sousbois("sousbois*"),
	ForetBouleaux("eleo*"),
	NaturePalace("nature*"),
	Special("preintro", "sudouest", "sudest", "sudsudest"),
	Valori("canyon*", "valori*"),
	NeedToBeRemoved("lavacave", "maptest"); // Design purpose=> need to be removed
	
	String[] mapNames;
	
	static Map<String, Region> computedMap = null;
	
	private Region(String... mapNames) {
		this.mapNames = mapNames;
	}
	
	public static Region fromMapName(String map) {
		if (computedMap == null) {
			// Browse all areas to fill the hashmap
			computedMap = new HashMap<String, Region>();
			String[] areas = Area.findAllAreasName();
			for (Region r : values()) {
				for (String s : r.mapNames) {
					if (!s.contains("*")) {
						computedMap.put(s, r);
					} else {
						// Find all maps respecting the regex
						Pattern p = Pattern.compile(s.replaceAll("\\*", ".*"));
						for (String area : areas) {
							if (p.matcher(area).matches() && computedMap.get(area) == null) {
								computedMap.put(area, r);
							}
						}
					}
				}
			}
		}
		return computedMap.get(map);
	}
	
	public String getName() {
		return UIText.getGameText("REGION_"+toString());		
	}
}
