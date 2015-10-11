package junit.save;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.nio.ByteBuffer;

import junit.perso.EngineUT;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import zildo.fwk.file.EasyBuffering;
import zildo.monde.Game;
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
		
		zildo = EngineZildo.persoManagement.getZildo();
		Assert.assertEquals(savedFloor, zildo.getFloor());
	}
	
	@Test
	public void wrongFloorWithSavegame() {
		byte[] savegame = {0, 44, 8, 102, 108, 117, 116, 95, 97, 115, 107, 1, 4, 102, 108, 117, 116, 1, 11, 99, 104, 
				97, 116, 101, 97, 117, 95, 97, 115, 107, 1, 14, 101, 110, 108, 101, 118, 101, 98, 117, 105, 115, 115, 
				111, 110, 115, 1, 18, 101, 110, 108, 101, 118, 101, 98, 117, 105, 115, 115, 111, 110, 115, 95, 119, 105, 
				110, 1, 16, 102, 101, 114, 109, 105, 101, 114, 112, 101, 114, 100, 117, 95, 97, 115, 107, 1, 12, 102, 101, 
				114, 109, 105, 101, 114, 112, 101, 114, 100, 117, 1, 12, 103, 97, 114, 100, 101, 108, 97, 105, 116, 105, 
				101, 114, 1, 16, 103, 97, 114, 100, 101, 108, 97, 105, 116, 105, 101, 114, 95, 119, 105, 110, 1, 9, 114, 
				105, 116, 111, 117, 95, 98, 97, 114, 1, 10, 101, 110, 108, 101, 118, 101, 109, 101, 110, 116, 1, 8, 104, 
				101, 99, 116, 111, 114, 95, 49, 1, 11, 115, 116, 97, 114, 116, 95, 100, 101, 102, 105, 49, 1, 10, 115, 116, 
				111, 112, 95, 100, 101, 102, 105, 49, 1, 12, 115, 116, 97, 114, 116, 95, 118, 105, 115, 105, 116, 49, 1, 19, 
				115, 117, 105, 116, 101, 95, 118, 105, 115, 105, 116, 49, 95, 119, 101, 97, 112, 111, 110, 1, 12, 109, 97, 
				108, 116, 117, 115, 95, 102, 111, 114, 101, 116, 1, 25, 109, 97, 108, 116, 117, 115, 95, 102, 111, 114, 101, 
				116, 95, 122, 105, 108, 100, 111, 95, 100, 101, 102, 101, 97, 116, 1, 18, 102, 111, 114, 101, 116, 103, 95, 
				98, 117, 116, 116, 111, 110, 95, 116, 114, 105, 103, 1, 19, 102, 111, 114, 101, 116, 103, 95, 97, 112, 114, 
				101, 115, 95, 103, 114, 111, 116, 116, 101, 1, 18, 98, 111, 115, 113, 117, 101, 116, 95, 107, 105, 108, 108, 
				95, 103, 97, 114, 100, 115, 1, 16, 98, 111, 115, 113, 117, 101, 116, 95, 102, 114, 101, 101, 95, 119, 97, 
				121, 1, 23, 122, 105, 108, 100, 111, 95, 112, 111, 108, 97, 107, 121, 95, 107, 105, 108, 108, 103, 117, 97, 
				114, 100, 115, 1, 12, 122, 105, 108, 100, 111, 95, 112, 111, 108, 97, 107, 121, 1, 10, 102, 111, 114, 101, 
				116, 95, 98, 97, 99, 107, 1, 12, 115, 116, 97, 114, 116, 95, 118, 105, 115, 105, 116, 50, 1, 12, 116, 114, 
				105, 103, 95, 101, 99, 104, 97, 110, 103, 101, 1, 15, 116, 111, 110, 110, 101, 97, 117, 95, 112, 111, 108, 
				97, 107, 121, 103, 1, 11, 102, 117, 105, 116, 101, 95, 116, 111, 110, 121, 49, 1, 9, 118, 101, 114, 116, 95, 
				115, 101, 101, 110, 1, 15, 97, 116, 116, 97, 113, 117, 101, 95, 118, 111, 108, 101, 117, 114, 115, 1, 14, 98, 
				101, 97, 110, 67, 97, 118, 101, 70, 108, 97, 109, 101, 115, 1, 17, 109, 101, 97, 110, 119, 104, 105, 108, 101, 
				95, 118, 111, 108, 101, 117, 114, 115, 1, 14, 102, 101, 114, 109, 101, 109, 50, 40, 55, 44, 32, 49, 53, 41, 1, 
				17, 112, 114, 105, 115, 111, 110, 101, 120, 116, 40, 52, 48, 44, 32, 52, 48, 41, 1, 11, 116, 114, 105, 112, 95, 
				112, 111, 108, 97, 107, 121, 1, 13, 112, 111, 108, 97, 107, 121, 51, 40, 50, 44, 32, 55, 41, 1, 16, 98, 111, 
				115, 113, 117, 101, 116, 98, 111, 115, 113, 117, 101, 116, 109, 50, 1, 13, 98, 111, 115, 113, 117, 101, 116, 
				109, 55, 55, 75, 69, 89, 1, 18, 98, 111, 115, 113, 117, 101, 116, 109, 98, 111, 115, 113, 117, 101, 116, 109, 
				50, 50, 1, 15, 112, 111, 108, 97, 107, 121, 52, 40, 51, 49, 44, 32, 49, 57, 41, 1, 15, 112, 111, 108, 97, 107, 
				121, 52, 40, 50, 55, 44, 32, 49, 57, 41, 1, 17, 112, 111, 108, 97, 107, 121, 103, 51, 112, 111, 108, 97, 107, 
				121, 103, 52, 50, 1, 16, 118, 111, 108, 101, 117, 114, 115, 103, 49, 40, 49, 56, 44, 32, 52, 41, 1, 5, 1, 6, 0, 
				0, 0, 0, 0, 0, 0, 7, 79, 108, 105, 118, 105, 101, 114, 2, 0, 3, 4, 70, 76, 85, 84, 0, 0, 8, 78, 69, 67, 75, 76, 
				65, 67, 69, 0, 0, 8, 82, 79, 67, 75, 95, 66, 65, 71, 0, 0, 9, 118, 111, 108, 101, 117, 114, 115, 103, 53, 2, 28, 
				1, 97, 6, -71, 3, -64, 0, -112, 3, 6, 68, 105, 122, 122, 105, 101, 23, 91, 91, 68, 89, 78, 65, 77, 73, 84, 69, 44, 
				32, 49, 93, 44, 32, 49, 53, 44, 32, 50, 48, 93, 16, 66, 105, 108, 101, 108, 73, 103, 111, 114, 86, 105, 108, 108, 
				97, 103, 101, 74, 91, 91, 69, 77, 80, 84, 89, 95, 66, 65, 71, 44, 32, 49, 93, 44, 32, 49, 48, 48, 44, 32, 50, 93, 
				44, 32, 91, 91, 66, 76, 85, 69, 68, 82, 79, 80, 44, 32, 49, 93, 44, 32, 49, 53, 44, 32, 45, 49, 93, 44, 32, 91, 91, 
				68, 89, 78, 65, 77, 73, 84, 69, 44, 32, 49, 93, 44, 32, 49, 48, 48, 44, 32, 50, 93, 11, 109, 111, 110, 101, 121, 83, 
				116, 111, 108, 101, 110, 4, 49, 54, 46, 48, 0};
		
		Game game = Game.deserialize(new EasyBuffering(ByteBuffer.wrap(savegame)), false);
		EngineZildo.setGame(game);

		// Set hero at the lowest floor in the map
		zildo.setX(168);
		zildo.setY(215);
		zildo.setFloor(0);
		
		// Restore him at the beginning of the room
		EngineZildo.mapManagement.respawn(true, 0);
		
		zildo = EngineZildo.persoManagement.getZildo();
		Assert.assertEquals(1, zildo.getFloor());
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
			zildo = EngineZildo.persoManagement.getZildo();
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
