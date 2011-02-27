/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

package zildo.client.sound;

import java.util.HashMap;
import java.util.Map;

import zildo.monde.map.Area;

/**
 * Class managing all ambient things: <ul>
 * <li>sounds : music and SFX</li>
 * <li>weather : clouds, rain</li>
 * </ul>
 * Depends on : current map, and scenaristic movements.
 * @author Tchegito
 *
 */
public class Ambient {

	Map<String, BankMusic> ambientMusic=new HashMap<String, BankMusic>();
	
	BankMusic currentMusic=null;
	
	public enum Weather {
		USUAL, CLOUD;
	}
	
	public Ambient() {
		ambientMusic.put("polakyg", BankMusic.Grotte);
		ambientMusic.put("polakyg2", BankMusic.Grotte);
		ambientMusic.put("polakyg3", BankMusic.Grotte);
		ambientMusic.put("polakyg4", BankMusic.Grotte);
		ambientMusic.put("polaky4", BankMusic.Grotte);
		ambientMusic.put("polaky5", BankMusic.Grotte);
		ambientMusic.put("d4m5", BankMusic.PianoBar);
		ambientMusic.put("d4m4", BankMusic.PianoBar);
	}
	
	public BankMusic getMusicForMap(String p_mapName) {
		BankMusic mus=ambientMusic.get(p_mapName.toLowerCase());
		if (mus == null)  {
			// Default is 'Village'
			mus=BankMusic.Village;
		}
		return mus;
	}
	
	public BankMusic getCurrentMusic() {
		return currentMusic;
	}
	
	public void setCurrentMusic(BankMusic p_mus) {
		currentMusic=p_mus;
	}
	
	/**
	 * Returns the map's weather. (default : 64x64 map have clouds)
	 * @param p_map
	 * @return Weather
	 */
	public Weather getWeather(Area p_map) {
		if (p_map.getDim_x() == 64 && p_map.getDim_y() == 64) {
			return Weather.CLOUD;
		} else {
			return Weather.USUAL;
		}
	}
}
