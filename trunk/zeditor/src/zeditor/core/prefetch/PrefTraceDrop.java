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

package zeditor.core.prefetch;

import zeditor.core.prefetch.complex.CompositePatch12;
import zeditor.core.prefetch.complex.TraceDelegateDraw;
import zeditor.core.prefetch.patch.CastleLow;
import zeditor.core.prefetch.patch.CastleLow2;
import zeditor.core.prefetch.patch.CastleMiddle1;
import zeditor.core.prefetch.patch.CastleMiddle2;
import zeditor.core.prefetch.patch.CastleUp;
import zeditor.core.prefetch.patch.CaveFloor;
import zeditor.core.prefetch.patch.CaveLow;
import zeditor.core.prefetch.patch.CaveMiddle;
import zeditor.core.prefetch.patch.CaveMiddle2;
import zeditor.core.prefetch.patch.CaveTop;
import zeditor.core.prefetch.patch.ForestBorder;
import zeditor.core.prefetch.patch.HillTop;
import zeditor.core.prefetch.patch.Road;
import zeditor.core.prefetch.patch.Water;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public enum PrefTraceDrop {

	PetiteColline(new Point(4, 5), new HillTop(false)), 
	GrandeColline(new Point(4, 5), new HillTop(true)), 
	PetitChemin(new Point(2, 2), new Road(false)),
	GrandChemin(new Point(3, 3), new Road(true)),
	PetiteLisiere(new Point(2, 2), new ForestBorder(false)),
	GrandeLisiere(new Point(3, 3), new ForestBorder(true)),
	Eau(new Point(3, 3), new Water()),
	PalaisBas(new Point(9, 9), new CompositePatch12(new CastleLow(), new CastleMiddle1(), new CastleMiddle2(), new CastleUp())),
	PalaisHaut(new Point(7, 7), new CompositePatch12(new CastleLow2(), new CastleUp())),
	Souterrain(new Point(5, 5), new CompositePatch12(new CaveMiddle(), new CaveTop())),
	SouterrainBas(new Point(9, 9), new CompositePatch12(new CaveFloor(), new CaveLow(), new CaveMiddle2(), new CaveTop()));
	
	Point size;
	TraceDelegateDraw method;
	
	private PrefTraceDrop(Point p_size, TraceDelegateDraw p_method) {
		size=p_size;
		method=p_method;
	}
	
	public static PrefTraceDrop fromPrefetch(Prefetch p_pref) {
		for (PrefTraceDrop p : values()) {
			if (p.name().equals(p_pref.name())) {
				return p;
			}
		}
		throw new RuntimeException("Impossible de trouver le PrefDrop associé à "+p_pref.name());
	}
	
}