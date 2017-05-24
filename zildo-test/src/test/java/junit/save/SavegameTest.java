package junit.save;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.DisableFreezeMonitor;
import zildo.fwk.ZUtils;
import zildo.fwk.file.EasyBuffering;
import zildo.monde.Game;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

// Disable freeze monitor, cause we're gonna sleep 4 seconds in each script
@DisableFreezeMonitor
public class SavegameTest extends EngineUT {

	@Test
	public void timeAfterReloadAndSave() {
		// 0) Create an hypothetic zildo
		EngineZildo.spawnClient(ZildoOutfit.Zildo);
		// 1) Create a game, and save
		Game game = new Game("preintro", "unitTest");
		EngineZildo.setGame(game);
		EasyBuffering buffer = new EasyBuffering(5000);
		game.serialize(buffer);
		int savedTime = EngineZildo.game.getTimeSpent();
		// 2) Reload this game
		game = Game.deserialize(buffer, false);
		EngineZildo.setGame(game);
		// 3) Wait some seconds
		ZUtils.sleep(4000);
		// 4) Save again
		game.serialize(buffer);
		// ==> check that time has been well updated
		int timeSpent = EngineZildo.game.getTimeSpent();
		Assert.assertTrue("Time measured at the end ("+timeSpent+") should have been greater than starting time ("+savedTime+")", 
				timeSpent > savedTime);
	}
	
	@Test
	public void timeAfterReloadAndWin() {
		// 0) Create an hypothetic zildo
		EngineZildo.spawnClient(ZildoOutfit.Zildo);
		// 1) Create a game, and save
		Game game = new Game("preintro", "unitTest");
		EngineZildo.setGame(game);
		EasyBuffering buffer = new EasyBuffering(5000);
		game.serialize(buffer);
		int savedTime = EngineZildo.game.getTimeSpent();
		// 2) Reload this game
		game = Game.deserialize(buffer, false);
		EngineZildo.setGame(game);
		// 3) Wait some seconds
		ZUtils.sleep(4000);
		// 4) Save again
		//game.serialize(buffer);
		// ==> check that time has been well updated
		int timeSpent = EngineZildo.game.getTimeSpent();
		Assert.assertTrue("Time measured at the end ("+timeSpent+") should have been greater than starting time ("+savedTime+")", 
				timeSpent > savedTime);
	}
	
	@Test
	public void preserveZ() {
		mapUtils.loadMap("sousbois6");
		PersoPlayer zildo = spawnZildo(244, 171);
		zildo.z = 8;
		// Save
		EasyBuffering buffer = new EasyBuffering(5000);
		EngineZildo.game.serialize(buffer);
		
		// Remove characters (including hero)
		EngineZildo.persoManagement.clearPersos(true);
		
		// Reload
		Game.deserialize(buffer, false);
		Assert.assertEquals(8,  (int) EngineZildo.persoManagement.getZildo().getZ());
	}
}
