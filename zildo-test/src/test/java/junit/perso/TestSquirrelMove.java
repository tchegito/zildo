package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.sprites.persos.ControllablePerso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Vector2f;
import zildo.resource.Constantes;
import zildo.resource.KeysConfiguration;

public class TestSquirrelMove extends EngineUT {

	PersoPlayer squirrel;
	
	private void init(String mapName, int x, int y) {
		mapUtils.loadMap(mapName);
		squirrel = spawnZildo(x, y);
		Assert.assertEquals(Constantes.ZILDO_SPEED, squirrel.getSpeed(), 0f);
		squirrel.setAppearance(ControllablePerso.PRINCESS_BUNNY);
		Assert.assertEquals(Constantes.ROXY_SPEED, squirrel.getSpeed(), 0f);
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
	
	/** Check that squirrel can jump on get back on the same floor (B17 from Ruben's list) **/
	@Test
	public void jumpOnBridge() {
		init("sousbois7", 751, 266);
		squirrel.floor = 1;
		
		squirrel.jump();
		renderFrames(1);
		while (squirrel.z > 0) {
			renderFrames(1);
		}
		Assert.assertEquals(1,  squirrel.floor);
	}

	/** Player could be "jumped" because of a ledge, and would still be able to jump on its own. No way ! Ruben's list B26 **/
	@Test
	public void doubleJump() {
		init("sousbois7", 682, 110);
		simulateDirection(0, 1);
		renderFrames(50);
		// Make sure squirrel is jumping
		Assert.assertEquals(MouvementZildo.SAUTE, squirrel.getMouvement());
		simulateDirection(0, 0);
		simulateKeyPressed(KeysConfiguration.PLAYERKEY_ATTACK.code);
		renderFrames(5);
		Assert.assertEquals(MouvementZildo.SAUTE, squirrel.getMouvement());
	}
}
