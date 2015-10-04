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
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
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
		
		// Spawn a character A
		Point targetB = new Point(160 + 16, 30);
		Perso persoB = spawnTypicalPerso("B", 200, 80);
		
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
	@Test
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
}