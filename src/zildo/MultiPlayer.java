/**
 *
 */
package zildo;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.Client.ClientType;
import zildo.monde.Game;
import zildo.server.Server;

/**
 * @author tchegito
 */
public class MultiPlayer {

    /**
     * Client wants to join LAN game.
     */
    public MultiPlayer() {
        Client client = ClientEngineZildo.getClientForGame();
        client.setUpNetwork(ClientType.CLIENT, null, 0);
    }
    
    /**
     * Client wants to join www game.
     */
    public MultiPlayer(String p_ip, int p_port) {
        Client client = ClientEngineZildo.getClientForGame();
        client.setUpNetwork(ClientType.CLIENT, p_ip, p_port);
    }

    /**
     * Player creates the game, but he is a client too.
     * @param p_game
     */
    public MultiPlayer(Game p_game, boolean p_lan) {
    	p_game.multiPlayer=true;
        Server server = new Server(p_game, p_lan);

        SinglePlayer singlePlayer = new SinglePlayer(server);
        singlePlayer.launchGame();
        server.cleanUp();
    }

    /**
     * Fake mode : client and server are on the same PC, and we emulate a net traffic.
     * @param p_game
     */
    public static void launchFakeMode(Game p_game, boolean p_fake) {
        Server server = new Server(p_game, true);
        Client client = new Client(false);
        client.setUpNetwork(ClientType.SERVER_AND_CLIENT, null, 0);

        server.start();
        client.run();
        server.disconnectClient(client.getNetClient());
    }
    
}
