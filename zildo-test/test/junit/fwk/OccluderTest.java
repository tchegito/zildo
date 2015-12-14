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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.gfx.Occluder;
import zildo.fwk.gfx.OccluderArranger;
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

	class OccluderCustom extends Occluder {
		public OccluderCustom(List<Zone> available) {
			super(256, 256);
			this.available = available;
		}
	};

	
	// Stress test to force Occluder to re-arrange its areas, when we request a zone.
	@Test
	public void extraSize() { 
		// Build a test case from a real one
		List<Zone> availableZones = new ArrayList<>();
		int[][] toStringAvailable = {{50, 95, 206, 1}, {113, 93, 143, 2}, {112, 140, 144, 6}, {157, 129, 99, 11}, {253, 96, 3, 32}, {202, 128, 54, 1}, {11, 212, 245, 2}, {37, 211, 219, 1}, 
				{0, 241, 256, 15}, {21, 240, 235, 1}, {68, 239, 188, 1}, {171, 238, 85, 1}, {50, 170, 206, 14}, {255, 184, 1, 24}, {239, 208, 17, 3}, {64, 169, 192, 1},
				{82, 168, 174, 1}, {226, 90, 30, 3}, {244, 233, 12, 5}, {241, 66, 15, 3}, {242, 42, 14, 5}, {98, 164, 158, 4}, {254, 69, 2, 18}, {242, 87, 14, 3}, {140, 163, 116, 1},
				{212, 162, 44, 1}, {253, 146, 3, 16}};
		
		availableZones = deserializeZones(toStringAvailable);
		// Create an occluder in this use case, and try to allocate a zone
		Occluder occ = new OccluderCustom(availableZones);
		
		// Try to recut
		List<Zone> newZones = new OccluderArranger(occ).recut();
		Occluder occ2 = new OccluderCustom(newZones);
		// Check same number of pixels in both areas
		Assert.assertEquals(occ.calculateAire(), occ2.calculateAire());
		// This should be successfull
		Assert.assertNotNull(occ.allocate(26, 16));
	}
	
	@Test
	public void extraSize2() {
		// Build a test case from a real one
		List<Zone> availableZones = new ArrayList<>();
		int[][] toStringAvailable = {{242, 18, 14, 6}, {244, 42, 12, 6},{242, 66, 14, 5}, {241, 90, 15, 6}, {113, 93, 128, 3}, {50, 95, 63, 1},{255, 96, 1, 142}, {254, 115, 1, 123},
				{232, 126, 11, 20}, {243, 127, 11, 19}, {222, 128, 10, 18}, {174, 129, 7, 17}, {91, 140, 45, 6}, {208, 140, 14, 6},  {195, 141, 13, 5}, {161, 142, 13, 4}, {181, 143, 14, 3},
				{136, 144, 25, 2}, {235, 166, 5, 18}, {210, 167, 12, 17}, {168, 168, 2, 16}, {133, 169, 3, 15}, {75, 170, 7, 14},{240, 178, 14, 6}, {222, 179, 13, 5}, {197, 180, 13, 4},
				{120, 181, 13, 3}, {155, 181, 13, 3}, {63, 182, 12, 2}, {101, 182, 19, 2}, {170, 182, 27, 2}, {50, 183, 13, 1},{82, 183, 19, 1}, {136, 183, 19, 1}, {249, 184, 5, 28},
				{26, 211, 223, 1}, {253, 212, 1, 26}, {150, 236, 103, 2}, {47, 237, 103, 1}, {228, 253, 28, 3}, {86, 254, 142, 2}, {14, 255, 72, 1}};
		
		availableZones = deserializeZones(toStringAvailable);
		// Create an occluder in this use case, and try to allocate a zone
		Occluder occ = new OccluderCustom(availableZones);
		
		Assert.assertNotNull(occ.allocate(13, 12));
		// Try to recut
		List<Zone> newZones = new OccluderArranger(occ).recut();
		// Check same number of pixels in both areas
		Occluder occ2 = new OccluderCustom(newZones);
		Assert.assertEquals(occ.calculateAire(), occ2.calculateAire());

		
	}
	
	private List<Zone> deserializeZones(int[][] intZones) {
		List<Zone> zones = new ArrayList<>();
		for (int[] z : intZones) {
			zones.add(new Zone(z[0], z[1], z[2], z[3]));
		}
		return zones;
	}

}
