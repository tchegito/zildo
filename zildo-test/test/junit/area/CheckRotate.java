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

import junit.perso.EngineUT;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.map.TileCollision;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class CheckRotate extends EngineUT {

	@Test
	public void simple() {
		Point start = new Point(7, 9);
		Point dest = Rotation.CLOCKWISE.rotate(start, 16, 16);
		
		Assert.assertEquals(new Point(6,7), dest);
	}
	
	@Test
	public void collisionRotate() {
		int nTile = 256*3 + 252;
		Point start = new Point(7, 9);
		Point dest = Rotation.CLOCKWISE.rotate(start, 16, 16);
		boolean a = TileCollision.getInstance().collide(start.x, start.y, nTile, Reverse.NOTHING, Rotation.NOTHING, 0);
		boolean b = TileCollision.getInstance().collide(dest.x,  dest.y,  nTile, Reverse.NOTHING, Rotation.CLOCKWISE, 0);
		Assert.assertEquals(a, b);
		Assert.assertEquals(false, a);
	}
}