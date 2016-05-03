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

package zeditor.core.prefetch.complex;

import zeditor.core.prefetch.patch.AbstractPatch12;
import zeditor.tools.AreaWrapper;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.util.Point;

/**
 * Define an interface for drawing specific render.
 * @author Tchegito
 *
 */
public abstract class TraceDelegateDraw {
	
	public abstract void draw(AreaWrapper p_map, Point p_start);

	public Adjustment[] getAdjustments() {
		return new Adjustment[] {};
	}
	
	public AbstractPatch12 getAdjustmentClass() {
		return null;
	}
	
	public void finalizeDraw() {}

	// Drawback: only on BACK tile
	public void drawAdjustments(AreaWrapper p_map, Point p_start, int... p_size) {
		AbstractPatch12 adjDraw = getAdjustmentClass();
		
		int size = 3;
		if (p_size != null) {
			size = p_size[0];
		}
		for (int i=-1;i<size+1;i++) {
			for (int j=-1;j<size+1;j++) {
				Case c = p_map.get_mapcase(p_start.x+j, p_start.y+i);
				if (c != null) {
					Tile t = c.getBackTile();
					for (Adjustment adj : getAdjustments()) {
						// Check for same tile and same rotation
						if (adj.matchTile == t.getValue() && adj.rot == t.rotation ) {
							Point p=new Point(p_start).translate(j, i);
							for (int tile : adj.addedTiles) {
								p = p.translate(adj.a.coords);
								if (!p_map.isOutside(p.x, p.y)) {
									if (adjDraw != null) {
										adjDraw.doTheJob(p_map, p, tile, adj.rot);
									} else {
										p_map.writemap(p.x, p.y, tile);
									}
								}
							}
						}
					}
				}
			}
		}		
	}
}
