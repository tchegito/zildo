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

import org.junit.Test;

import tools.EngineUT;
import zildo.monde.map.Area;
import zildo.monde.map.TilePattern;
import zildo.monde.sprites.Rotation;

import org.junit.Assert;

/**
 * @author Tchegito
 *
 */
public class CheckTilePattern extends EngineUT {

	@Test
	public void checkSimple() {
		Area area = mapUtils.area;
		TilePattern.explodedHill.apply(4, 4, area);
		
		Assert.assertEquals(222 + 256*5, area.readmap(4, 5));
		Assert.assertEquals(223 + 256*5, area.readmap(5, 5));
	}
	
	@Test
	public void checkRotate() {
		Area area = mapUtils.area;
		TilePattern.explodedHill.apply(4, 4, area, Rotation.CLOCKWISE);
		
		Assert.assertEquals(256*2, area.readmap(5, 4));
		Assert.assertEquals(222 + 256*5, area.readmap(4, 4));
		
		TilePattern.explodedHill.apply(12, 12, area, Rotation.UPSIDEDOWN);
		Assert.assertEquals(222 + 256*5, area.readmap(13, 12));
		Assert.assertEquals(223 + 256*5, area.readmap(12, 12));
		
		TilePattern.explodedHill.apply(10, 20, area, Rotation.COUNTERCLOCKWISE);
		Assert.assertEquals(222 + 256*5, area.readmap(11, 21));
		Assert.assertEquals(223 + 256*5, area.readmap(11, 20));
	}
}
