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
import zildo.monde.map.Tile;
import zildo.monde.util.Point;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.Server;

public class TestAllChainingPoints {

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
				// Ok we got an horizontal one => find the walkable side
				int y = ch.getPy() + 2;
				int factor = 1;
				if (!mapManagement.collide(ch.getPx() * 16, y * 16, null)) {
					System.out.print("Chaining point haut=>bas");
				} else {
					System.out.print("Chaining point bas=>haut");
					factor = -1;
				}
				System.out.println(" " + ch.getPx() + "," + ch.getPy());

				// Check all expected tiles
				for (Point p : mustBeMasked) {
					Point toCheck = new Point(ch.getPx() + p.x, ch.getPy() + p.y * factor);
					if (!area.isOutside(toCheck.x << 4, toCheck.y << 4)) {
						Case c = area.get_mapcase(toCheck.x, toCheck.y + 4);
						Tile t = c.getForeTile();
						if (t == null) {
							mapOk = false;
							System.out.println("ERROR: tile at " + toCheck + " should be masked !");
						}
					}
				}
			}
			if (mapOk) {
				System.out.println("==> Map ok");
			}

			// Save the map into a temporary file

		}
	}
}
