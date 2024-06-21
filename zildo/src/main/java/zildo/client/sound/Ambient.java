/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.client.sound;

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

	BankMusic currentMusic=null;
	
	public enum Weather {
		USUAL, CLOUD;
	}
	
	public enum Atmosphere {
		OUTSIDE(true, BankMusic.Village), // 0
		CAVE(false, BankMusic.Grotte), // 1
		HOUSE(false, BankMusic.Village), // 2
		BAR(false, BankMusic.PianoBar),	// 3
		DESERT(false, BankMusic.Valori), // 4
		CASTLE(false, BankMusic.Chateau),
		CASTLEINSIDE(false, BankMusic.Chateau),
		DANGERINSIDE(false, BankMusic.Surprise),
		UNDERGROWTH(true, BankMusic.Squirrel),
		NATUREPALACE(false, BankMusic.Nature),
		LAVACAVE(false, BankMusic.Grotte);
		
		public boolean outside;
		public BankMusic music;
		private Atmosphere(boolean p_outside, BankMusic p_music) {
			outside = p_outside;
			music = p_music;
		}
		
		public int getEmptyTile() {
			switch (this) {
			default:
				return 54;	// Green herb;
			case HOUSE:
			case BAR:
			case CAVE:
			case DANGERINSIDE:
				return 512;
			case DESERT:
			    return 256*5 + 2;
			case CASTLEINSIDE:
				return 256*7 + 96;
			case LAVACAVE:
				return 256*10 + 34;
			case NATUREPALACE:
				return 256*7 + 96;
			}
		}
	}
	
	public Ambient() {
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
		switch (p_map.getAtmosphere()) {
			case OUTSIDE:
			case CASTLE:
			case UNDERGROWTH:
			case DESERT:
				return Weather.CLOUD;
			default:
				return Weather.USUAL;
		}
	}
}
