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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.collision.Collision;
import zildo.monde.collision.Rectangle;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
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
		Pointf targetA = new Pointf(100, 190);
		Perso persoA = spawnPerso(PersoDescription.TURTLE, "Turtle", 100, 80);
		persoA.setQuel_deplacement(MouvementPerso.MOBILE_WAIT, true);
		persoA.setTarget(targetA);
		
		// Spawn character (non-hero)
		Perso persoB = spawnTypicalPerso("non-hero", 100, 150);
		
		// Let's rock !
		renderFrames(500);
		
		Assert.assertNull("Character A shouldn't have a target !", persoA.getTarget());
		// Check that persoB has moved, to let turtle pass
		assertLocation(persoB, new Pointf(100, 150), false);
		// Check that turtle is arrived
		assertLocation(persoA, targetA, true);
	}
	
	/** Proves that a character can push any blocking character when he's on its way **/
	@Test
	public void persoPushPerso() {
		// Spawn a character A
		mapUtils.loadMap("eleom1");
		EngineZildo.persoManagement.clearPersos(true);
		waitEndOfScripting();
		Pointf targetA = new Pointf(78, 80);
		Perso eleo = spawnPerso(PersoDescription.ELEORIC, "eleoric", 78, 115);
		eleo.setTarget(targetA);
		eleo.setGhost(true);
		
		// Spawn character (non-hero)
		Perso blocker = spawnTypicalPerso("non-hero", 78, 96);
		
		// Let's rock !
		renderFrames(500);
		
		Assert.assertNull("Eleoric shouldn't have a target !", eleo.getTarget());
		// Check that persoB has moved, to let turtle pass
		assertLocation(blocker, new Pointf(100, 150), false);
		// Check that character is arrived
		assertLocation(eleo, targetA, true);
		// Check that ScriptExecutor has set ghost at false, because 'blocker' has been declared as 'involved'
		Assert.assertFalse(blocker.isGhost());
	}
	
	/** Proves that turtle can't push hero. Instead, it has to wait for him to move by himself. **/
	@Test
	public void turtleCantPushHero() {
		// Spawn a character A
		Pointf targetTurtle = new Pointf(200, 80);
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
		hero.setTarget(new Pointf(190,60));
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
		Assert.assertTrue( Collision.checkCollisionCircles((int) turtle.x, (int) turtle.y, (int) hero.x, (int) hero.y, 7, 7));
		// Check with circle and zone
		Assert.assertFalse( new Rectangle(turtle.getMover().getZone()).isCrossingCircle(new Point(hero.x, hero.y), 7) );
		
		Assert.assertFalse(EngineZildo.mapManagement.collide(hero.x, hero.y, hero));
	}
	
	/** Found a specific location during christa's scene in the farm, where player meets an exception in 
	/* IdGenerator, because of PathFinder algorithm was failing. It tried to move a blocking character, but never
	 * succeeded to find a good spot to go. So it turned in an infinite loop.
	 * To fix this test, we select a good position according to the fact that blocking character will cross the first one or not.
	 */
	@Test
	public void characterCanPushHero() {
		EngineZildo.scriptManagement.accomplishQuest("vactoToTheFarm", false);
		EngineZildo.scriptManagement.accomplishQuest("vactoRequest", false);
		
		mapUtils.loadMap("fermem2");
		PersoPlayer zildo = spawnZildo(new Vector2f(289,53.5));
		Perso christa = EngineZildo.persoManagement.getNamedPerso("christa");
		Assert.assertNotNull(christa);
		Assert.assertFalse(EngineZildo.mapManagement.collide(christa.x, christa.y, christa));
		zildo.setAngle(Angle.NORD);
		waitEndOfScripting();
		
		assertNotBlocked(zildo);
		assertNotBlocked(christa);

		talkAndCheck("fermem2.c.0");
		talkAndCheck("fermem2.c.2");
		goOnDialog();
		// Dialog shoud have triggered following quest
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestProcessing("christaAccept"));
		
		waitEndOfScriptingPassingDialog();
		Assert.assertNull(EngineZildo.persoManagement.getNamedPerso("christa"));
	}
	
	
	@Test
	public void pushCrates() {
		mapUtils.loadMap("chatcou5");
		PersoPlayer zildo = spawnZildo(150, 135);
		waitEndOfScripting();
		
		// 1) First crate
		simulateDirection(-1,  0);
		renderFrames(10);
		SpriteEntity crate = zildo.getPushingSprite();
		Assert.assertNotNull(crate);
		
		Point initialLocation = new Point(crate.x, crate.y);
		while (zildo.getPushingSprite() != null) {
			renderFrames(1);
		}
		
		while (((Element)crate).vx != 0) {
			renderFrames(1);
			Assert.assertEquals(MouvementZildo.POUSSE, zildo.getMouvement());
		}
		// Check that the moved crate is at the right place
		Assert.assertEquals((int) initialLocation.x - 16, (int) crate.x);
		
		// 2) Next one
		simulateDirection(0, 1);
		renderFrames(5);
		crate = zildo.getPushingSprite();
		Assert.assertNotNull(crate);
		
		simulateDirection(0, -1);
		renderFrames(10);
		crate = zildo.getPushingSprite();
		// This crate is not pushable !
		Assert.assertNull(crate);
	}
	
	@Test
	public void pushOneCrateAtATime() {
		mapUtils.loadMap("chatcou5");
		PersoPlayer zildo = spawnZildo(150, 144);
		waitEndOfScripting();
		
		// 1) First crate
		simulateDirection(-1,  0);
		Map<Integer, SpriteEntity> crates = new HashMap<>();
		for (int i=0;i<40;i++) {
			renderFrames(1);
			for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
				if (entity.getDesc() == ElementDescription.CRATE || entity.getDesc() == ElementDescription.CRATE2) {
					if (entity.getEntityType().isElement() && ((Element)entity).getLinkedPerso() == zildo) {
						crates.put(entity.getId(), entity);
					}
				}
			}
		}
		Assert.assertEquals("We should only have 1 crate !", 1, crates.size());
	}
	
	@Test
	public void cantPushCrateOnObstacle() {
		mapUtils.loadMap("chatcou5");
		PersoPlayer zildo = spawnZildo(150, 156);
		waitEndOfScripting();
		
		// The crate should be blocked because of an obstacle just behind
		simulateDirection(-1,  0);
		renderFrames(30);
		SpriteEntity entity = zildo.getPushingSprite();
		Assert.assertNotNull(entity);
		Assert.assertEquals(EntityType.ELEMENT, entity.getEntityType());
		Element elem = (Element) entity;
		Assert.assertEquals(0f, elem.vx, 0.1f);
	}
	
	
	@Test
	public void pushStanceStopped() {
		// There was a bug when hero enters in a "pushing" stance, but when he moved, he kept that stance
		mapUtils.loadMap("prisonext");
		PersoPlayer zildo = spawnZildo(774, 292);
		waitEndOfScripting();
		
		simulateDirection(0, 1);
		renderFrames(40);
		Assert.assertEquals(MouvementZildo.POUSSE, zildo.getMouvement());
		float y = zildo.getY();
		
		// Change direction => hero should stop pushing
		simulateDirection(-1, 1);
		renderFrames(40);
		Assert.assertTrue(zildo.getY() > y);
		Assert.assertEquals(MouvementZildo.VIDE,  zildo.getMouvement());
	}

}
