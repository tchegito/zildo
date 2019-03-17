package junit.collision;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.collision.Collision;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

public class TestCollidePerf extends EngineUT {

	// In this test, we ensure that CollideManagement#tab_colli isn't filled with duplicates
	@Test //@InfoPersos
	public void checkArrayPollution() {
		// Gets in prison15 to have some staffs launched, and neutralize cinematic
		mapUtils.loadMap("prison15");
		EngineZildo.scriptManagement.accomplishQuest("prison15Interrogation", false);
		PersoPlayer zildo = spawnZildo(160, 566);
		int pv = zildo.getPv();
		waitEndOfScripting();
		
		// Trigger the staff to shoot
		int nbSpritesBasic = EngineZildo.spriteManagement.getSpriteEntities(null).size();
		simulateDirection(0,2);
		renderFrames(15);
		System.out.println(zildo.getY());
		int nbSpritesCurrent = EngineZildo.spriteManagement.getSpriteEntities(null).size();
		System.out.println(nbSpritesCurrent);
		// Assert that hero didn't get hurt, and we have more sprites than before
		Assert.assertEquals(pv,  zildo.getPv());
		Assert.assertTrue(nbSpritesCurrent > nbSpritesBasic);
		
		List<Collision> collis = EngineZildo.collideManagement.getTabColli();
		Set<Integer> idsCollision = new HashSet<>();
		boolean doublon = false;
		for (Collision coll : collis) {
			int hash = coll.hashCode();
			System.out.println(coll+" - "+hash);
			if (idsCollision.contains(hash)) {
				doublon = true;
			}
			idsCollision.add(hash);
		}
		Assert.assertFalse(doublon);
	}
}