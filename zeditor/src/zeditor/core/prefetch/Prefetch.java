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

package zeditor.core.prefetch;

import java.util.ArrayList;
import java.util.List;

/**
 * Note: How add a new prefetch ?
 *  1) add a reference here, as an enum element, to get it shown in the "Prefetch" tab
 *  2) add an enum element in corresponding class (ex: PrefTraceDrop, PrefDrop...)
 *  
 * @author Tchegito
 *
 */
public enum Prefetch {

	PetiteColline(PrefKind.TraceDrop), 
	GrandeColline(PrefKind.TraceDrop), 
	CollineGauche(PrefKind.Drop), CollineDroite(PrefKind.Drop), CollineBordure(PrefKind.Colour),
	CollineMarron(PrefKind.Colour), PetitChemin(PrefKind.TraceDrop), GrandChemin(PrefKind.TraceDrop), Eau(PrefKind.TraceDrop),
	
	Arbre(PrefKind.Drop), ArbreMalade(PrefKind.Drop), 
	Souche(PrefKind.Drop), Statue(PrefKind.Drop), ArcheVillage(PrefKind.Drop), GrossePierre(PrefKind.Drop),
	MaisonRouge(PrefKind.TraceDrop), MaisonBleue(PrefKind.Colour), MaisonVerte(PrefKind.Colour),
	ArbreRouge(PrefKind.Drop), ArbreJaune(PrefKind.Drop),
	RouteDesert(PrefKind.TraceDrop),
	PetiteLisiere(PrefKind.TraceDrop), GrandeLisiere(PrefKind.TraceDrop),
	PalaisHaut(PrefKind.TraceDrop),
	PalaisBas(PrefKind.TraceDrop),
	Souterrain(PrefKind.TraceDrop),
	SouterrainBas(PrefKind.TraceDrop),
	MaisonIntRouge(PrefKind.TraceDrop),
	MaisonIntViolet(PrefKind.TraceDrop),
	PalaceNature(PrefKind.TraceDrop);
	
	PrefKind kind;
	
	private Prefetch(PrefKind p_kind) {
		kind = p_kind;	
	}
	
	static public String[] getNames() {
		List<String> names=new ArrayList<String>();
		StringBuilder sb;
		for (Prefetch pref : values()) {
			String name=pref.name();
			sb=new StringBuilder();
			for (int i=0;i<name.length();i++) {
				char a=name.charAt(i);
				if (Character.isUpperCase(a) && i>0) {
					sb.append(" ");
				}
				sb.append(Character.toLowerCase(a));
			}
			names.add(sb.toString());
		}
		return names.toArray(new String[]{});
	}
	
	public static Prefetch fromInt(int i) {
		return values()[i];
	}
}
