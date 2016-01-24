package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.sprites.persos.ControllablePerso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Vector2f;

public class TestSquirrelMove extends EngineUT {

	PersoPlayer squirrel;
	
	private void init(String mapName, int x, int y) {
		mapUtils.loadMap(mapName);
		squirrel = spawnZildo(x, y);
		squirrel.setAppearance(ControllablePerso.PRINCESS_BUNNY);
		waitEndOfScripting();
	}
	
	// Squirrel should be able to jump on a stump from grass
	@Test
	public void jumpOnStump() {
		init("sousbois6", 336, 436);
		
		// Walk to the stump
		simulateDirection(new Vector2f(0, 1f));
		renderFrames(20);
		
		// Check that squirrel doesn't walk on stump without jumping
		Assert.assertEquals(0, (int) squirrel.z);
		
		squirrel.jump();
		renderFrames(20);
		Assert.assertEquals(5, (int) squirrel.z);
	}
	
	// Squirrel shouldn't from mud (because its jumps is lower)
	@Test
	public void jumpOnStumpUnderMud() {
		init("sousbois6", 207, 401);
		
		// Walk to the stump
		simulateDirection(new Vector2f(0, 1f));
		renderFrames(10);
		
		// Check that squirrel doesn't walk on stump without jumping
		Assert.assertEquals(0, (int) squirrel.z);
		
		squirrel.jump();
		renderFrames(20);
		Assert.assertEquals(0, (int) squirrel.z);
	}
	
	@Test
	public void jumpFromStumpUnderMud() {
		init("sousbois6", 207, 428);
		squirrel.z = 5;
		
		squirrel.jump();
		int maxZ = 0;
		for (int i=0;i<100;i++) {
			renderFrames(1);
			maxZ = Math.max((int) squirrel.z, maxZ);
		}
		Assert.assertTrue("Squirrel should have reach z=10, but measured max was "+maxZ, maxZ > 10);
		
	}
	@Test
	public void fallFromStumpOnPlot() {
		// Place squirrel on a high stump, just on the right of a plot
		init("sousbois3", 222, 506);
		squirrel.z = 8;
		
		// Walks to fall on the plot
		simulateDirection(new Vector2f(-1, 0));
		
		renderFrames(30);
		Assert.assertEquals(MouvementZildo.TOMBE, squirrel.getMouvement());
		simulateDirection(null);
		renderFrames(30);
		Assert.assertEquals(MouvementZildo.VIDE, squirrel.getMouvement());

		Assert.assertEquals(0, (int) squirrel.z);
	}
}
