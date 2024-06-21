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
import tools.MapUtils;
import zildo.fwk.file.EasyBuffering;
import zildo.monde.Hasard;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.persos.ControllablePerso;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Pointf;
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
		Pointf target = new Pointf(160 + 16, 30);
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
		Pointf target = new Pointf(160 + 16, 30);
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
		Pointf target = new Pointf(160 + 16, 30);
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
		Pointf targetA = new Pointf(300, 80);
		Perso persoA = spawnTypicalPerso("A", 100, 80);
		persoA.setTarget(targetA);
		
		// Spawn a character B
		spawnTypicalPerso("B", 200, 80);
		
		// Let's rock !
		renderFrames(500);
		
		assertLocation(persoA, targetA, false);
		
		targetA = new Pointf(persoA.getX(), 20);
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
		Pointf target = new Pointf(16 *60 + 8, 550);
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
		Pointf targetA = new Pointf(300, 80);
		Perso persoA = spawnTypicalPerso("A", 100, 80);
		persoA.setTarget(targetA);
		
		// Spawn a character A
		Pointf targetB = new Pointf(160 + 16, 30);
		Perso persoB = new PersoPlayer(200, 80, ZildoOutfit.Zildo);
		clientState.zildoId = persoB.getId();
		
		// Let's rock !
		renderFrames(500);
		
		assertLocation(persoA, targetA, false);
		
		targetA = new Pointf(persoA.getX(), 20);
		persoA.setTarget(targetA);
		
		// Let's rock !
		renderFrames(500);
		
		assertLocation(persoA, targetA, true);

		// Block with Zildo
		targetB = new Pointf(persoA.getX(), persoA.getY());
		persoB.setTarget(targetB);

		// Let's rock !
		renderFrames(500);
		
		assertLocation(persoB, targetB, false);
		
		// And try to move blocked character
		targetA = new Pointf(persoA.getX()+100, persoA.getY());
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
	}
	
	// Hero tries to jump over brambles (but he can't because he jumps at 8 and brambles are at 12). So he should never managed to pass through.
	@Test
	public void projection3() {
		mapUtils.loadMap("sousbois7");
		waitEndOfScripting();
		
		PersoPlayer zildo = spawnZildo(318,  342);
		zildo.setAppearance(ControllablePerso.PRINCESS_BUNNY);
		zildo.setX(318.06998f);
		//zildo.setX(321.89157f);
		simulateDirection(new Vector2f(0, 1f));
		zildo.jump();
		renderFrames(40);

		Assert.assertEquals("Hero should have been hit and loose HP !", 5, zildo.getPv());
		Assert.assertEquals(true, zildo.isBlinking());
		assertNotBlocked(zildo);
		renderFrames(30);
		Assert.assertTrue("Hero shouldn't have passed through brambles !", zildo.getY() < 365);
	}
	
	// Check that firething doesn't get stucked, after save+load on the same map
	@Test
	public void enemyNeverStucked() {
		mapUtils.loadMap("eleog");
		waitEndOfScripting();
		Perso fire1 = EngineZildo.persoManagement.getNamedPerso("fire1");
		assertPersoInLand(fire1);
		renderFrames(1);
		System.out.println(fire1);
		EasyBuffering buffer = new EasyBuffering();
		EngineZildo.mapManagement.getCurrentMap().serialize(buffer);
		Area.deserialize(buffer, "eleog_2", true);
		fire1 = EngineZildo.persoManagement.getNamedPerso("fire1");
		assertPersoInLand(fire1);
	}
	
	private void assertPersoInLand(Perso p) {
		Assert.assertFalse(EngineZildo.mapManagement.collide(p.x, p.y, p));
	}
	
	/** Issue 161: monster was passing through door and reached map's outside **/
	@Test
	public void monsterOutside() {
		// Load the cave in Lugdunia and remove the north door
		waitEndOfScripting();
		mapUtils.loadMap("foretg");
		EngineZildo.spriteManagement.getNamedElement("inDoor").dying=true;
		waitEndOfScripting();
		
		// Force the hazard
		EngineZildo.hasard = new Hasard() {
			public double rand() { return 0.16f; }
		};
		
		EngineZildo.spriteManagement.deleteSprite(persoUtils.persoByName("new"));
		Perso fireThing = persoUtils.persoByName("new");
		fireThing.setPos(new Vector2f(68 + 30, 303 -30 + 1));
		fireThing.setAngle(Angle.OUEST);
		for (int i=0;i<300;i++) {
			renderFrames(1);
			Assert.assertTrue("Monster should not be able to pass through that chaining point !", fireThing.getX() > 30);
			//System.out.println(fireThing);
		}
	}
	
	// Hero swings his sword as an enemy is in front of it, but at a different floor.
	@Test
	public void hitOnWrongFloor() {
		mapUtils.loadMap("prisonext");
		waitEndOfScripting();
		Perso darkGuy = spawnPerso(PersoDescription.DARKGUY, "dark", 356, 140);
		darkGuy.setFloor(2);
		darkGuy.setPv(4);
		darkGuy.setInfo(PersoInfo.ENEMY);
		PersoPlayer zildo = spawnZildo(356,  160);
		zildo.setWeapon(new Item(ItemKind.SWORD));
		zildo.setAngle(Angle.NORD);
		zildo.attack();
		renderFrames(20);
		
		// He shouldn't be hit
		Assert.assertEquals(4, darkGuy.getPv());
	}
	
	@Test
	@Ignore	// TODO: put back that test later for Episode4
	public void hoodedMoveAlong() {
		mapUtils.loadMap("nature2");
		spawnZildo(198, 73);
		waitEndOfScripting();
		Perso hooded = persoUtils.persoByName("hooded");
		hooded.x = 245;
		hooded.y = 128;
		//hooded.setTarget(new Pointf(221, 100));
		renderFrames(10);
		Assert.assertNotNull(hooded.getTarget());
		// Wait and check if monster gets stuck
		
		while (hooded.deltaMoveX != 0 || hooded.deltaMoveY != 0) {
			System.out.println(hooded);
			assertNotBlocked(hooded);
			renderFrames(1);
		} // 227,103
		Assert.assertNull("Hooded should have reach its target !", hooded.getTarget());
		System.out.println(hooded.getTarget());
	}
}