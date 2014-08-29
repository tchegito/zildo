package junit.area;

import junit.framework.Assert;
import junit.perso.EngineUT;

import org.junit.Test;

import zildo.Zildo;
import zildo.fwk.input.CommonKeyboardHandler;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.input.KeyboardInstant;
import zildo.monde.Trigo;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.util.Vector2f;
import zildo.resource.Constantes;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

import static org.mockito.Mockito.*;

public class CheckDpadDirection extends EngineUT {

	Perso zildo;
	ClientState state;
	KeyboardInstant instant;
	KeyboardHandler fakedKbHandler;
	
	Vector2f CONSTANT_DIRECTION = new Vector2f(0, -Constantes.ZILDO_SPEED);
	
	private void init(int x, int y, Vector2f holdDirection) {
		mapUtils.loadMap("d4m11");
		EngineZildo.persoManagement.clearPersos(true);

		zildo = spawnZildo(x, y);
		state = clients.get(0);
		state.zildoId = zildo.getId();
		state.zildo = (PersoZildo) zildo;
		zildo.walkTile(false);

		
		// Wait end of scripts
		while (EngineZildo.scriptManagement.isScripting()) {
			renderFrames(1);
		}
		
		fakedKbHandler = org.mockito.Mockito.mock(CommonKeyboardHandler.class);
		Zildo.pdPlugin.kbHandler = fakedKbHandler;

		when(fakedKbHandler.getDirection()).thenReturn(holdDirection);
		
		instant = new KeyboardInstant();
	}
	
	@Test
	public void testStraightMovement() {
		init(152, 101, CONSTANT_DIRECTION);

		
		int step = 50;
		while (step-- >= 0) {
			// Make Zildo go forward
			state.keys = instant;
			instant.setKey(KeysConfiguration.PLAYERKEY_UP, true);
			instant.update();

			renderFrames(1);
		}
		
		Assert.assertTrue(zildo.y < 90);
		Assert.assertTrue(zildo.x == 152);
	}
	
	@Test
	public void testRealisticMovement() {
		System.out.println("Realistic");
		// Simulation of a realistic direction => UP with a little value on X axis, as it is with a real touch screen
		init(152, 101, Trigo.vect((Math.PI / 2) - 0.01, -Constantes.ZILDO_SPEED));
		
		int step = 50;
		while (step-- >= 0) {
			// Make Zildo go forward
			state.keys = instant;
			instant.setKey(KeysConfiguration.PLAYERKEY_UP, true);
			instant.update();

			renderFrames(1);
			
			// Hero shouldn't derive more than small decimal on the left on X axis
			Assert.assertTrue(Math.abs(zildo.getDelta().x) < 1);
		}
		
		Assert.assertTrue(zildo.y < 90);
	}
}
