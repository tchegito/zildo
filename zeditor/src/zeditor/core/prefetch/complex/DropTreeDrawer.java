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

import zeditor.core.prefetch.PrefDrop;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;

/**
 * @author Tchegito
 * 
 */
public class DropTreeDrawer extends DropDelegateDraw {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * zeditor.core.prefetch.complex.DropDelegateDraw#draw(zildo.monde.map.Case,
	 * zildo.monde.map.Case, boolean)
	 */
	@Override
	public void draw(Case p_mapCase, Case p_toDraw, int p_mask) {
		// Between 2 trees
		// 1) fore tile
		Tile t1 = p_mapCase.getForeTile();
		Tile t2 = p_toDraw.getForeTile();
		if (linkTwoTree(t1, t2)) {
			return;
		}

		// 3) back tile
		t1 = p_mapCase.getBackTile();
		t2 = p_toDraw.getBackTile();
		if (linkTwoTree(t1, t2)) {
			return;
		}

		// 3) mask on a masked tile

		// The 'mask' parameter has no sense here.
		super.draw(p_mapCase, p_toDraw, p_mask);
	}

	/**
	 * Adjust the link between two trees.
	 * 
	 * @param t1
	 * @param t2
	 * @return TRUE if the adjustment has been done.
	 */
	private boolean linkTwoTree(Tile t1, Tile t2) {
		final int[] treeModel = PrefDrop.Arbre.data;
		if (t1 != null && t2 != null && t1.bank == 0) {
			int a = t1.index;
			int b = t2.index;
			for (int i = 0; i < 5; i++) {
				int c = Math.abs(treeModel[0 + 4 * i]);
				int d = Math.abs(treeModel[3 + 4 * i]);
				if ((a == c && b == d) || (a == d && b == c)) {
					t1.index = 191 + i;
					t1.bank = 6;
					return true;
				}
			}
		}
		return false;
	}
}
