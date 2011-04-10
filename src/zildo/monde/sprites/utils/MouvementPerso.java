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
import java.util.Arrays;
import java.util.List;

public enum MouvementPerso {

	// Mouvement des persos
	SCRIPT_ZONE(0),
	SCRIPT_POULE(1),
	SCRIPT_OBSERVE(2),
	SCRIPT_IMMOBILE(3),
	SCRIPT_VOLESPECTRE(4),
	SCRIPT_RAT(5),
	SCRIPT_ZONELANCE(6),
	SCRIPT_ZONEARC(7),
	SCRIPT_ELECTRIQUE(8),
	SCRIPT_ABEILLE(9),
	SCRIPT_OISEAU(10),
	SCRIPT_LAPIN(11);
	
	public int valeur;
	
	public static List<MouvementPerso> persoDiagonales=Arrays.asList(SCRIPT_POULE,SCRIPT_VOLESPECTRE,SCRIPT_ELECTRIQUE);
	
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
	
	public boolean isFlying() {
		return this == SCRIPT_VOLESPECTRE || this == SCRIPT_OISEAU;
	}
	
	public static String[] getValues() {
		List<String> str=new ArrayList<String>();
		for (MouvementPerso mvt : values()) {
			str.add(mvt.name());
		}
		return str.toArray(new String[]{});
	}
}
