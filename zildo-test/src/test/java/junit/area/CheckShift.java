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

package junit.area;


import org.junit.Assert;
import org.junit.Test;

import zildo.client.sound.Ambient.Atmosphere;
import zildo.monde.map.Area;
import zildo.server.EngineZildo;
import zildo.server.SpriteManagement;

/**
 * Test the 'shift' functionality. It consists of moving the current map origin, shifting everything: tiles, sprites
 * and chaining points.
 * 
 * @author Tchegito
 *
 */
public class CheckShift {

	private Area createBasicArea() {
		Area area = new Area(10, 10);
		area.setAtmosphere(Atmosphere.OUTSIDE);

		// Fill 2 cases
		area.writemap(0, 0, 16);
		area.writemap(8, 3, 256*3 + 15);
		
		EngineZildo.spriteManagement = new SpriteManagement();
		return area;
	}
	
	@Test
	public void basic() {
		Area area = createBasicArea();
		
		area.shift(2, 3);
		// 1st: dimension
		Assert.assertEquals(12, area.getDim_x());
		Assert.assertEquals(13, area.getDim_y());
		// 2nd: data
		Assert.assertEquals(16, area.readmap(2, 3));
		Assert.assertEquals(256*3 + 15, area.readmap(8 + 2, 3 + 3));
		// 3nd: removed cases
		Assert.assertEquals(Atmosphere.OUTSIDE.getEmptyTile(), area.readmap(0, 0));
	}
	
	@Test
	public void negative() {
		Area area = createBasicArea();
		
		area.shift(-2, -1);
		
		// 1st: dimension
		Assert.assertEquals(8, area.getDim_x());
		Assert.assertEquals(9, area.getDim_y());
		// 2nd: data
		Assert.assertEquals(256*3 + 15, area.readmap(8 - 2, 3 - 1));
		// 3nd: removed cases
		Assert.assertEquals(Atmosphere.OUTSIDE.getEmptyTile(), area.readmap(0, 0));
		
	}
}
