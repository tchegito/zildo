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

import zildo.monde.sprites.Rotation;

/**
 * @author Tchegito
 *
 */
public class PalaceNatureWall extends AbstractXPatch12 {

	/**
	 * @param p_big
	 */
	public PalaceNatureWall() {
		super(true);
	}

	XTile[] conv_value = new XTile[]
   	{ new XTile(96), new XTile(0, Rotation.UPSIDEDOWN), new XTile(0, Rotation.COUNTERCLOCKWISE), new XTile(1, Rotation.UPSIDEDOWN),
   	  new XTile(0, Rotation.CLOCKWISE), new XTile(2, Rotation.UPSIDEDOWN), new XTile(-1), new XTile(44, Rotation.UPSIDEDOWN),
   	  new XTile(0), new XTile(-1), new XTile(2), new XTile(44, Rotation.COUNTERCLOCKWISE),
   	  new XTile(1), new XTile(44, Rotation.CLOCKWISE), new XTile(44), new XTile(-1)
   	};

	int[] value = getReverseTab(conv_value, 0);
	
	@Override
	public int toBinaryValue(int p_val, Rotation rot) {
		int a = p_val - 256 * 9;
		a += rot.value << 8;
		if (a < 0 || a >= value.length) {
			return 0;
		}
		return value[a];
	}

	@Override
	public XTile toGraphicalValueXtile(int p_val) {
		return conv_value[p_val];
	}

}
