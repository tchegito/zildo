package junit.collision;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

public class TestThrow extends EngineUT {

	@Test
	public void peebles() {
		mapUtils.loadMap("cavef2");
		PersoPlayer zildo = spawnZildo(232, 111);
		waitEndOfScriptingPassingDialog();
		zildo.setAngle(Angle.OUEST);
		zildo.setWeapon(new Item(ItemKind.ROCK_BAG));
		// Throw a peeble on the west
		zildo.attack();
		// Find the sprite
		Element peeble = findEntityByDesc(ElementDescription.PEEBLE);
		Assert.assertNotNull(peeble);
		// Wait for peeble to disappear
		while (!peeble.dying) {
			renderFrames(1);
			System.out.println(peeble.x);
		}
		// Check its location => must be in lava
		Assert.assertTrue("Peeble should have been landed in lava ! But x="+peeble.x, peeble.x < 198);
	}
	
	@Test
	public void removeObsoleteProjectile() {
		EngineZildo.scriptManagement.accomplishQuest("fromPrison13", false);
		// Start in prison12 (map with 2 bow-hanging gards)
		mapUtils.loadMap("prison12");
		spawnZildo(22, 133);
		waitEndOfScripting();
		simulateDirection(-1, 0);
		renderFrames(40);
		waitEndOfScroll();
		// Check that hero has changed map
		mapUtils.assertCurrent("prison13");
		waitEndOfScripting();
		simulateDirection(1, 0);
		renderFrames(80);
		waitEndOfScripting();
		// Check that we go back
		mapUtils.assertCurrent("prison12");
		simulateDirection(0, 0);
		Perso garde = EngineZildo.persoManagement.getNamedPerso("noir1");
		waitEndOfScroll();
		garde.setAngle(Angle.OUEST);
		garde.attack();
		renderFrames(10);
		simulateDirection(new Vector2f(-0.5f, 0f));
		renderFrames(30 + 20);
		Element arrow = findEntityByDesc(ElementDescription.ARROW_LEFT);
		Assert.assertNotNull(arrow);
		simulateDirection(-1,0);
		renderFrames(80);
		waitEndOfScripting();
		mapUtils.assertCurrent("prison13");
	}
	
	private Element findEntityByDesc(ElementDescription desc) {
		for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (entity.getDesc() == desc) {
				return (Element) entity;
			}
		}
		return null;
	}

}
