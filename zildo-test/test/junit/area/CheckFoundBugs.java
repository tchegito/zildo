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

import java.util.List;

import junit.perso.EngineUT;

import org.junit.Assert;
import org.junit.Test;

import zildo.client.ClientEventNature;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Case;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
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
	
	@Test
	public void npeRestoreFirstTime() {	// Issue 84 (NPE)
		mapUtils.loadMap("coucou");
		waitEndOfScripting();
		// Spawn hero near a coast, and send him to the water 
		PersoPlayer zildo = spawnZildo(581, 902);
		EngineZildo.scriptManagement.execute("intro", true);
		waitEndOfScriptingPassingDialog();
		
		Assert.assertFalse("Dialog should be over !", EngineZildo.getClientState().dialogState.isDialoguing());
		
		// Intro is over, so place hero near a coast, and send him to the water
		zildo.setPos(new Vector2f(581, 902));
		zildo.setPv(1);
		simulateDirection(0, -1);
		renderFrames(40);
		waitEndOfScripting();
		Assert.assertEquals(MouvementZildo.SAUTE, zildo.getMouvement());
		renderFrames(40);
		Assert.assertEquals(MouvementZildo.VIDE, zildo.getMouvement());
		// Player has fallen into water
		boolean error = false;
		try {
			waitEndOfScriptingPassingDialog();
		} catch (NullPointerException e) {
			e.printStackTrace();
			error = true;
		}
		Assert.assertFalse(error);
	}
	
	private List<SpriteEntity> sprites() {
		return EngineZildo.spriteManagement.getSpriteEntities(null);
	}
	// Issue 86
	@Test
	public void checkProjectileHit() {
		mapUtils.loadMap("igorlily");
		PersoPlayer zildo = spawnZildo(102, 153);
		zildo.setWeapon(new Item(ItemKind.SWORD));
		waitEndOfScripting();
		
		// Get snakes
		Perso snake = null;
		int nbSnake = 0;
		for (SpriteEntity entity : sprites()) {
			if (entity.getDesc() == PersoDescription.FLYINGSERPENT) {
				int distance = (int) Point.distance(entity.x, entity.y, zildo.x, zildo.y);
				if (distance < 160) {
					Assert.assertNull(snake);
					snake = (Perso) entity;
				}
				nbSnake++;
			}
		}
		Assert.assertEquals(nbSnake, EngineZildo.collideManagement.getTabColli().size());
		
		int initialSnakePv = snake.getPv();
		int initialHeroPv = zildo.getPv();
		
		// Wait for snake to launch some projectile
		SpriteEntity projectile = null;
		while (projectile == null) {
			for (SpriteEntity entity : sprites()) {
				if (entity.getDesc() == ElementDescription.BROWNSPHERE1 && entity.isVisible()) {
					projectile = entity;
					break;
				}
			}
			renderFrames(1);
		}
	
		zildo.setPos(new Vector2f(102, 162));
		// Now we wait for projectile to be just in front of hero
		Assert.assertNotNull(projectile);
		zildo.setAngle(Angle.NORD);
		boolean closeToHero = false;
		while (!closeToHero && !projectile.dying) {
			int distance = (int)Point.distance(zildo.x, zildo.y, projectile.x, projectile.y);
			System.out.println(projectile.x+","+projectile.y+" =>  "+distance);
			if (distance < 20) {
				closeToHero = true;
			}
			renderFrames(1);
		}
		zildo.attack();
		while (!projectile.dying) {
			//Assert.assertFalse(zildo.isWounded());
			renderFrames(1);
		}
		Assert.assertEquals("Snake should not have lost any HP !", initialSnakePv, snake.getPv());
		Assert.assertEquals(initialHeroPv,  zildo.getPv());
	}
	
	@Test
	public void buttonsConflict() {
		mapUtils.loadMap("igorvillage");
		PersoPlayer zildo = spawnZildo(546,221);
		waitEndOfScripting();
		
		simulateDirection(-1, 0);
		while (true) {
			renderFrames(4);
			if (zildo.deltaMoveX == 0) break;
		}
		// Hero must be in front of Bilel
		simulatePressButton(Keys.Q, 1);	// Q => Action button (see KeysConfiguration)
		Perso bilel = zildo.getDialoguingWith();
		Assert.assertNotNull(bilel);
		Assert.assertEquals("bilel", bilel.getName());
		while (zildo.getDialoguingWith() != null && !zildo.isInventoring()) {
			simulatePressButton(Keys.Q, 1);
			simulatePressButton(Keys.DIALOG_FRAME, 1);
		}
		// Wait SEMIFADE out
		renderFrames(1);
		Assert.assertEquals(ClientEventNature.FADING_OUT, clientState.event.nature);
		while (clientState.event.nature != ClientEventNature.NOEVENT) {
			renderFrames(1);
		}
		// Hero should be in "buying" screen
		Assert.assertTrue(zildo.isInventoring());
		// Press a key to buy ==> stay on same screen
		simulatePressButton(Keys.Q, 1);
		Assert.assertEquals(ClientEventNature.NOEVENT, clientState.event.nature);
		// Press a key to quit ==> semifade should happen
		simulatePressButton(Keys.X, 1);
		// Wait SEMIFADE in
		Assert.assertEquals(ClientEventNature.FADING_IN, clientState.event.nature);
		while (clientState.event.nature != ClientEventNature.NOEVENT) {
			renderFrames(1);
		}
		Assert.assertFalse(zildo.isInventoring());
		Assert.assertFalse(clientState.dialogState.isDialoguing());
		zildo.setPos(new Vector2f(624,240));
		simulateDirection(0, -1);
		renderFrames(50);
		Assert.assertTrue(EngineZildo.mapManagement.isChangingMap(zildo));
		renderFrames(50);
		Assert.assertEquals("igorv2", EngineZildo.mapManagement.getCurrentMap().getName());
	}
	
	@Test
	public void buttonsConflictAndroid() {
		// Same for Android
		PlatformDependentPlugin.currentPlugin = KnownPlugin.Android;
		buttonsConflict();
	}
}