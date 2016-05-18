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
import zildo.monde.collision.Rectangle;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

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
		persoA.setQuel_deplacement(MouvementPerso.MOBILE_WAIT, true);
		persoA.setTarget(targetA);
		
		// Spawn character (non-hero)
		Perso persoB = spawnTypicalPerso("non-hero", 100, 150);
		
		// Let's rock !
		renderFrames(500);
		
		Assert.assertNull("Character A shouldn't have a target !", persoA.getTarget());
		// Check that persoB has moved, to let turtle pass
		assertLocation(persoB, new Point(100, 150), false);
		// Check that turtle is arrived
		assertLocation(persoA, targetA, true);
	}
	
	/** Proves that turtle can't push hero. Instead, it has to wait him to move by himself. **/
	@Test
	public void turtleCantPushHero() {
		// Spawn a character A
		Point targetA = new Point(200, 80);
		Perso persoA = spawnPerso(PersoDescription.TURTLE, "Turtle", 100, 80);
		persoA.setQuel_deplacement(MouvementPerso.MOBILE_WAIT, true);
		persoA.setTarget(targetA);
		
		// Spawn hero
		Perso hero = spawnZildo(150, 90);

		
		// Let's rock !
		renderFrames(400);

		// Check that turtle hasn't loosed its target
		Assert.assertTrue(persoA.getTarget() != null);
		assertLocation((Element) persoA, targetA, false);

		Assert.assertTrue("Character A should still have a target !", persoA.getTarget() != null);
		
		// Now we move hero
		hero.setTarget(new Point(150,120));
		hero.setGhost(true);
		
		renderFrames(400);
		
		// And now, check that turtle is arrived
		Assert.assertTrue(persoA.getTarget() == null);
		assertLocation(persoA, targetA, true);
	}
	
	@Test
	public void testTurtleZone() {
		mapUtils.loadMap("sousbois3");
		Perso turtle = EngineZildo.persoManagement.getNamedPerso("sacher");
		turtle.setPos(new Vector2f(470, 584));
		turtle.setAngle(Angle.EST);
		Perso hero = spawnZildo(470,  595);
		
		renderFrames(1);
		
		// Check with circles (both radius = 7)
		Assert.assertTrue( EngineZildo.collideManagement.checkCollisionCircles((int) turtle.x, (int) turtle.y, (int) hero.x, (int) hero.y, 7, 7));
		// Check with circle and zone
		Assert.assertFalse( new Rectangle(turtle.getMover().getZone()).isCrossingCircle(new Point(hero.x, hero.y), 7) );
		
		Assert.assertFalse(EngineZildo.mapManagement.collide(hero.x, hero.y, hero));
	}
}
