package junit.area;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.ZildoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

public class CheckMapInteractions extends EngineUT {

	PersoPlayer hero;
	
	@Test
	public void forkBushes() {
		mapUtils.loadMap("ferme");
		hero = spawnZildo(515, 408);
		haveFork();
		waitEndOfScripting();
		
		// Hero fork a bush
		hero.setAngle(Angle.OUEST);
		Assert.assertTrue(hero.canFork());
		hero.attack();	// Fork
		renderFrames(16);
		Assert.assertFalse(hero.canFork());
		simulateDirection(-1, 0);
		renderFrames(16);
		simulateDirection(0, 0);
		Assert.assertNull(findEntityByDesc(ElementDescription.LEAF_GREEN));
		hero.setAngle(Angle.NORD);	// Turn around to put leaves away on an empty tile
		renderFrames(1);
		hero.attack();	// Put away fork
		renderFrames(16);
		
		// Check if green leaves are scattered
		Assert.assertNotNull(findEntityByDesc(ElementDescription.LEAF_GREEN));
	}
	
	@Test
	public void fork() {
		mapUtils.loadMap("ferme");
		hero = spawnZildo(206, 149);
		haveFork();
		waitEndOfScripting();
		
		Assert.assertEquals(1, findEntitiesByDesc(ZildoDescription.FORK0).size());
		
		// Fork once
		hero.setAngle(Angle.OUEST);
		Assert.assertTrue(hero.canFork());
		hero.attack();	// Fork
		Assert.assertEquals(MouvementZildo.ATTACK_FORK, hero.getMouvement());
		renderFrames(16);
		Assert.assertEquals(MouvementZildo.HOLD_FORK, hero.getMouvement());
		Assert.assertNotNull(findEntityByDesc(ZildoDescription.FORK0));
		hero.attack();	// Put away fork
		Assert.assertEquals(MouvementZildo.PUTAWAY_FORK, hero.getMouvement());
		renderFrames(16);

		// Fork a second time
		Assert.assertEquals(1, findEntitiesByDesc(ZildoDescription.FORK0).size());
		hero.attack();
		renderFrames(16);
		hero.attack();
		renderFrames(16);
		Assert.assertEquals(1, findEntitiesByDesc(ZildoDescription.FORK0).size());

	}
	
	private void haveFork() {
		Item spade = new Item(ItemKind.SPADE);
		hero.getInventory().add(spade);
		hero.setWeapon(spade);
	}
	
	@Test
	public void hitCactus() {
		mapUtils.loadMap("canyon");
		hero = spawnZildo(269, 291);
		haveFork();
		hero.setAngle(Angle.OUEST);
		waitEndOfScripting();
		
		Perso cactus = persoUtils.persoByName("first");
		Assert.assertNotNull(cactus);
		Point origin = new Point((int) cactus.x, (int) cactus.y);
		hero.attack();
		renderFrames(16);
		Assert.assertEquals(origin.x, (int) cactus.x);
		Assert.assertEquals(origin.y, (int) cactus.y);
	}
	
	@Test
	public void blockedChainingPoint() {
		mapUtils.loadMap("valori");
		hero = spawnZildo(467, 70);
		waitEndOfScripting();
		
		// Solution to make this UT work => modify TriggerElement#isDone to handle
		// location trigger with specific position (only if map names match) like match() with radius
		
		simulateDirection(new Vector2f(-1f, -0.5f));
		renderFrames(40);
		Assert.assertFalse("Hero shouldn't go to valori's cave before finding 3 keys !", EngineZildo.mapManagement.isChangingMap(hero));
	}
}