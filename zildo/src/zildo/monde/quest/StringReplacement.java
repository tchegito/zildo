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

package zildo.monde.quest;

import java.util.HashMap;
import java.util.Map;

import zildo.server.state.ScriptManagement;

/**
 * Just a simple class to encapsulate a <String, String> map.
 * It's used for map (=area) replacement according to a quest status. (See {@link ScriptManagement})
 * Same for music and character's names.
 * @author tchegito
 *
 */
public class StringReplacement {

	private static final long serialVersionUID = 1L;

	Map<String, String> map;
	
	public StringReplacement() {
        map = new HashMap<String, String>();
    }
    
    public String getValue(String p_name) {
        String name = map.get(p_name);
        if (name == null) {
            return p_name;
        } else {
            return name;
        }
    }
    
    public void put(String p_key, String p_value) {
    	map.put(p_key, p_value);
    }
}
