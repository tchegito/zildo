/**
 *
 */
package zildo;

import java.util.HashSet;
import java.util.Set;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.fwk.input.KeyboardInstant;
import zildo.monde.Game;
import zildo.server.ClientState;
import zildo.server.EngineZildo;
import zildo.server.Server;

/**
 * @author eboussaton
 */
public class SinglePlayer {

    EngineZildo engineZildo;
    ClientEngineZildo clientEngineZildo;
    Server server;

    public SinglePlayer(Server p_server) {
        server = p_server;
        engineZildo = p_server.getEngineZildo();
    }

    public SinglePlayer(Game p_game) {
        engineZildo = new EngineZildo(p_game);

        launchGame();
    }

    /**
     * Launch the game. Several cases:
     * -the current player is both server and client
     * -the same, but in fake mode : we emulate a network traffic
     */
    public void launchGame() {
        Client client = new Client(false);
        ClientEngineZildo clientEngineZildo = client.getEngineZildo();

        // Create Zildo !
        int zildoId = engineZildo.spawnClient();
        ClientEngineZildo.spriteDisplay.setZildoId(zildoId);

        // Initialize map
        ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());

        boolean done = false;
        Set<ClientState> states=new HashSet<ClientState>();
        ClientState state = new ClientState(null, zildoId);
        while (!done) {
        	states.clear();

            // Server's network job
            if (server != null) {
                server.networkJob();
                
                states.addAll(server.getClientStates());
            }
            
            // Reset queues
        	EngineZildo.soundManagement.resetQueue();
        	EngineZildo.dialogManagement.resetQueue();

            // Read keyboard
            KeyboardInstant instant = KeyboardInstant.getKeyboardInstant();
            state.keys = instant;
            states.add(state);

            // Update server
            engineZildo.renderFrame(states);

            // Update client
            ClientEngineZildo.spriteDisplay.setEntities(EngineZildo.spriteManagement.getSpriteEntities());

            // Dialogs
            ClientEngineZildo.dialogDisplay.launchDialog(EngineZildo.dialogManagement.getQueue());
            
            // Render client
            done = client.render();
            
            // Render sounds
            ClientEngineZildo.soundPlay.playSounds(EngineZildo.soundManagement.getQueue());
        }
        clientEngineZildo.cleanUp();
        client.cleanUp();
        engineZildo.cleanUp();
    }
}