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

import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.persos.ControllablePerso;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class TestCollision extends EngineUT {


	@Test
	public void closedDoor() {
		// Add closed door tile
		mapUtils.createClosedDoor(10,4);
		// Spawn a character below
		Point target = new Point(160 + 16, 30);
		Perso perso = spawnTypicalPerso("A", 160 + 16, 160);
		perso.setTarget(target);
		perso.setOpen(true);
		
		// Let's rock !
		renderFrames(500);
		
		// He shouldn't be blocked
		assertLocation(perso, target, true);
		// Check that door has been opened
		
	}
	
	@Test
	public void openedDoor() {
		// Add opened door tile
		mapUtils.createOpenedDoor(10,4);
		// Spawn a character below
		Point target = new Point(160 + 16, 30);
		Perso perso = spawnTypicalPerso("A", 160 + 16, 160);
		perso.setTarget(target);
		
		// Let's rock !
		renderFrames(500);
		
		// He should be blocked
		assertLocation(perso, target, true);
		
		// Check that door has been opened
		int doorLeft=mapUtils.area.readmap(10, 4);
		int doorRight=mapUtils.area.readmap(11, 4);
		Assert.assertTrue("Door should have been opened !", doorLeft == (256+58) && doorRight == (256+59));
	}
	
	@Test
	public void stairs() {
		// Add closed door tile
		EngineZildo.mapManagement.getCurrentMap().writemap(10, 4, 256*3 + 89);
		EngineZildo.mapManagement.getCurrentMap().writemap(11, 4, 256*3 + 90);
		// Spawn a character below
		Point target = new Point(160 + 16, 30);
		Perso perso = spawnTypicalPerso("A", 160 + 16, 160);
		perso.setTarget(target);
		
		// Let's rock !
		renderFrames(500);
		
		// He should be blocked
		assertLocation(perso, target, false);
	}
	
	@Test
	public void crossing() {
		// Spawn a character A
		Point targetA = new Point(300, 80);
		Perso persoA = spawnTypicalPerso("A", 100, 80);
		persoA.setTarget(targetA);
		
		// Spawn a character B
		spawnTypicalPerso("B", 200, 80);
		
		// Let's rock !
		renderFrames(500);
		
		assertLocation(persoA, targetA, false);
		
		targetA = new Point(persoA.getX(), 20);
		persoA.setTarget(targetA);
		
		// Let's rock !
		renderFrames(500);
		
		assertLocation(persoA, targetA, true);
	}

	
	@Test
	public void doorBorder() {
		
		// Add closed door tile
		MapUtils map = new MapUtils();
		map.area.setDim_x(64);
		map.area.setDim_y(64);
		map.createClosedDoor(59, 35);
		// Spawn a character below
		Perso perso = spawnTypicalPerso("A", 16 * 60+8, 617);
		Point target = new Point(16 *60 + 8, 550);
		perso.x += 0.49786f; 
		perso.y += 0.3603f;
		perso.setTarget(target);
		perso.setOpen(true);
		perso.setSpeed(1.5f);
		
		// Let's rock !
		renderFrames(150);
		
		// check if character is blocked
		assertNotBlocked(perso);
		
		// Check that door has been opened
		int doorLeft=map.area.readmap(59, 35);
		int doorRight=map.area.readmap(60, 35);
		Assert.assertTrue("Door should have been opened !", doorLeft == (256+58) && doorRight == (256+59));

	}
	
	// This one is not working yet, because character avoid isn't implemented now. This is for the future.
	//@Test
	public void crossingZildo() {
		// Spawn a character A
		Point targetA = new Point(300, 80);
		Perso persoA = spawnTypicalPerso("A", 100, 80);
		persoA.setTarget(targetA);
		
		// Spawn a character A
		Point targetB = new Point(160 + 16, 30);
		Perso persoB = new PersoPlayer(200, 80, ZildoOutfit.Zildo);
		clientState.zildoId = persoB.getId();
		
		// Let's rock !
		renderFrames(500);
		
		assertLocation(persoA, targetA, false);
		
		targetA = new Point(persoA.getX(), 20);
		persoA.setTarget(targetA);
		
		// Let's rock !
		renderFrames(500);
		
		assertLocation(persoA, targetA, true);

		// Block with Zildo
		targetB = new Point(persoA.getX(), persoA.getY());
		persoB.setTarget(targetB);

		// Let's rock !
		renderFrames(500);
		
		assertLocation(persoB, targetB, false);
		
		// And try to move blocked character
		targetA = new Point(persoA.getX()+100, persoA.getY());
		persoA.setTarget(targetA);
		
		// Let's rock !
		renderFrames(500);
		
		assertLocation(persoA, targetA, true);
	}
	
	// Check that hero walking on bramble get the right collision
	@Test
	public void projection() {
		mapUtils.loadMap("sousbois7");
		waitEndOfScripting();
		
		PersoPlayer zildo = spawnZildo(310, 373 + 10);
		zildo.setX(310.51776f);
		zildo.setAppearance(ControllablePerso.PRINCESS_BUNNY);
		simulateDirection(0, -1);
		renderFrames(20);
		
		// Check that hero was hit, has the right move, and is not blocked after the shock
		Assert.assertEquals(5, zildo.getPv());
		Assert.assertEquals(MouvementZildo.TOUCHE, zildo.getMouvement());
		assertNotBlocked(zildo);
	}
	
	// Check that after a shock, during hero's blinking, he can't pass through brambles
	@Test
	public void projection2() {
		mapUtils.loadMap("sousbois7");
		waitEndOfScripting();
		
		PersoPlayer zildo = spawnZildo(324, 335);
		zildo.setAppearance(ControllablePerso.PRINCESS_BUNNY);
		simulateDirection(0, 1);
		renderFrames(30);

		Assert.assertEquals("Hero should have been hit and loose HP !", 5, zildo.getPv());
		Assert.assertEquals(true, zildo.isBlinking());
		
		// Keep walking
		renderFrames(60);
		Assert.assertEquals(true, zildo.isBlinking());
		Assert.assertTrue("Hero shouldn't have passed through brambles !", zildo.getY() < 363);
		/*		
Perso: x=318.08127, y=370.73297 cx=326.0, cy=357.0
-3.9962134
6.930388
Perso: x=300.5134, y=369.1689 cx=309.0, cy=357.0
-4.576242
6.56186
Perso: x=318.14322, y=356.05673 cx=326.0, cy=357.0
-7.9429603
-0.9536143


Perso: x=321.9082, y=354.69293 cx=326.0, cy=357.0, z=5.6
-6.9686475
-3.9291155
Perso: x=302.60788, y=368.62717 cx=309.0, cy=365.0, z=-1.5497208E-6
-0.86973226
0.49352387
				*/
		//321.9082, 342
	}
	
	@Test
	public void projection3() {
		mapUtils.loadMap("sousbois7");
		waitEndOfScripting();
		
		/*
		Perso: x=318.06998, y=356.6673 cx=326.0, cy=357.0, z=6.5999994
				-7.9929686
				-0.33534348
				*/
		PersoPlayer zildo = spawnZildo(318,  342);
		zildo.setAppearance(ControllablePerso.PRINCESS_BUNNY);
		zildo.setX(318.06998f);
		//zildo.setZ(5.6f);
		simulateDirection(new Vector2f(0, 1f));
		renderFrames(30);

		Assert.assertEquals("Hero should have been hit and loose HP !", 5, zildo.getPv());
		Assert.assertEquals(true, zildo.isBlinking());
		assertNotBlocked(zildo);
		renderFrames(30);
		Assert.assertTrue("Hero shouldn't have passed through brambles !", zildo.getY() < 365);

	}
}