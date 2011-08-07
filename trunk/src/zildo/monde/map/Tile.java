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

package zildo.monde.map;

/**
 * @author Tchegito
 *
 */
public class Tile implements Cloneable {

	public int index;
	public int bank;
	public Case parent;
	
	public Tile(int p_bank, int p_index, Case p_parent) {
		if (bank == 73) {
			throw new RuntimeException();
		}
		bank = p_bank & 63;
		index = p_index;
		parent = p_parent;
	}
	
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
		return "bank="+bank+" ; index="+index;
	}
}
