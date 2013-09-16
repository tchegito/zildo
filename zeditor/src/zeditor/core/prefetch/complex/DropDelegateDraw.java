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

package zeditor.core.prefetch.complex;

import zeditor.core.tiles.TileSelection;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;

/**
 * @author Tchegito
 *
 */
public class DropDelegateDraw {

	/**
	 * Delegate method to draw a {@link Case} object from a {@link TileSelection} on the map.
	 * @param p_mapCase
	 * @param p_toDraw
	 * @param p_mask 0/1/2
	 */
	public void draw(Case p_mapCase, Case p_toDraw, int p_mask) {
		// Apply modifications
		Tile tile = p_toDraw.getBackTile();
		if (tile.index != -1 && p_mask == 0) {	// Smash the previous tile
			p_mapCase.setBackTile(p_toDraw.getBackTile().clone());
		} else {
			//TODO : Check this
			/*
			if (tile.index == 54) {
				c.setMasked(false);
			} else {
				c.setMasked(true);
			}
			*/
		}
		if (p_mask == 2) {
			p_mapCase.setForeTile(p_toDraw.getBackTile().clone());
		} else if (p_mask == 1) {
			p_mapCase.setBackTile2(p_toDraw.getBackTile().clone());
		} else {
			Tile clonedForeTile = null;
			if (p_toDraw.getForeTile() != null) {
				clonedForeTile = p_toDraw.getForeTile().clone();
			}
			p_mapCase.setForeTile(clonedForeTile);
			Tile cloneBackTile2 = null;
			if (p_toDraw.getBackTile2() != null) {
				cloneBackTile2 = p_toDraw.getBackTile2().clone();
			}
			p_mapCase.setBackTile2(cloneBackTile2);
		}
	}
}
