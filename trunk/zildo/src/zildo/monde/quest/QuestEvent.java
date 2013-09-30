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

package zildo.monde.quest;

public enum QuestEvent {

    LOCATION,	// Some one walks on a specific location 
    DIALOG, 	// Hero speaks to someone
    INVENTORY,	// Hero has a particular item 
    QUESTDONE, 	// Hero has completed a quest
    DEAD, 		// Some enemies have died
    PUSH, 		// Hero has pushed a block or something
    LIFT,		// Hero picks up some jar/bushes
    USE;		// Hero uses an object (flut for example)
    
    public static QuestEvent fromString(String p_name) {
    	for (QuestEvent kind : values()) {
    		if (kind.toString().equalsIgnoreCase(p_name)) {
    			return kind;
    		}
    	}
    	return null;
    }
}
