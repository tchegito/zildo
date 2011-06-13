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

import zeditor.core.tiles.TileSelection;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Point;
import zildo.monde.map.Tile;

/**
 * @author Tchegito
 *
 */
public class PrefetchSelection extends TileSelection {

	PrefKind kind;
	PrefTraceDrop traceDrop;
	
	public PrefetchSelection(Prefetch p_pref) {
		super();
		kind=p_pref.kind;
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
					Tile back = aCase.getBackTile();
					if (d<0) {	// Motif en foreground
						back.index = -1;
						aCase.setForeTile(new Tile(-d / 256, -d % 256));
					} else {
						back.index = d % 256;
						back.bank = d / 256;
					}
					items.add(aCase);
				}
			}
			break;
		case TraceDrop:
			traceDrop=PrefTraceDrop.fromPrefetch(p_pref);
			width=traceDrop.size.x;
			height=traceDrop.size.y;
			break;
		default:
			// Not implemented yet
		}
	}
	
	@Override
	public void draw(Area p_map, Point p_start, boolean p_mask) {
		switch (kind) {
		case TraceDrop:
			traceDrop.method.draw(p_map, p_start);
			break;
		default:
			super.draw(p_map, p_start, p_mask);
		}
	}

	@Override
	public void finalizeDraw() {
		switch (kind) {
		case TraceDrop:
			traceDrop.method.finalizeDraw();
			break;
		}
	}
}
