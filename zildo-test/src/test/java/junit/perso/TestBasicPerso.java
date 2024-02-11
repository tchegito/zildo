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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.dialog.Behavior;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Vector2f;
import zildo.monde.util.Zone;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class TestBasicPerso extends EngineUT {

	@Test
	public void facing() {
		Perso a = spawnTypicalPerso("a", 160, 100);
		a.setAngle(Angle.EST);
		Perso b = spawnTypicalPerso("b", 250, 130);
		b.setAngle(Angle.OUEST);
		Assert.assertTrue(a.isFacing(b));
		Assert.assertTrue(b.isFacing(a));
		
		a.setAngle(Angle.NORD);
		Assert.assertTrue(!a.isFacing(b));
		Assert.assertTrue(b.isFacing(a));
	}
	
	@Test
	public void facingMarginal() {
		Perso hero = spawnTypicalPerso("hero", 835, 187);
		hero.setAngle(Angle.OUEST);
		Perso willOWist = spawnTypicalPerso("wow", 834, 235);
		
		Assert.assertFalse(hero.isFacing(willOWist));
		hero.setAngle(Angle.SUD);
		Assert.assertTrue(hero.isFacing(willOWist));
		
	}
	
	// Check that hero can't have more than 999 gold coins
	@Test
	public void maxMoney() {
		PersoPlayer hero = spawnZildo(160, 100);
		hero.setMoney(990);
		ElementGoodies money = new ElementGoodies();
		money.setDesc(ElementDescription.GOLDPURSE1);
		// Take 5 gold (=>995)
		hero.pickGoodies(money, 5);
		renderFrames(2);
		Assert.assertEquals(995, hero.getMoney());
		// Again (=>999)
		hero.pickGoodies(money, 5);
		renderFrames(2);
		Assert.assertEquals(999, hero.getMoney());
		// Again (still)
		hero.pickGoodies(money, 5);
		renderFrames(2);
		Assert.assertEquals(999, hero.getMoney());
	}
	
	@Test
	public void stealMoney() {
		PersoPlayer hero = spawnZildo(160, 100);
		hero.setMoney(400);
		hero.pickGoodies(null, -999);
		renderFrames(2);
		Assert.assertEquals(0, hero.getMoney());
	}
	
	/** This test was to verify Issue 124.
	 * It places every kind of character with hero around him, and they try to dialog.
	 * That should turn character in front of hero, testing its PersoNJ#finaliseComportement method in every case.
	 * 
	 * Actually, it allowed also to fix a rare collision issue **/
	@Test
	public void speakEachAngle() {
		PersoPlayer hero = spawnZildo(160,100);
		waitEndOfScripting();
		
		
		List<Angle> interestingAngles = Arrays.asList(Angle.NORD, Angle.EST, Angle.OUEST, Angle.SUD);
		List<PersoDescription> erreurs = new ArrayList<>();
		for (PersoDescription desc : PersoDescription.values()) {
			System.out.println(desc);
			String persoName = "test"+desc;
			Perso perso = spawnPerso(desc, persoName, 180, 100);
			perso.setZone_deplacement(new Zone((int) perso.x, (int) perso.y, 50, 50));
			// Create fake dialog for our character
			mapUtils.area.getMapDialog().addBehavior(new Behavior(persoName));
			renderFrames(1);
			
			if (perso.getInfo() == PersoInfo.ENEMY || perso.isZildo()) {
				System.out.println(" ==> SKIP: Can't talk to enemy/zildo");
			} else if( desc.isTakable() || desc.isDamageable()) {
				System.out.println(" ==> SKIP: Can't talk to takable/damageable characters");
			} else if (perso.isForeground() || perso.flying) {
				System.out.println(" ==> SKIP: Can't talk to foreground/flying characters");
			} else if (desc == PersoDescription.TURTLE || desc == PersoDescription.COAL_COLD) {
				System.out.println(" ==> SKIP: Can't talk to turtles");
			} else {
				// Try to talk from each angle
				Angle current = null;
				try {
					for (Angle a : interestingAngles) {
						current = a;
						hero.setPos(new Vector2f(perso.x + a.coords.x * 8, perso.y + a.coords.y * 4));
						hero.setAngle(a.opposite());
						simulatePressButton(KeysConfiguration.PLAYERKEY_ACTION.code, 1);
						// Check that hero is talking to our character
						Assert.assertEquals(perso, hero.getDialoguingWith());
						
						EngineZildo.dialogManagement.stopDialog(clientState, true);
						renderFrames(1);
						Assert.assertNull(hero.getDialoguingWith());
					}
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Erreur sur le perso  avec l'angle "+current+" message="+ e.getMessage());
					erreurs.add(desc);
					EngineZildo.dialogManagement.stopDialog(clientState, true);
					simulateKeyPressed();
				}
			}
			perso.setAngle(Angle.NORD);
			perso.die();
			renderFrames(2);
			Assert.assertNull(persoUtils.persoByName(persoName));
		}
		Assert.assertTrue("List of failures should have been empty but is "+erreurs,  erreurs.isEmpty());
	}
	
	@Test
	public void observableNotAlertable() {
		mapUtils.loadMap("polaky5");
		PersoPlayer zildo = spawnZildo(647, 546); //585, 569);
		zildo.setAngle(Angle.NORD);
		waitEndOfScripting();
		Perso lulu = persoUtils.persoByName("lulu");
		Assert.assertNotNull(lulu);
		Assert.assertEquals(MouvementPerso.OBSERVE, lulu.getQuel_deplacement());
		Assert.assertFalse(lulu.isAlerte());
		zildo.takeSomething(648, 535, ElementDescription.JAR, null);
		renderFrames(5);
		Assert.assertNotNull(zildo.getEn_bras());
		zildo.setAngle(Angle.OUEST);
		simulateDirection(-1, 0);
		renderFrames(30);
		zildo.throwSomething();
		renderFrames(80);
		Assert.assertFalse(lulu.isAlerte());
	}
}
