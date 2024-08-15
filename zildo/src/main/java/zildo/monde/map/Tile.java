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
		/** Lava **/ 
		BOTTOMLESS,	
		/** Fall but there's a floor under, on another map (need chaining point, see cavef9 for example) **/ 
		BOTTOMFLOOR,
		/** Fall but hero can jump on the floor (in dragon's cave for example)**/
		BOTTOMJUMP,
		/** Another floor below, but hero can't jump (rope on the bridge for example)**/
		BOTTOMNOJUMP,
		/** Deep water, where character has to swim**/ 
		WATER,	
		/** Little mud, where character can walk **/ 
		WATER_MUD,	
		/** A bushes (falling on it will cause it to blow) **/
		BUSH,	
		/** Swamp (squirrel can jump lower than on regular tiles) **/
		SWAMP, 
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
    public final static int T_BONES1 = 256*3+249;
    public final static int T_BONES2 = 256*3+250;
    public final static int T_LAVA = 256*10 + 34; 
    public final static int T_NATUREPALACE_PLATFORM = 256*9 + 174;
    public final static int T_NATUREPALACE_PLATFORM2 = 256*9 + 175;
    		
	public Tile(int p_bank, int p_index, Case p_parent) {
		bank = (byte) (p_bank & 15);
		index = p_index;
		parent = p_parent;
	}

	public Tile(int p_value, Case p_parent) {
		set(p_value, Rotation.NOTHING, Reverse.NOTHING);
		parent = p_parent;
	}
	
	public Tile(int p_value, Reverse p_rev, Case p_parent) {
		this(p_value, p_parent);
		reverse = p_rev;
	}
	
	public void set(int p_value, Rotation rot, Reverse rev) {
		previousBank = bank;
		index = p_value & 255;
		bank = (byte) ((p_value >> 8) & 15);
		rotation = rot;
		reverse = rev;
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
	
	public static boolean isBottomLess(int value, int back2Value) {
		// Means that hero fall and have to lost HP
		switch (value) {
		case 256 * 3 + 217:
			// Void in nature palace
		case 39 + 256 * 9: case 40 + 256 * 9: 		case 41 + 256 * 9:
		case T_LAVA:
			// Rock forming moutains on top of lava
		case 256*10 + 7:
		case 256*10 + 6:
		case 256*10 + 60:
		case 256*10 + 61:
		case 256*10 + 62:

		case 256*10 + 66:
		case 256*10 + 67:
		case 256*10 + 68:
		case 256*10 + 69:
		case 256*10 + 77:
		case 256*10 + 78:
		case 256*10 + 79:
		case 256*10 + 80:
		case 256*10 + 81:
		case 256*10 + 82:
			// Nature palace
		case 256*9 + 31:
		case 256*9 + 33:
		case 256*9 + 35:
		case 256*9 + 37:
			return true;
		case 108:	// 'Ponton' case: consider bottomless only if 'back2' tile exists
			return back2Value != -1;
		default:
			return false;
		}
	}
	
	public static boolean isBottomFloor(int value) {
		// Means that hero will fall on another map's floor
		return value == 256*3 + 125;
	}
	
	public static boolean isBottomJump(int value) {
		// Means that hero will fall on another map's floor
		return value == 256 * 6 + 35;
	}
	
	public static boolean isBottomNoJump(int value) {
		// Means that hero will fall on another map's floor
		return value == 256 * 6 + 36;
	}
	
	/** Returns TRUE if given tile value could raise/lower hero from one floor to another (ex:ladder)
	 *  This is only valid tiles walkable out of a cutscene. **/
	public static boolean isTransitionnable(int value) {
		return value == 206 || value == 207 || value == 7*256+134 || value == 7*256+133 ||
				value == 7*256+135 || value == 7*256+136;
	}
	
	/** Returns TRUE for tiles where hero has to be masked, because ceiling is too low **/
	public static boolean isTransitionnableMasked(int value) {
		return value == 9*256+181;
	}
	
	/** Tile where hero can pick up something (ex: jar, bushes ...) **/
	public static boolean isPickableTiles(int value) {
		switch (value) {
		case 165:
		case 167:
		case 169:
		case 751:
		case 256*5+195:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isOpenedChest(int tileDesc) {
		switch (tileDesc) {
			case 512 + 238: // Opened chest (don't spawn the linked item)
			case 512 + 48: case 512 + 58: case 512+60:
				return true;
			default:
				return false;
		}
	}
		
	public static boolean isLinkableToItem(int tileDesc) {
		if (isClosedChest(tileDesc)) {
			return true;
		}
		switch (tileDesc) {
			case 165: // Bushes
			case 167: // Stone
			case 169: // Heavy stone
			case 751: // Jar
			case 256*5+195:	// Amphora
				return true;
			default:
				return false;
		}
	}
}
