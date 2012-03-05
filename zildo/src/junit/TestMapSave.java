package junit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import zildo.fwk.awt.ZildoCanvas;
import zildo.monde.Game;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.sprites.Reverse;
import zildo.server.EngineZildo;
import zildo.server.Server;

public class TestMapSave {

	Server server;
	
	@Before
	public void setUp() throws Exception {
		Game game = new Game(false);
		game.editing = true;	// To avoid the trigger mechanisms
		server = new Server(game, false);
	}
	
	@Test
	public void modifyReverseTileAttribute() {
		EngineZildo.mapManagement.loadMap("coucou", false);
		Area area = EngineZildo.mapManagement.getCurrentMap();
		Case cas = area.get_mapcase(15, 10);	// Arbitrary tile
		Tile back = cas.getBackTile();
		back.reverse = Reverse.HORIZONTAL;

		cas = area.get_mapcase(16, 10);	// Arbitrary tile
		back = cas.getBackTile();
		back.reverse = Reverse.VERTICAL;
		
		EngineZildo.mapManagement.saveMapFile("test.map");
		// reload the map
		EngineZildo.mapManagement.loadMap("test", false);
	}

}
