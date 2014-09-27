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

import java.util.Arrays;

import zeditor.core.prefetch.complex.Adjustment;
import zildo.monde.sprites.Rotation;
import zildo.monde.util.Angle;

/**
 * @author Tchegito
 *
 */
public class PalaceNatureWall extends AbstractXPatch12 {

	Adjustment[] adjustments = new Adjustment[] {
			// Upper-left corner
			new Adjustment(256*9 , Angle.EST, 256*9 + 1),
			new Adjustment(256*9, Angle.SUD, 256*9 + 2),
			// Upper-right corner
			new Adjustment(256*9, Rotation.CLOCKWISE, Angle.SUD, 256*9 + 1),
			new Adjustment(256*9, Rotation.CLOCKWISE, Angle.OUEST, 256*9 + 2),
			// Lower-right corner
			new Adjustment(256*9, Rotation.UPSIDEDOWN, Angle.OUEST, 256*9 + 1),
			new Adjustment(256*9, Rotation.UPSIDEDOWN, Angle.NORD, 256*9 + 2),
			// Lower-left corner
			new Adjustment(256*9, Rotation.COUNTERCLOCKWISE, Angle.NORD, 256*9 + 1),
			new Adjustment(256*9, Rotation.COUNTERCLOCKWISE, Angle.EST, 256*9 + 2)
	};
	
	/**
	 * @param p_big
	 */
	public PalaceNatureWall() {
		super(true);
		tolerateValue(1, Rotation.NOTHING, 12);
		tolerateValue(2, Rotation.NOTHING, 10);
		tolerateValue(1, Rotation.CLOCKWISE, 5);
		tolerateValue(2, Rotation.CLOCKWISE, 12);
		tolerateValue(1, Rotation.UPSIDEDOWN, 3);
		tolerateValue(2, Rotation.UPSIDEDOWN, 5);
		tolerateValue(1, Rotation.COUNTERCLOCKWISE, 10);
		tolerateValue(2, Rotation.COUNTERCLOCKWISE, 3);
	}
	 
	XTile[] conv_value = new XTile[]
   	{ new XTile(96), new XTile(0, Rotation.UPSIDEDOWN), new XTile(0, Rotation.COUNTERCLOCKWISE), new XTile(4, Rotation.UPSIDEDOWN),
   	  new XTile(0, Rotation.CLOCKWISE), new XTile(6, Rotation.UPSIDEDOWN), new XTile(-1), new XTile(44, Rotation.UPSIDEDOWN),
   	  new XTile(0), new XTile(-1), new XTile(6), new XTile(44, Rotation.COUNTERCLOCKWISE),
   	  new XTile(4), new XTile(44, Rotation.CLOCKWISE), new XTile(44), new XTile(-1)
   	};

	int[] value = getReverseTab(conv_value, 0);

	private void tolerateValue(int val, Rotation rot, int corresponding) {
		int v = new XTile(val, rot).value();
		if (v > value.length) {
			value = Arrays.copyOf(value, v+1);
		}
		value[v] = corresponding;
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

	@Override
	public AbstractPatch12 getAdjustmentClass() {
		return this;
	}
	
	@Override
	public Adjustment[] getAdjustments() {
		return adjustments;
	}
}
