/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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
public class PalaceNatureLow1 extends AbstractXPatch12 {


	XTile[] conv_value = new XTile[]
	{ new XTile(9), new XTile(3, Rotation.UPSIDEDOWN), new XTile(3, Rotation.COUNTERCLOCKWISE), new XTile(5, Rotation.UPSIDEDOWN),
	  new XTile(3, Rotation.CLOCKWISE), new XTile(7, Rotation.UPSIDEDOWN), new XTile(-1), new XTile(47, Rotation.UPSIDEDOWN),
	  new XTile(3), new XTile(-1), new XTile(7), new XTile(47, Rotation.COUNTERCLOCKWISE),
	  new XTile(5), new XTile(47, Rotation.CLOCKWISE), new XTile(47), new XTile(9)
	};

	int[] value = getReverseTab(conv_value, 0);

	public PalaceNatureLow1() {
		super(true);
	}

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
