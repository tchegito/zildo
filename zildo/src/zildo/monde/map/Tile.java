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

package zildo.monde.map;

import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;


/**
 * @author Tchegito
 * 
 */
public class Tile implements Cloneable {

	public int index;
	public int renderedIndex;	// for animated tiles
	public byte bank;
	public byte previousBank;	// Save previous bank, in case of bank switching
	public Case parent;

	public Reverse reverse = Reverse.NOTHING;
	public Rotation rotation = Rotation.NOTHING;
	
	public enum TileNature {
		BOTTOMLESS,	// Lava
		WATER,	// Deep water, where character has to swim
		WATER_MUD,	// Little mud, where character can walk
		BUSH,	// A bushes (falling on it will cause it to blow)
		SWAMP,	// Swamp (squirrel can jump lower than on regular tiles)
		REGULAR;
	};

	public final static int T_WATER_FEW = 78 + 256*3;
    public final static int T_STUMP = 159;	// Stump's first tile
    public final static int T_HSTUMP = 225 + 256*6;	// Higher stump's first tile
    public final static int T_WATER_MUD = 224 + 256*6;
    public final static int T_BUSH = 165;
    public final static int T_BUSH_CUT = 166;
    public final static int T_PLOT = 173;
    public final static int T_SWAMP = 118 + 256;
    public final static int T_NETTLE = 6*256 + 231;
    public final static int T_NETTLE_CUT = 6*256 + 232;
    
	public Tile(int p_bank, int p_index, Case p_parent) {
		bank = (byte) (p_bank & 15);
		index = p_index;
		parent = p_parent;
	}

	public Tile(int p_value, Case p_parent) {
		set(p_value, Rotation.NOTHING);
		parent = p_parent;
	}
	
	public Tile(int p_value, Reverse p_rev, Case p_parent) {
		this(p_value, p_parent);
		reverse = p_rev;
	}
	
	public void set(int p_value, Rotation rot) {
		previousBank = bank;
		index = p_value & 255;
		bank = (byte) ((p_value >> 8) & 15);
		rotation = rot;
	}
	
	@Override
	public Tile clone() {
		try {
			return (Tile) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unable to clone Tile");
		}
	}

	public int getValue() {
		int a = bank & 31;
		int b = index;
		a = a << 8;
		return a + b;
	}

	@Override
	public String toString() {
		return "bank=" + bank + " ; index=" + index;
	}
	
	public static boolean isClosedChest(int value) {
		switch (value) {
		case 512 + 231: // Chest
		case 512 + 49: case 512 + 59: case 512 + 61:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Return the corresponding tile value from closed to opened chest.
	 * @param value
	 * @return int
	 */
	public static int getOpenedChest(int value) {
		switch (value) {
		case 512 + 231: return 512 + 238;
		case 512 + 49:	return 512 + 48;
		case 512 + 59:	return 512 + 58;
		case 512 + 61:	return 512 + 60;
		default:	return 0;
		}
	}
	
	public static boolean isButton(int value) {
		switch (value) {
		case 768 + 212: case 768 + 213:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isBottomLess(int value) {
		return (value == 256 * 3 + 217 || value == 41 + 256 * 9);
	}
	
	/** Returns TRUE if given tile value could raise/lower hero from one floor to another (ex:ladder)
	 *  This is only valid tiles walkable out of a cutscene. **/
	public static boolean isTransitionnable(int value) {
		return value == 206 || value == 207 || value == 7*256+134 || value == 7*256+133 ||
				value == 7*256+135 || value == 7*256+136;
	}
	
	public static boolean isPickableTiles(int value) {
		switch (value) {
		case 165:
		case 167:
		case 169:
		case 751:
			return true;
		default:
			return false;
		}
	}
}
