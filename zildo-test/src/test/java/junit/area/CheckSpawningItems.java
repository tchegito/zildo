package junit.area;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.map.Area;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.server.EngineZildo;

public class CheckSpawningItems extends EngineUT {
	
	@Test
	public void simple() {
		mapUtils.loadMap("prisonext");
		Assert.assertNull("Drop floor item shouldn't spawned on the map ! It should pop when player cuts the bushes.", 
				findEntityByDesc(ElementDescription.DROP_FLOOR));

		Assert.assertNull("Sword should be inside chest !", 
				findEntityByDesc(ElementDescription.SWORD));
	}

	@Test
	public void itemOnOpenedChest() {
		EngineZildo.scriptManagement.accomplishQuest("prisonext(40, 40)", false);
		mapUtils.loadMap("prisonext");
		
		Area area = EngineZildo.mapManagement.getCurrentMap();
		
		Assert.assertEquals(238 + 256 * 2, area.readmap(40, 40));
		Assert.assertNull("Sword should be inside chest !", 
				findEntityByDesc(ElementDescription.SWORD));
	}
	
	@Test
	public void spawnLinkableItem() {
		// An item is spawned via scripts, and it should be linked to the chest, and not appear on the map
		EngineZildo.scriptManagement.accomplishQuest("findDragonPortalKey", false);
		EngineZildo.scriptManagement.accomplishQuest("prisonext", false);
		mapUtils.loadMap("prisonext");
		spawnZildo(616, 683);
		waitEndOfScripting();
		
		Assert.assertNull("Purse shouldn't be on the map !", findEntityByDesc(ElementDescription.GOLDPURSE1));

		simulateDirection(0, -1);
		renderFrames(50);
		simulateDirection(0, 0);
		simulatePressButton(Keys.Q, 2);
		
		SpriteEntity purse = findEntityByDesc(ElementDescription.GOLDPURSE1);
		Assert.assertNotNull("Purse should have pop out of the chest !", purse);
	
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestDone("prisonext(38, 41)"));
		
		while (!purse.dying) {	// Wait for the goodies to disappear, otherwise it will stay after map reload, because it's linked to hero
			renderFrames(1);
		}
		
		Area area = EngineZildo.mapManagement.getCurrentMap();
		mapUtils.loadMap("prisonext");
		waitEndOfScripting();
		Assert.assertEquals(48 + 256 * 2, area.readmap(38, 41));
		Assert.assertNull("Purse shouldn't be on the map !", findEntityByDesc(ElementDescription.GOLDPURSE1));
	}
}
