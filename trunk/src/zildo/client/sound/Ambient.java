/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

/**
 * Class managing all ambient sounds : music and SFX.
 * Depends on : current map, and scenaristic movements.
 * @author Tchegito
 *
 */
public class Ambient {

	Map<String, BankMusic> ambientMusic=new HashMap<String, BankMusic>();
	
	BankMusic currentMusic=null;
	
	public Ambient() {
		ambientMusic.put("polakyg", BankMusic.Grotte);
		ambientMusic.put("polakyg2", BankMusic.Grotte);
		ambientMusic.put("polakyg3", BankMusic.Grotte);
		ambientMusic.put("polaky4", BankMusic.Grotte);
		ambientMusic.put("polaky5", BankMusic.Grotte);
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
}
