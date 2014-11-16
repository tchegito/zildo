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

package junit.fwk;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.gfx.Occluder;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;

/**
 * @author Tchegito
 *
 */
public class OccluderTest {

	@Test
	public void basic() {
		// 1) Create occluder and remove upper-left corner
		Occluder occ = new Occluder(256, 256);
		Assert.assertEquals(256*256, occ.calculateAire());
		occ.remove(new Zone(0, 0, 64, 80));
		Assert.assertEquals(256*256 - 64*80, occ.calculateAire());
		Point free = occ.allocate(256, 100);
		Assert.assertEquals(256*256 - 64*80 - 256*100, occ.calculateAire());
		Assert.assertEquals(new Point(0, 80), free);

		//65536 - 5120 = 60416
		// 2) Create occluder and remove upper-middle corner
		occ = new Occluder(256, 256);
		occ.remove(new Zone(64, 0, 64, 80));

		free = occ.allocate(256, 100);
		Assert.assertEquals(new Point(0, 80), free);

		// 2) Create occluder and remove bottom-right corner
		occ = new Occluder(256, 256);
		occ.remove(new Zone(128, 64, 128, 192));

		free = occ.allocate(256, 64);
		Assert.assertEquals(new Point(0, 0), free);

	}
	
	@Test
	public void cut2() {
		Occluder occ = new Occluder(256, 256);
		occ.remove(new Zone(80, 80, 64, 64));
	}
}
