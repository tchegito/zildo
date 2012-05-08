/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package zildo.client.stage;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.Client.ClientType;
import zildo.fwk.net.ServerInfo;
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
        client.setUpNetwork(ClientType.CLIENT, null, 0, true);
    }
    
    /**
     * Client wants to join www game.
     */
    public MultiPlayer(ServerInfo p_serverInfo) {
        Client client = ClientEngineZildo.getClientForGame();
        client.setUpNetwork(ClientType.CLIENT, p_serverInfo.ip, p_serverInfo.port, true);
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
        //server.cleanUp();
    }

    /**
     * Fake mode : client and server are on the same PC, and we emulate a net traffic.
     * @param p_game
     */
    public static void launchFakeMode(Game p_game, boolean p_fake) {
        Server server = new Server(p_game, true);
        Client client = new Client(false);
        client.setUpNetwork(ClientType.SERVER_AND_CLIENT, null, 0, true);

        server.start();
        client.run();
        server.disconnectClient(client.getNetClient());
    }
    
}
