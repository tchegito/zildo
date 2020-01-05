package junit.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.junit.Test;

import zildo.monde.collision.PersoCollision;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.server.EngineZildo;
import zildo.server.SpriteManagement;

public class TestPersoCollision {

	@Test
	public void addAndRemove() {
		PersoCollision colli = new PersoCollision();
		EngineZildo.spriteManagement = new SpriteManagement();
		EngineZildo.spriteManagement.persoColli = colli;
		
		Element elem = new Element();
		// Will do twice: one for background, one for foreground
		for (int i=0;i<2;i++) {
			// No one in the field
			assertNull(colli.checkCollision(160,  100, elem, 10));
			// Add one then recheck
			Perso clampin = new PersoNJ();
			clampin.setPv(6);
			clampin.x = 160;
			clampin.y = 100;
			clampin.initializeId(SpriteEntity.class);
			if (i == 1) {
				clampin.setForeground(true);
				elem.setForeground(true);
			}
			colli.initFrame(Collections.singletonList(clampin));
			assertEquals(clampin, colli.checkCollision(160,  100, elem, 10));
			
			// Remove then recheck
			colli.notifyDeletion(clampin);
			assertNull(colli.checkCollision(160,  100, elem, 10));
		}
	}
}
