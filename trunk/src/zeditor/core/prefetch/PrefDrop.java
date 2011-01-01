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

package zeditor.core.prefetch;

import zildo.monde.map.Point;

/**
 * Description of all the droppable prefetch (ex:tree, rock, ...)
 * 
 * @author Tchegito
 *
 */
public enum PrefDrop {

	CollineGauche(new Point(2,4),new int[] {27,40, 37,41, 38,54, 39,54}), 
	CollineDroite(new Point(2,4), new int[] {42, 35, 43, 44, 54, 45, 54, 46}), 
	PetitChemin(new Point(2, 2), new int[] {8, 10, 14, 12}),	// Use by PrefTraceDrop
	GrandChemin(new Point(3, 3), new int[] {8, 9, 10, 15, 24, 11, 14, 13, 12}), // Use by PrefTraceDrop
	Arbre(new Point(4, 5), new int[] {-139, -140, -141, -142, -143, -144, -145, -146, 
		-147, -148, -149, -150, 151, 152, 153, 154, 155, 156, 157, 158}),
	Souche(new Point(2, 2), new int[] {159, 160, 161, 162}),
	Statue(new Point(2, 3), new int[] {-186, -187, 188, 189, 190, 191}), 
	ArcheVillage(new Point(5, 3), new int[] {-376, -377, -378, -379, -380, 381, -382, -383, -384, 381, 385, 386, 386, 386, 385}),
	GrossePierre(new Point(2, 2), new int[] {196, 197, 198, 199}),
	ArbreRouge(new Point(4, 5), new int[] {-1042, -1043, -1044, -1045, -1046, -1047, -1048, -1049,
		-1050, -1051, -1052, -1053, 1054, 1055, 1056, 1057, 155, 156, 157, 158}),
	ArbreJaune(new Point(4, 5), new int[] {-1058, -1059, -1060, -1061, -1062, -1063, -1064, -1065,
		-1066, -1067, -1068, -1069, 1070, 1071, 1072, 1073, 155, 156, 157, 158});

	
	Point size;
	public int[] data;	// Negative values mean masked tiles (example: tree)
	
	private PrefDrop(Point p_size, int[] p_data) {
		size=p_size;
		data=p_data;
	}
	
	public static PrefDrop fromPrefetch(Prefetch p_pref) {
		for (PrefDrop p : values()) {
			if (p.name().equals(p_pref.name())) {
				return p;
			}
		}
		throw new RuntimeException("Impossible de trouver le PrefDrop associé à "+p_pref.name());
	}
}
