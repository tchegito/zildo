package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

/** Unit Test class for release 2.19 because these behavior have been viewed as regressions, and enhancements ('notBlocked'). **/
public class TestFollowPerso extends EngineUT {

	@Test
	public void notBlocked() {
		// When Igor follows hero, make sure he doesn't stay stuck on a wall if he can move
		EngineZildo.scriptManagement.accomplishQuest("zildoAccessIgor", false);
		mapUtils.loadMap("prison7");
		PersoPlayer zildo = spawnZildo(259, 159);
		waitEndOfScripting();
		
		Perso igor = EngineZildo.persoManagement.getNamedPerso("igor");
		Assert.assertNotNull(igor);
		simulateDirection(new Vector2f(-1, 1f));
		// Wait until hero is blocked
		boolean zildoMoving = true;
		while (zildoMoving) {
			renderFrames(5);
			zildoMoving = zildo.deltaMoveX != 0 || zildo.deltaMoveY != 0;
		}
		// Check Igor situation
		renderFrames(20);
		boolean igorMoving = igor.deltaMoveX != 0 || igor.deltaMoveY != 0;
		Assert.assertTrue("Hero is arrived, we expect Igor to walk to him, but he's stuck.", igorMoving);
	}
	
	@Test
	public void walkStairs() {
		// Make sure Igor appears on map where hero comes from a stair
		EngineZildo.scriptManagement.accomplishQuest("zildoAccessIgor", false);
		mapUtils.loadMap("prison6");
		PersoPlayer zildo = spawnZildo(255, 360);
		waitEndOfScripting();

		simulateDirection(new Vector2f(0, -1f));
		
		renderFrames(50);
		waitEndOfScripting();
		
		// Ensure that hero has changed room
		Assert.assertEquals("prison7", EngineZildo.mapManagement.getCurrentMap().getName());
		simulateDirection(new Vector2f(0, 0));
		// Ensure Igor is here, following
		Perso igor = EngineZildo.persoManagement.getNamedPerso("igor");
		Assert.assertNotNull(igor);
		Assert.assertTrue(igor.isGhost());
		Assert.assertEquals(MouvementPerso.FOLLOW, igor.getQuel_deplacement());
		renderFrames(50);
		// Ensure he's visible
		Assert.assertTrue(igor.isVisible());
		Assert.assertTrue(Point.distance(zildo.x, zildo.y, igor.x, igor.y) < 1);
	}
}
