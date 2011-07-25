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

package zildo.monde.sprites.utils;

import java.util.ArrayList;
import java.util.List;

public enum MouvementPerso {

	// Mouvement des persos
	ZONE(0),
	POULE(1),
	OBSERVE(2),
	IMMOBILE(3),
	VOLESPECTRE(4),
	RAT(5),
	ZONELANCE(6),
	ZONEARC(7),
	ELECTRIQUE(8),
	ABEILLE(9),
	OISEAU(10),
	LAPIN(11);
	
	public int valeur;
	
	private MouvementPerso(int val) {
		this.valeur=val;
	}
	
	public static MouvementPerso fromInt(int a) {
		for (MouvementPerso mvt : MouvementPerso.values()) {
			if (mvt.valeur == a) {
				return mvt;
			}
		}
		throw new RuntimeException("Le script de mouvement "+a+" n'existe pas.");
	}
	
	/**
	 * Does this script make the character collide obstacles ?
	 * @return TRUE = no collision / FALSE = collision with background
	 */
	public boolean isFlying() {
		return this == VOLESPECTRE || this == OISEAU;
	}
	
	public static String[] getValues() {
		List<String> str=new ArrayList<String>();
		for (MouvementPerso mvt : values()) {
			str.add(mvt.name());
		}
		return str.toArray(new String[]{});
	}
	
	/**
	 * Does this script make the character focus on his target, or out of his way ? 
	 * @return TRUE = character runs away / FALSE = character runs on his target
	 */
	public boolean isAfraid() {
		return this == POULE;
	}
	/**
	 * Does this script make the character move diagonally ?
	 * @return TRUE = diagonal move / FALSE = lateral move
	 */
	public boolean isDiagonal() {
		return this == POULE ||
		this == VOLESPECTRE ||
		this == ELECTRIQUE;
	}
}
