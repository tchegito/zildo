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
	
	public Tile(int p_bank, int p_index, Case p_parent) {
		bank = (byte) (p_bank & 15);
		index = p_index;
		parent = p_parent;
	}

	public Tile(int p_value, Case p_parent) {
		bank = (byte) ((p_value >> 8) & 15);
		index = p_value & 255;
		parent = p_parent;
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
}
