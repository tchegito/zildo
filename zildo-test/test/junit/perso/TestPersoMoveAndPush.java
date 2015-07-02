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

package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class TestPersoMoveAndPush extends EngineUT {

	/** Proves that turtle can push any blocking character when he's on its way **/
	@Test
	public void turtlePushPerso() {
		// Spawn a character A
		Point targetA = new Point(100, 190);
		Perso persoA = spawnPerso(PersoDescription.TURTLE, "Turtle", 100, 80);
		persoA.setTarget(targetA);
		
		// Spawn character (non-hero)
		Perso persoB = spawnTypicalPerso("non-hero", 100, 150);
		
		// Let's rock !
		renderFrames(500);
		
		Assert.assertNull("Character A shouldn't have a target !", persoA.getTarget());
		assertLocation(persoA, targetA, true);
	}
	
	/** Proves that turtle can't push hero. Instead, it has to wait him to move by himself. **/
	@Test
	public void turtlePushHero() {
		// Spawn a character A
		Point targetA = new Point(100, 190);
		Perso persoA = spawnPerso(PersoDescription.TURTLE, "Turtle", 100, 80);
		persoA.setTarget(targetA);
		
		// Spawn hero
		Perso persoB = spawnZildo(100, 150);

		
		// Let's rock !
		renderFrames(500);
		
		Assert.assertNull("Character A shouldn't have a target !", persoA.getTarget());
		assertLocation(persoA, targetA, true);
	}
}
