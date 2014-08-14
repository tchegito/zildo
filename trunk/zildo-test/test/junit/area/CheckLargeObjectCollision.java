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

package junit.area;

import junit.framework.Assert;
import junit.perso.EngineUT;

import org.junit.Test;

import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.persos.ia.mover.PhysicMoveOrder;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class CheckLargeObjectCollision extends EngineUT{

	SpriteEntity waterLily;
	Perso zildo;
	
	private void init(int x, int y) {
		mapUtils.loadMap("igorvillage");
		EngineZildo.persoManagement.clearPersos(true);

		

		// Spawn water lily
		waterLily = EngineZildo.spriteManagement.spawnSprite(
				ElementDescription.WATER_LEAF,
				x, y,
				false, Reverse.NOTHING, false); // 113,259

		zildo = spawnZildo(x, y);
		clients.get(0).zildoId = zildo.getId();
		clients.get(0).zildo = (PersoZildo) zildo;
		zildo.walkTile(false);
		
		// Wait end of scripts
		while (EngineZildo.scriptManagement.isScripting()) {
			renderFrames(1);
		}

	}
	
	/**
	 * Run the whole thing, and check every frame that Zildo's relative location hasn't changed.
	 * If it happens, so there's definitely a bug, because we don't want platform moves without him.
	 */
	private void runAndCheck() {
		int frame = 0;
		
		Pointf relativeZildoLoc = new Pointf(zildo.x - waterLily.x, zildo.y - waterLily.y);
		while (frame++<500) {
			renderFrames(1);
			Assert.assertTrue("Zildo relative location has changed !! It should not happen !",
					relativeZildoLoc.x == zildo.x - waterLily.x && relativeZildoLoc.y == zildo.y - waterLily.y);
			
		}
	}
	@Test
	public void testMove() {
		init(133, 381);

		Point location = new Point(0, -16);
		waterLily.setMover(new PhysicMoveOrder(location.x, location.y));

		runAndCheck();
	}
	
	@Test
	public void testMoveOnBorder() {
		init(15, 242);

		Point location = new Point(0, 16);
		waterLily.setMover(new PhysicMoveOrder(location.x, location.y));

		runAndCheck();
	}
	
	@Test
	public void testMoveOnBorderOutside() {
		init(-2, 242);

		Point location = new Point(0, 16);
		waterLily.setMover(new PhysicMoveOrder(location.x, location.y));

		runAndCheck();
	}
	// 15,232
}
