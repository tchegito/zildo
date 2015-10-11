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

import junit.perso.EngineUT;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.items.ItemKind;
import zildo.monde.map.Case;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class CheckFoundBugs extends EngineUT {

	@Test
	public void bugForeRefusingToChange() {
		mapUtils.loadMap("voleurs");
		
		Case c = mapUtils.area.get_mapcase(46, 54);
		Assert.assertTrue(c.getForeTile() != null);
		c.setForeTile(null);
		Assert.assertTrue(c.getForeTile() == null);
		EngineZildo.mapManagement.saveMapFile("voleursBackup.map");
		
		// Reload and check
		mapUtils.loadMap("voleurs");
		
		c = mapUtils.area.get_mapcase(46, 54 + 4);
		Assert.assertTrue(c.getForeTile() == null);
	}
	
	/** Awful bug that made 'HORIZONTAL_BAR' considered as a weapon. Consequences was in cave of the flames,
	 * for example, when one bar disappearead, and another one was 'takeable' as an item ...
	 */
	@Test
	public void wrongWeapon() {
		ElementDescription elemDesc = ElementDescription.BAR_HORIZONTAL;
		ItemKind kind = ItemKind.fromDesc(elemDesc);
		Assert.assertEquals(null, kind);
	}
	
	/** At a moment, we had a NPE when hero goes upstairs **/
	@Test
	public void freezeChainingPoint() {
		mapUtils.loadMap("voleursm2");
		PersoPlayer zildo = spawnZildo(263, 88);
		
		Assert.assertEquals(zildo, EngineZildo.persoManagement.getZildo());
		
		waitEndOfScripting();
		
		simulateDirection(new Vector2f(0, -1));
		
		renderFrames(50);

		Assert.assertTrue(EngineZildo.scriptManagement.isScripting());
		while (EngineZildo.scriptManagement.isScripting()) {
			renderFrames(1);
		}
		Assert.assertEquals("voleursm2u", EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertFalse(zildo.isGhost());
	}
	
	/** At a moment, we had a game freeze (opposition direction than previous one) **/
	@Test
	public void freezeChainingPoint2() {
		mapUtils.loadMap("voleursm2u");
		PersoPlayer zildo = spawnZildo(212, 55);
		
		Assert.assertEquals(zildo, EngineZildo.persoManagement.getZildo());
		
		waitEndOfScripting();
		
		simulateDirection(new Vector2f(1, 0));
		
		renderFrames(50);

		Assert.assertTrue(EngineZildo.scriptManagement.isScripting());
		Assert.assertNotNull(EngineZildo.mapManagement.getChainingPoint());
		while (EngineZildo.scriptManagement.isScripting()) {
			renderFrames(1);
		}
		Assert.assertFalse(zildo.isGhost());
		renderFrames(10);
		Assert.assertEquals("voleursm2", EngineZildo.mapManagement.getCurrentMap().getName());
	}
}