/**
 *
 */
package zildo;

import zildo.monde.Area;
import zildo.monde.Game;
import zildo.monde.serveur.MapManagement;
import zildo.network.Server;

/**
 * @author tchegito
 *
 */
public class TestSaveMap {

    public void testeSaveMap() {

        Game game = new Game("polaky", false);
        Server server = new Server(game);

        MapManagement mapManagement = server.getEngineZildo().mapManagement;

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
