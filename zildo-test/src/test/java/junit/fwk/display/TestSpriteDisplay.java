package junit.fwk.display;

import static zildo.server.EngineZildo.hasard;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.client.ClientEngineZildo;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

public class TestSpriteDisplay extends EngineUT {

	SpriteDisplayMocked sd
	;
	/** Ensure that goodies are well displayed behind hero and not above him if there's higher on the screen.
	 * This was wrong because of a correction done for the turtle (y+=sprite.z*2;) ==> see if both can work out.**/
	@Test
	public void goodiesBehindHero() {
		mapUtils.loadMap("prisonext");
		PersoPlayer zildo = spawnZildo(57*16,  417);
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
		
		sd = (SpriteDisplayMocked) ClientEngineZildo.spriteDisplay;
		sd.setEntities(entities);
		sd.updateSpritesClient(new Point(704, 297));
		// Check that goodie is displayed behind hero
		checkOrder();
		sd.setEntities(Collections.emptyList());

		// Wait goodies is on the floor
		while (goodie.z > 4) {
			renderFrames(1);
		}
		sd.setEntities(entities);
		sd.updateSpritesClient(new Point(704, 297));
		checkOrder();
	}

	private void checkOrder() {
		SpriteEntity[][] order = sd.getTabTri();
		int posGoodies = 0;
		int posZildo = 0;
		for (int i=0;i<order.length;i++) {
			for (int j=0;j<order[i].length;j++) {
				SpriteEntity ent = order[i][j];
				if (ent != null) {
					if (ent.isZildo()) {
						posZildo = i;
					} else if (ent.isGoodies()) {
						posGoodies = i;
					}
				}
			}
		}
		Assert.assertTrue("Hero should have been after goodies to be displayed correctly !", posZildo > posGoodies);

	}
}
