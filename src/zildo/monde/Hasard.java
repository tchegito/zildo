/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

public class Hasard {

	// Note about the hazard in bushes :
	// We have to be careful to not giving bombs to Zildo if he doesn't have any yet. He MUST find
	// the weapon first, and after take some ammos.

	// Hazard
	public static final int hazardBushes_Diamant = 7;
	public static final int hazardBushes_Heart = 9;
	public static final int hazardBushes_Arrow = 9;
	public static final int hazardBushes_Bombs = 9;

	/**
	 * Renvoie VRAI si un lancer de dé à 10 faces fait plus de 'p_number'
	 * 
	 * @param p_number
	 */
	static public boolean lanceDes(int p_number) {
		return Math.random() * 10 > p_number;
	}

	/**
	 * Returns a number between -p_range/2 and p_range/2.
	 * 
	 * @param p_range
	 * @return int
	 */
	static public int intervalle(int p_range) {
		return (int) (Math.random() * p_range) - (p_range / 2);
	}

	/**
	 * Returns an integer n where 0 <= n < max.
	 * 
	 * @param max
	 * @return int
	 */
	static public int rand(int max) {
		return (int) (Math.random() * max);
	}
}
