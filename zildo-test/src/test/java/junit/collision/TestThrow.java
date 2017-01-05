package junit.collision;

import org.junit.Assert;

import org.junit.Test;

import tools.EngineUT;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

public class TestThrow extends EngineUT {

	@Test
	public void peebles() {
		mapUtils.loadMap("cavef2");
		PersoPlayer zildo = spawnZildo(232, 111);
		waitEndOfScripting();
		zildo.setAngle(Angle.OUEST);
		zildo.setWeapon(new Item(ItemKind.ROCK_BAG));
		// Throw a peeble on the west
		zildo.attack();
		// Find the sprite
		Element peeble = null;
		for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (entity.getDesc() == ElementDescription.PEEBLE) {
				peeble = (Element) entity;
				break;
			}
		}
		Assert.assertNotNull(peeble);
		// Wait for peeble to disappear
		while (!peeble.dying) {
			renderFrames(1);
			System.out.println(peeble.x);
		}
		// Check its location => must be in lava
		Assert.assertTrue("Peeble should have been landed in lava ! But x="+peeble.x, peeble.x < 198);
	}
}
