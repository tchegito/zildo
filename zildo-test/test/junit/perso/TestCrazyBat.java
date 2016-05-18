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

package junit.perso;

import org.junit.Assert;

import org.junit.Test;

import tools.EngineUT;
import zildo.monde.sprites.persos.PersoBat;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Anticiper;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class TestCrazyBat extends EngineUT {

	@Test
	public void batGoingOnScreenCorner() {
		Anticiper anticiper = new Anticiper(1.5f);
		PersoBat bat = new PersoBat();
		bat.setX(671f);
		bat.setY(385f);
		PersoPlayer zildo = new PersoPlayer(0);
		zildo.setX(660.0062f);
		zildo.setY(341.06696f);
		zildo.deltaMoveX = -2.121338f;
		zildo.deltaMoveY = 2.1213074f;
		
		float delta = Point.distance(bat.x, bat.y, zildo.x, zildo.y);
		System.out.println("Distance entre les 2: " + delta);
		Point t = anticiper.anticipeTarget(bat, zildo);
		System.out.println(t);
		Assert.assertTrue(t.x == 0 && t.y == 0);
	}
}
