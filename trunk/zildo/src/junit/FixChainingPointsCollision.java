package junit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.LogManager;

import org.junit.Test;

import zildo.fwk.bank.MotifBank;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.Game;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.ChainingPoint;
import zildo.monde.util.Point;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.Server;

/**
 * Fix every door collision.
 * @author Tchegito
 *
 */
public class FixChainingPointsCollision {

	Point[] mustBeMasked = new Point[] { new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(2, 0),
			new Point(0, 1), new Point(1, 1),
			new Point(0, 2), new Point(1, 2) };

	@Test
	public void checkAll() {
		String path = Constantes.DATA_PATH + Constantes.MAP_PATH;
		File directory = new File(path);

		File[] maps = directory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".map") && !name.equals("ancien.map");
			}
		});
		LogManager.getLogManager().reset();

		Game game = new Game(null, true);
		new Server(game, true);
		for (String bankName : TileEngine.tileBankNames) {
			MotifBank motifBank = new MotifBank();
			motifBank.charge_motifs(bankName.toUpperCase());
		}

		boolean mapOk = true;
		for (File f : maps) {
			String name = f.getName();
			try {
				EngineZildo.mapManagement.loadMap(name, false);
			} catch (RuntimeException e) {
				// Nothing to do : this map doesn't work now
				continue;
			}
			MapManagement mapManagement = EngineZildo.mapManagement;
			Area area = mapManagement.getCurrentMap();

			// Chaining points
			for (ChainingPoint ch : area.getListPointsEnchainement()) {
				if (ch.isBorder() || ch.isVertical()) {
					continue;
				}
				Case c = area.get_mapcase(ch.getPx(), ch.getPy()+4 - 1);
				if (c.getForeTile() != null) {
					int a = c.getForeTile().index;
					//System.out.println("Found "+a+" at " + ch.getPx() + "," + ch.getPy());
					if (a == 88 || a == 72) {
						// Found one !
						System.out.println(name+": found door at " + ch.getPx() + "," + ch.getPy());
						c.getBackTile().index = a;
						Case c2 = area.get_mapcase(ch.getPx() + 1, ch.getPy()+4 - 1);
						c2.getBackTile().index = c2.getForeTile().index;
					}
				}
			}
			if (mapOk) {
				System.out.println("==> Map ok");
			}

			// Save the map into a temporary file
			mapManagement.saveMapFile(name);
		}
	}
}
