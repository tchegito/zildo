/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.monde.sprites.desc;

/**
 * @author Tchegito
 *
 */
public enum PersoBehaviorDescription {

	// 4 sprites : 2 for horizontal (WEST), and 1 for vertical with reverse
	CLASSIC_4ANGLES(4, true); //, new int[][] { {1}, {2}, {0}, {2}} );
	
	final int nbAngles;
	final boolean reverseHorizontal;
	//final int[][] seq;
	
	private PersoBehaviorDescription(int nbAngles, boolean reverseHorizontal) {
		this.nbAngles = nbAngles;
		this.reverseHorizontal = reverseHorizontal;
	}
}