package junit.area;

import junit.framework.Assert;
import junit.perso.EngineUT;

import org.junit.Test;

import zildo.Zildo;
import zildo.fwk.input.CommonKeyboardHandler;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.input.KeyboardInstant;
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
	KeyboardInstant instant = new KeyboardInstant();
	
	Vector2f CONSTANT_DIRECTION = new Vector2f(0, -Constantes.ZILDO_SPEED);
	
	private void init(int x, int y) {
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
		
		KeyboardHandler fakedKbHandler = org.mockito.Mockito.mock(CommonKeyboardHandler.class);
		Zildo.pdPlugin.kbHandler = fakedKbHandler;
		when(fakedKbHandler.getDirection()).thenReturn(CONSTANT_DIRECTION);
	}
	
	@Test
	public void testStraightMovement() {
		init(152, 101);
		
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
}
