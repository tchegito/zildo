package junit.fwk.display;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.ClientMainLoop;
import zildo.client.ClientEngineZildo;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.persos.ControllablePerso;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

public class TestSpriteDisplay extends EngineUT {

	SpriteDisplayMocked sd;
	
	@Before
	public void init() {
		sd = (SpriteDisplayMocked) ClientEngineZildo.spriteDisplay;
	}
	/** Ensure that goodies are well displayed behind hero and not above him if there's higher on the screen.
	 * This was wrong because of a correction done for the turtle (y+=sprite.z*2;) ==> see if both can work out.**/
	@Test
	public void goodiesBehindHero() {
		mapUtils.loadMap("prisonext");
		PersoPlayer hero = spawnZildo(57*16,  417);
		waitEndOfScripting();
		EngineZildo.mapManagement.getCurrentMap().attackTile(1, new Point(57, 25), null);
		renderFrames(5);
		// Find goodie (always on this tile)
		ElementGoodies goodie = null;
		List<SpriteEntity> entities = EngineZildo.spriteManagement.getSpriteEntities(null);
		for (SpriteEntity e : entities) {
			if (e.isGoodies()) {
				goodie = (ElementGoodies) e;
			}
		}
		Assert.assertNotNull(goodie);
		
		sd.setEntities(entities);
		sd.setZildoId(hero.getId());
		sd.updateSpritesClient(new Point(704, 297));
		// Check that goodie is displayed behind hero
		checkOrder(goodie);

		// Wait goodies is on the floor
		while (goodie.z > 4) {
			renderFrames(1);
		}
		sd.setEntities(entities);
		sd.updateSpritesClient(new Point(704, 297));
		checkOrder(goodie);
	}
	
	@Test
	public void squirrelInFrontOfTurtle() {
		mapUtils.loadMap("sousbois3");
		PersoPlayer squirrel = spawnZildo(465, 576);
		squirrel.setAppearance(ControllablePerso.PRINCESS_BUNNY);
		waitEndOfScripting();

		// Transmit entities and place camera
		ClientEngineZildo.mapDisplay.setCamera(new Point(308, 470));
		List<SpriteEntity> entities = EngineZildo.spriteManagement.getSpriteEntities(null);
		sd.setEntities(entities);
		sd.setZildoId(squirrel.getId());
		renderFrames(1);
		
		squirrel.jump();
		renderFrames(5);
		simulateDirection(0, -1);
		Perso turtle = EngineZildo.persoManagement.getNamedPerso("sacher");
		checkOrder(turtle);
		while (!squirrel.isOnPlatform()) {
			renderFrames(1);
		}
		checkOrder(turtle);
	}

	/** Check that hero is displayed after another entity. **/
	private void checkOrder(SpriteEntity other) {
		SpriteEntity[][] order = sd.getTabTri();
		int posOther = -1;
		int posZildo = -1;
		for (int i=0;i<order.length;i++) {
			for (int j=0;j<order[i].length;j++) {
				SpriteEntity ent = order[i][j];
				if (ent != null) {
					if (ent.isZildo()) {
						posZildo = i;
						System.out.println("Hero: "+ent.y);
					} else if (ent == other) {
						posOther = i;
						System.out.println("Other: "+ent.y);
					}
				}
			}
		}
		Assert.assertNotEquals("Hero isn't on the displayed area !", -1, posZildo);
		Assert.assertNotEquals("Other entity ("+other+") isn't on the displayed area !", -1, posOther);
		Assert.assertTrue("Hero should have been after entity to be displayed correctly !", posZildo > posOther);
	}
	
	
	@Test @ClientMainLoop
	public void spritesOverFont() {
		accomplishQuest("gardelaitier, gardelaitier_win, findDragonPortalKey");
		mapUtils.loadMap("coucou");
		spawnZildo(368, 985);
		simulateDirection(0, 1);
		renderFrames(100);
		Assert.assertEquals("prisonext", EngineZildo.mapManagement.getCurrentMap().getName());
		Perso darkGuy = (Perso) findEntityByDesc(PersoDescription.DARKGUY);
		Assert.assertNotNull(darkGuy);
		// Wait for the dialog
		while (EngineZildo.dialogManagement.getQueue().isEmpty()) {
			renderFrames(1);
		}
		Assert.assertEquals(2, darkGuy.getFloorForSort());
		renderFrames(10);
		
		List<SpriteEntity> entities = EngineZildo.spriteManagement.getSpriteEntities(null);
		sd.setEntities(entities);
		renderFrames(100);
		
		// Find a displayed font

		List<SpriteEntity> fonts = sd.getEntities().stream()
				.filter(entity -> entity.getEntityType().isFont())
				.collect(toList());
 		System.out.println(fonts.size());
 		System.out.println(fonts.get(0).getFloorForSort());
 		for (SpriteEntity font : fonts) {
 	 		Assert.assertTrue(fonts.get(0).isVisible());
 			checkOrderDarkGuy(darkGuy, font);
 		}
	}
	
	/** Check that hero is displayed after another entity. **/
	private void checkOrderDarkGuy(SpriteEntity first, SpriteEntity second) {
		SpriteEntity[][] order = sd.getTabTri();
		int posSecond = -1;
		int posFirst = -1;
		for (int i=0;i<order.length;i++) {
			for (int j=0;j<order[i].length;j++) {
				SpriteEntity ent = order[i][j];
				if (ent != null) {
					if (ent == first) {
						posFirst = i;
						System.out.println("First: "+ent.y);
					} else if (ent == second) {
						posSecond = i;
						System.out.println("Second: "+ent.y);
					}
				}
			}
		}
		Assert.assertNotEquals("First isn't on the displayed area !", -1, posFirst);
		Assert.assertNotEquals("Second entity ("+second+") isn't on the displayed area !", -1, posSecond);
		Assert.assertTrue("First should have been after second to be displayed correctly !", posFirst < posSecond);
	}
}
