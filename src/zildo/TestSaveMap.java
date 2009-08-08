/**
 *
 */
package zildo;

import zildo.monde.Game;
import zildo.monde.map.Area;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.Server;

/**
 * @author tchegito
 *
 */
public class TestSaveMap {

    public void testeSaveMap() {

        Game game = new Game("polaky", false);
        Server server = new Server(game);

        MapManagement mapManagement = EngineZildo.mapManagement;

        Area map = mapManagement.getCurrentMap();

        String mapname = "polaky";
        // Sauvegarde
        mapManagement.saveMapFile(mapname + "test");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new TestSaveMap().testeSaveMap();
    }

}
