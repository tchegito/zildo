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
import org.junit.Ignore;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.InfoPersos;
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
	@Ignore // This usecase has been canceled: turtle doesn't push anyone
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
	
	/** Proves that a character can push any blocking character when he's on its way **/
	@Test @InfoPersos
	public void persoPushPerso() {
		// Spawn a character A
		mapUtils.loadMap("eleom1");
		EngineZildo.persoManagement.clearPersos(true);
		waitEndOfScripting();
		Point targetA = new Point(78, 80);
		Perso eleo = spawnPerso(PersoDescription.ELEORIC, "eleoric", 78, 115);
		eleo.setTarget(targetA);
		eleo.setGhost(true);
		
		// Spawn character (non-hero)
		Perso blocker = spawnTypicalPerso("non-hero", 78, 96);
		
		// Let's rock !
		renderFrames(500);
		
		Assert.assertNull("Eleoric shouldn't have a target !", eleo.getTarget());
		// Check that persoB has moved, to let turtle pass
		assertLocation(blocker, new Point(100, 150), false);
		// Check that character is arrived
		assertLocation(eleo, targetA, true);
		// Check that ScriptExecutor has set ghost at false, because 'blocker' has been declared as 'involved'
		Assert.assertFalse(blocker.isGhost());
	}
	
	/** Proves that turtle can't push hero. Instead, it has to wait for him to move by himself. **/
	@Test @InfoPersos
	public void turtleCantPushHero() {
		// Spawn a character A
		Point targetTurtle = new Point(200, 80);
		Perso turtle = spawnPerso(PersoDescription.TURTLE, "Turtle", 100, 80);
		turtle.setQuel_deplacement(MouvementPerso.MOBILE_WAIT, true);
		turtle.setTarget(targetTurtle);

		// Spawn hero
		Perso hero = spawnZildo(150, 80);
		
		// Let's rock !
		renderFrames(100);

		// Check that turtle hasn't lost its target
		Assert.assertTrue("Turtle should still have a target !", turtle.getTarget() != null);
		assertLocation((Element) turtle, targetTurtle, false);

		
		// Now we move hero to let turtle pass
		hero.setTarget(new Point(190,60));
		hero.setGhost(true);
		
		renderFrames(400);
		
		// And now, check that turtle is arrived
		Assert.assertTrue(turtle.getTarget() == null);
		assertLocation(turtle, targetTurtle, true);
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
