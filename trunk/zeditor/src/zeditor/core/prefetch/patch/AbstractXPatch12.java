/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zeditor.core.prefetch.patch;

import java.util.List;

import zeditor.core.prefetch.complex.CompositePatch12;
import zeditor.tools.AreaWrapper;
import zildo.fwk.collection.MultiMap;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.sprites.Rotation;
import zildo.monde.util.Point;

/**
 * Same behavior than AbstractPatch12, plus Rotation management.
 * 
 * Allows to have less tiles to define, and use rotations.
 * 
 * @author Tchegito
 *
 */
public abstract class AbstractXPatch12 extends AbstractPatch12 {

	/**
	 * @param p_big
	 */
	public AbstractXPatch12(boolean p_big) {
		super(p_big);
	}

	@Override
	public int toBinaryValue(int p_val) {
		return 0;
	}

	@Override
	public int toGraphicalValue(int p_val) {
		return 0;
	}
	
	final protected int[] getReverseTab(XTile[] p_tab, int p_startTile) {
		MultiMap<Integer, Integer> temp = new MultiMap<Integer, Integer>();
		int maxTile = 0;
		for (int i = 0; i <= 15; i++) {
			int val = p_tab[i].value();
			temp.put(val, i);
			maxTile = Math.max(val, maxTile);
		}
		int[] result = new int[maxTile - p_startTile + 1];
		for (int i = p_startTile; i <= maxTile; i++) {
			List<Integer> vals = temp.get(i);
			if (vals != null) {
				for (Integer v : vals) {
					result[i - p_startTile] = v.intValue();
				}
			} else {
				result[i - p_startTile] = 0;
			}
		}

		return result;
	}
	
	public abstract int toBinaryValue(int p_val, Rotation rot);

	public abstract XTile toGraphicalValueXtile(int p_val);
	
	
	@Override
	protected void arrangeOneTile(AreaWrapper p_map, int p_patchValue, int p_x, int p_y, CompositePatch12 p_composite) {
		// Get map value
		Case c = p_map.get_mapcase(p_x, p_y);
		Tile t = c.getBackTile();
		int val = t.getValue();
		if (p_composite != null && !p_composite.canDraw(val)) {
			return;
		}
		val=toBinaryValue(val, t.rotation);
		// Add patch value
		val=val | p_patchValue;
		// And render the arranged one
		XTile xtile = toGraphicalValueXtile(val);
		
		// Be careful to 256*9 offset for nature palace bank
		t.set(xtile.tile + 256*9, xtile.rot);
	}
	
	@Override
	public void doTheJob(AreaWrapper p_map, Point p, int tile) {
		/*
		XTile binaryValue=toBinaryValue(tile);
		if (binaryValue == 0) {
			p_map.writemap(p.x, p.y, tile);
		} else {
			arrangeOneTile(p_map, binaryValue, p.x, p.y, null);
		}*/
	}
	
	@Override
	public boolean isFromThis(int p_val) {
		int a = toBinaryValue(p_val, Rotation.NOTHING);
		return a != 0;
	}
}
