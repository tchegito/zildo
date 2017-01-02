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
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;

/**
 * @author Tchegito
 *
 */
public class TestBasicPerso extends EngineUT {

	@Test
	public void facing() {
		Perso a = spawnTypicalPerso("a", 160, 100);
		a.setAngle(Angle.EST);
		Perso b = spawnTypicalPerso("b", 250, 130);
		b.setAngle(Angle.OUEST);
		Assert.assertTrue(a.isFacing(b));
		Assert.assertTrue(b.isFacing(a));
		
		a.setAngle(Angle.NORD);
		Assert.assertTrue(!a.isFacing(b));
		Assert.assertTrue(b.isFacing(a));
	}
	
	@Test
	public void facingMarginal() {
		Perso hero = spawnTypicalPerso("hero", 835, 187);
		hero.setAngle(Angle.OUEST);
		Perso willOWist = spawnTypicalPerso("wow", 834, 235);
		
		Assert.assertFalse(hero.isFacing(willOWist));
		hero.setAngle(Angle.SUD);
		Assert.assertTrue(hero.isFacing(willOWist));
		
	}
}
