package junit.save;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.DisableFreezeMonitor;
import zildo.fwk.ZUtils;
import zildo.fwk.file.EasyBuffering;
import zildo.monde.Game;
import zildo.monde.map.Area;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Vector2f;
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
	
	// Save game then check if hero'z is well preserved
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
	
	/** Check that regression about bombs3,moon fragment disappeared from chest is fixed **/
	@Test
	public void dynamiteKept() {
		mapUtils.loadMap("chateausud");
		mapUtils.writemap(2,  5,  54,  256*2 + 49 , -1);
		Element dynamites = ElementDescription.BOMBS3.createElement();
		EngineZildo.spriteManagement.spawnSprite(dynamites);
		System.out.println("class="+dynamites.getClass());
		dynamites.setName("retrieve");
		dynamites.setPos(new Vector2f(2*16+8, 5*16+8));

		// save
		EngineZildo.game.editing = true;	// In order to retrieve goodies planned in chests
		EasyBuffering buffer = new EasyBuffering(8000);
		mapUtils.area.serialize(buffer);
		buffer.getAll().flip();
		
		EngineZildo.mapManagement.clearMap();

		Area area = Area.deserialize(buffer, "chateausud", true);
		
		System.out.println(area.readmap(2, 5));
		// reload
		SpriteEntity retrieved = EngineZildo.spriteManagement.getNamedEntity("retrieve");
		Assert.assertEquals(ElementDescription.BOMBS3, retrieved.getDesc());
		Assert.assertTrue(retrieved.isVisible());
		System.out.println(retrieved.x);
		System.out.println(retrieved.y);
		Assert.assertEquals(ElementDescription.BOMBS3, area.getCaseItem(2, 5).desc);
		System.out.println(retrieved.getEntityType()+" "+retrieved.getClass());
	}
}