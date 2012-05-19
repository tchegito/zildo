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

package zeditor.core.prefetch.complex;

import zeditor.core.prefetch.patch.AbstractPatch12;
import zildo.monde.map.Area;
import zildo.monde.util.Point;

/**
 * Define an interface for drawing specific render.
 * @author Tchegito
 *
 */
public abstract class TraceDelegateDraw {
	
	public abstract void draw(Area p_map, Point p_start);

	public Adjustment[] getAdjustments() {
		return new Adjustment[] {};
	}
	
	public AbstractPatch12 getAdjustmentClass() {
		return null;
	}
	
	public void finalizeDraw() {}

	public void drawAdjustments(Area p_map, Point p_start) {
		AbstractPatch12 adjDraw = getAdjustmentClass();
		
		for (int i=0;i<3;i++) {
			for (int j=0;j<3;j++) {
				int val=p_map.readmap(p_start.x+j, p_start.y+i);
				for (Adjustment adj : getAdjustments()) {
					if (adj.matchTile == val) {
						Point p=new Point(p_start).translate(j, i);
						for (int tile : adj.addedTiles) {
							p = p.translate(adj.a.coords);
							if (!p_map.isOutside(p.x, p.y)) {
								if (adjDraw != null) {
									int binaryValue=adjDraw.toBinaryValue(tile);
									if (binaryValue == 0) {
										p_map.writemap(p.x, p.y, tile);
									} else {
										adjDraw.arrangeOneTile(p_map, binaryValue, p.x, p.y, null);
									}
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
