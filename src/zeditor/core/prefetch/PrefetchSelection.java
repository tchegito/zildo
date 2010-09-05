/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

import zeditor.core.tiles.TileSelection;
import zildo.monde.map.Case;

/**
 * @author Tchegito
 *
 */
public class PrefetchSelection extends TileSelection {

	public PrefetchSelection(Prefetch p_pref) {
		super();
		switch (p_pref.kind) {
		case Drop:
			// Get the associated PrefDrop object
			PrefDrop drop=PrefDrop.fromPrefetch(p_pref);
			width=drop.size.x;
			height=drop.size.y;
			for (int j=0;j<height;j++) {
				for (int i=0;i<width;i++) {
					int d=drop.data[j*width + i];
					Case aCase=new Case();
					if (d<0) {	// Motif en foreground
						aCase.setN_banque(128);
						aCase.setN_motif(-1);
						aCase.setN_motif_masque(-d % 256);
						aCase.setN_banque_masque(-d / 256);
					} else {
						aCase.setN_motif(d % 256);
						aCase.setN_banque(d / 256);
					}
					items.add(aCase);
				}
			}
			break;
		default:
			// Not implemented yet
		}
	}
}
