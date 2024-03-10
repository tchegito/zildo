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

package junit.area;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.InfoPersos;
import zildo.client.ClientEventNature;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.map.ChainingPoint;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.persos.ia.mover.Mover;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class CheckDoubleChaingPoint extends EngineUT {

	SpriteEntity waterLily;
	PersoPlayer zildo;
	
	private void init(int x, int y) {
		mapUtils.loadMap("igorvillage");
		EngineZildo.persoManagement.clearPersos(true);

		// Wait end of scripts
		waitEndOfScripting();

		// Spawn water lily
		waterLily = EngineZildo.spriteManagement.spawnSprite(
				ElementDescription.WATER_LEAF,
				x, y,
				false, Reverse.NOTHING, false); // 113,259
		waterLily.setName("leaf");
		
		
		zildo = spawnZildo(x, y);
		zildo.walkTile(false);
	}
	
	@Test
	public void leaveMap() {
		init(10, 260);

		zildo.setWeapon(new Item(ItemKind.SWORD));
		zildo.setAngle(Angle.EST);
		zildo.setPv(4);
		zildo.attack();

		renderFrames(14);
		Assert.assertNotNull(waterLily.getMover());
		Mover mover = waterLily.getMover();
		Element placeHolder = mover.getPlaceHolder();
		Assert.assertNotNull(placeHolder);
		
		//System.out.println(zildo.getDelta() + " - " + placeHolder.getDelta());
		Assert.assertEquals("igorlily", EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertEquals(ClientEventNature.CHANGINGMAP_SCROLL, clientState.event.nature);
		waitEndOfScripting();
		Assert.assertEquals("igorlily", EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertTrue(waterLily.isVisible());
		zildo.attack();
		renderFrames(10);
		Assert.assertTrue(zildo.getPv() == 4);
	}
	
	@Test
	public void simple() {
		// Load any map in 64x64 size
		mapUtils.loadMap("foret");
		ChainingPoint ch = new ChainingPoint();
		ch.setPx((short) 0);
		ch.setPy((short) 0);
		ch.setVertical(true);
		ch.setBorder(true);
		ch.getZone(mapUtils.area);

		Assert.assertTrue(ch.isCollide(0, 0, true));
		Assert.assertFalse(ch.isCollide(8, 0, true));
		
		ch= new ChainingPoint();
		ch.setPx((short) 58);
		ch.setPy((short) 28);
		ch.setBorder(true);
		ch.setVertical(true);
		ch.getZone(mapUtils.area);
		Assert.assertTrue(ch.isCollide(58, 65, true));
	}

	// At at time, we had a problem with this room being unwalkable
	// (because of 'vertical' flag refactor on chaining points)
	@Test
	public void horizontalSimple() {
		EngineZildo.scriptManagement.accomplishQuest("boss_turret", false);
		EngineZildo.scriptManagement.accomplishQuest("killBossTurret1", false);
		mapUtils.loadMap("prison14");
		spawnZildo(160, 55);
		waitEndOfScripting();
		simulateDirection(0, -1);
		assertMapIsChangingToward("prison13");
	}
	
	@Test
	public void doubleHorizontal() {
		mapUtils.loadMap("chateaucoucou3");
		spawnZildo(559, 58);
		waitEndOfScripting();
		simulateDirection(0, -1);
		assertMapIsChangingToward("chatcou5");
	}
	
}
