package junit.perso;

import org.junit.Assert;

import org.junit.Test;

import zildo.monde.sprites.persos.ControllablePerso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Vector2f;

public class TestSquirrelMove extends EngineUT {

	PersoPlayer squirrel;
	
	private void init(int x, int y) {
		mapUtils.loadMap("sousbois6");
		squirrel = spawnZildo(x, y);
		squirrel.setAppearance(ControllablePerso.PRINCESS_BUNNY);
		waitEndOfScripting();
	}
	
	// Squirrel should be able to jump on a stump from grass
	@Test
	public void jumpOnStump() {
		init(336, 436);
		
		// Walk to the stump
		simulateDirection(new Vector2f(0, 1f));
		renderFrames(20);
		
		// Check that squirrel doesn't walk on stump without jumping
		Assert.assertEquals(0, squirrel.z,  0.1f);
		
		squirrel.jump();
		renderFrames(20);
		Assert.assertEquals(5, squirrel.z,  0.1f);
	}
	
	// Squirrel shouldn't from mud (because its jumps is lower)
	@Test
	public void jumpOnStumpUnderMud() {
		init(207, 401);
		
		// Walk to the stump
		simulateDirection(new Vector2f(0, 1f));
		renderFrames(10);
		
		// Check that squirrel doesn't walk on stump without jumping
		Assert.assertEquals(0, squirrel.z,  0.1f);
		
		squirrel.jump();
		renderFrames(20);
		Assert.assertEquals(0, squirrel.z,  0.1f);
	}
}
