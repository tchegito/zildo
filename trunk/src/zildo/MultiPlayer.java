/**
 *
 */
package zildo;

import zildo.client.Client;
import zildo.monde.Game;
import zildo.server.Server;

/**
 * @author tchegito
 */
public class MultiPlayer {

    public MultiPlayer(Game p_game) {
        Server server = new Server(p_game);
        Client client = new Client();

        server.start();
        client.run();
        server.disconnectClient(client.getNetClient());
    }
}