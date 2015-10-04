package junit.save;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import junit.perso.EngineUT;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import zildo.monde.map.Tile.TileNature;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

public class FreezeTest extends EngineUT {

	PersoPlayer zildo;
	
	int savedFloor;
	
	@Before
	public void commonInit() {
		// Load map and place hero at specific location with right floor
		EngineZildo.mapManagement.loadMap("voleursg5", false);
		zildo = spawnZildo(422, 360);
		zildo.setPv(2);
		zildo.floor = 0;
		savedFloor = 0;
		EngineZildo.mapManagement.setStartLocation(new Point(422, 360), Angle.NORD, zildo.floor);
		
		// Prepare backed up game
		EngineZildo.backUpGame();
		
		// Wait init scripts over
		waitEndOfScripting();
	}
	
	/** Place hero in the cave of flames, on 0th floor, and pushes him to death in lava **/
	@Test
	public void sameFloor() {
		dieInLava();
		dieInLava();
	}
	
	@Test 
	public void differentFloor() {
		zildo.setX(168);
		zildo.setY(215);
		zildo.setFloor(1);
		
		dieInLava();
		dieInLava();
	}
	
	@Test
	public void differentFloorReload() {
		zildo.setX(168);
		zildo.setY(215);
		zildo.setFloor(1);
		
		EngineZildo.restoreBackedUpGame();
		
		Assert.assertEquals(savedFloor, zildo.getFloor());
	}
	
	@Test
	public void repeat() {
		for (int i=0;i<10;i++) {
			System.out.println("Test: pass "+i+"/10");
			zildo.setPv(2);
			dieInLava();
			dieInLava();
			// Stuck because of DialogDisplay, part of GUIDisplay, that we mock ! How reach it from here ?
			// Check that gameover dialog is over
			boolean toggle = true;
			while (clientState.dialogState.dialoguing) { 
				// Simulate press/release action key to pass dialog
				instant.setKey(KeysConfiguration.PLAYERKEY_ACTION, toggle);
				renderFrames(1);
				toggle = !toggle;
			}
			Assert.assertFalse(clientState.dialogState.dialoguing);
		}
	}
	
	private void dieInLava() {
		int initialPv = zildo.getPv();
		simulateDirection(new Vector2f(0, -1f));
		
		boolean scripting = false;
		for (int i=0;i<100;i++) {
			renderFrames(1);
			scripting = EngineZildo.scriptManagement.isScripting();
			if (scripting) {	// Break as soon as a script starts
				break;
			}
		}
		// Check that player has died in lava
		Assert.assertTrue(scripting);
		Assert.assertEquals(TileNature.BOTTOMLESS, zildo.getCurrentTileNature());	// Lava tile
		verify(zildo, never()).die();	// 'Die' method hasn't been called
		
		waitEndOfScripting(new ScriptAction(null) {
			// When hero dies, players has to click on dialog to continue
			@Override
			public void launchAction(ClientState p_clientState) {
				EngineZildo.scriptManagement.userEndAction();
			}
		});
		
		// Check that hero has been respawned
		verify(EngineZildo.mapManagement, times(1)).respawn(true, 1);
		Assert.assertEquals(initialPv - 1, zildo.getPv());
		Assert.assertEquals(savedFloor, zildo.floor);
		// Check that appropriate 'die' method has been called if hero is dead
		if (zildo.getPv() == 0) {
			verify(zildo, times(1)).die(true, null);
		}
		
		renderFrames(20);
		
		reset(zildo);
		reset(EngineZildo.mapManagement);
	}
}
