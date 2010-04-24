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

package zildo.monde;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.fwk.script.xml.AdventureElement;

/**
 * Modelizes a saved game, or start game. For now, it describes:<br/>
 * -simple game in a given map <br/>
 * -minimum management for map editing (ZEditor) <br/>
 * -deathmatch/cooperative nature<br/>
 * -current quest diary<br/>
 * @author tchegito
 */
public class Game implements EasySerializable {

    public boolean editing;
    public boolean multiPlayer;
    public boolean deathmatch; // Defines the game rules
    public String mapName;
    public AdventureElement adventure;
    
    public Game(String p_mapName, boolean p_editing) {
        mapName = p_mapName;
        editing = p_editing;
        multiPlayer = false;
    }

    public Game(boolean p_editing) {
    	this(null, p_editing);
    }
    
	public void serialize(EasyBuffering p_buffer) {
		
	}
}
