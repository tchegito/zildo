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

    /**
     * Client wants to join game.
     */
    public MultiPlayer() {
        Client client = new Client();
        client.run();
    }

    /**
     * Player creates the game, but he is a client too.
     * @param p_game
     */
    public MultiPlayer(Game p_game) {
        Server server = new Server(p_game);

        SinglePlayer singlePlayer = new SinglePlayer(server);
        singlePlayer.launchGame();
        server.cleanUp();
    }

    /**
     * Fake mode : client and server are on the same PC, and we emulate a net traffic.
     * @param p_game
     */
    public MultiPlayer(Game p_game, boolean p_fake) {
        Server server = new Server(p_game);
        Client client = new Client();

        server.start();
        client.run();
        server.disconnectClient(client.getNetClient());
    }

}
