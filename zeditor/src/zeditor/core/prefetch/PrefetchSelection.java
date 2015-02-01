/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

import zeditor.core.prefetch.complex.DropDelegateDraw;
import zeditor.core.tiles.TileSelection;
import zeditor.tools.AreaWrapper;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.sprites.Reverse;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class PrefetchSelection extends TileSelection {

	PrefKind kind;
	PrefTraceDrop traceDrop;
	PrefDrop drop;
	
	final static private DropDelegateDraw defaultDrawer = new DropDelegateDraw();
	
	public PrefetchSelection(Prefetch p_pref) {
		super();
		kind=p_pref.kind;
		switch (p_pref.kind) {
		case Drop:
			// Get the associated PrefDrop object
			drop=PrefDrop.fromPrefetch(p_pref);
			width=drop.size.x;
			height=drop.size.y;
			for (int j=0;j<height;j++) {
				for (int i=0;i<width;i++) {
					int d=drop.data[j*width + i];
					Case aCase=new Case();
					Tile back = aCase.getBackTile();
					int val = Math.abs(d) & 0xfff;	// Remove optional flags
					if (d<0) {	// Motif en foreground
						back.index = -1;
						aCase.setForeTile(new Tile(val / 256, val % 256, aCase));
					} else {
						back.index = val % 256;
						back.bank = (byte) ((val >> 8) & 255);
					}
					if ((Math.abs(d) & 0x1000) != 0) {
						back.reverse = Reverse.HORIZONTAL;
					}
					items.add(aCase);
				}
			}
			if (drop.drawer == null) {
				drawer = defaultDrawer;
			} else {
				drawer = drop.drawer;
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
	public void draw(AreaWrapper p_map, Point p_start, int p_mask) {
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
