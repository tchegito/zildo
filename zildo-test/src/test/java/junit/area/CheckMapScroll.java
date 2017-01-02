package junit.area;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.InfoPersos;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

public class CheckMapScroll extends EngineUT {

	/** Check that hero faces the right direction after a scroll **/
	@Test @InfoPersos
	public void heroFaceRight() {
		mapUtils.loadMap("chateausud");
		PersoPlayer zildo = spawnZildo(6, 338);
		waitEndOfScripting();
		
		// Check that no script is rendering at this time
		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());

		// 1) check on WEST
		// Send hero on the border and wait for him to reach it
		simulateDirection(-1, 0);
		renderFrames(20);
		Assert.assertNotNull("Hero should have been changing map !", EngineZildo.mapManagement.getMapScrollAngle());
		waitEndOfScripting();
		simulateDirection(0, 0);	// Stop him
		// Now we should be on the next map
		Assert.assertEquals("eleoforet1", EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());
		while (zildo.isGhost()) {
			renderFrames(1);
		}
		// Check that hero stands still and faces right direction
		Assert.assertEquals(0,  (int) zildo.deltaMoveX);
		renderFrames(10);
		Assert.assertEquals(Angle.OUEST, zildo.getAngle());
		
		// 2) Same with opposite direction (EAST)
		simulateDirection(1, 0);
		renderFrames(20);
		Assert.assertNotNull("Hero should have been changing map !", EngineZildo.mapManagement.getMapScrollAngle());
		waitEndOfScripting();
		simulateDirection(0, 0);	// Stop him
		Assert.assertEquals("chateausud", EngineZildo.mapManagement.getCurrentMap().getName());
		while (zildo.isGhost()) {
			renderFrames(1);
		}
		Assert.assertEquals(0,  (int) zildo.deltaMoveX);
		renderFrames(10);
		Assert.assertEquals(Angle.EST, zildo.getAngle());
	}
}
