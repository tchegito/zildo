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

package junit.fwk;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.ZMaths;
import zildo.monde.collision.Line;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class MathTest {

	@Test
	public void decompose10() {
		// Nominal cases
		Assert.assertArrayEquals(new byte[] {1, 3, 6}, ZMaths.decomposeBase10(136));
		Assert.assertArrayEquals(new byte[] {7}, ZMaths.decomposeBase10(7));
		Assert.assertArrayEquals(new byte[] {2, 9, 0, 2, 4, 0}, ZMaths.decomposeBase10(290240));
		
		// Boundaries cases
		Assert.assertArrayEquals(new byte[] {0}, ZMaths.decomposeBase10(0));
		Assert.assertArrayEquals(new byte[] {2,1,4,7,4,8,3,6,4,7}, ZMaths.decomposeBase10(0x7fffffff)); //1234567890));
		Assert.assertArrayEquals(new byte[] {-1}, ZMaths.decomposeBase10(-1)); //1234567890));
	}
	
	@Test
	public void intersection() {
		Line a=new Line(new Point(0,4), new Point(12,1));
		Line b=new Line(new Point(5,1), new Point(5,4));
		
		Point inter=a.intersect(b);
		Assert.assertEquals(new Point(5, 2), inter);
	}
	
	@Test
	public void integer() {
		Assert.assertEquals(1, (int) 1.1f);
		Assert.assertEquals(1, (int) 1.9f);
	}
}
