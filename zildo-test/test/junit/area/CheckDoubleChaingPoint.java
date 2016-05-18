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
import zildo.client.ClientEventNature;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
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
		while (EngineZildo.scriptManagement.isScripting()) {
			renderFrames(1);
		}

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

		renderFrames(12);
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
		System.out.println("Suite");
		zildo.attack();
		renderFrames(10);
		Assert.assertTrue(zildo.getPv() == 4);
		System.out.println(waterLily.x);
	}
}
