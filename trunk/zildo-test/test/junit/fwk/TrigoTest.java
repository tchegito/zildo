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

import static java.lang.Math.abs;
import static zildo.monde.Trigo.getAngleRadian;
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Tchegito
 *
 */
public class TrigoTest {

	@Test
	public void angleRadian() {
		// No dependent of size
		Assert.assertEquals(getAngleRadian(0, -1), getAngleRadian(0, -8));
		// PI / 2
		Assert.assertEquals(abs(getAngleRadian(0f, -1.5f)), Math.PI / 2d, 0.0001d);
		// 0
		Assert.assertEquals(abs(getAngleRadian(3, 0)), 0, 0.0001d);
		// PI / 4
		Assert.assertEquals(abs(getAngleRadian(1, 1)), abs(getAngleRadian(5, 5)), 0.0001d);
		Assert.assertEquals(abs(getAngleRadian(1, 1)), Math.PI / 4d, 0.0001d);
	}
}
