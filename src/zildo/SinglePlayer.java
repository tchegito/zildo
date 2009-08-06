/**
 *
 */
package zildo;

import java.util.Collections;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.fwk.KeyboardInstant;
import zildo.monde.Game;
import zildo.server.ClientState;
import zildo.server.EngineZildo;

/**
 * @author tchegito
 */
public class SinglePlayer {

    EngineZildo engineZildo;
    ClientEngineZildo clientEngineZildo;

    public SinglePlayer(Game p_game) {
        engineZildo = new EngineZildo(p_game);
        Client client = new Client(false);

        // Create Zildo !
        int zildoId = engineZildo.spawnClient();
        ClientEngineZildo.spriteDisplay.setZildoId(zildoId);

        // Set sprite bank's server with client's one, because client has corrects SpriteModel
        EngineZildo.spriteManagement.setBanqueSpr(ClientEngineZildo.spriteDisplay.getBanqueSpr());
        // Initialize map
        ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());

        boolean done = false;
        while (!done) {
            // Read keyboard
            KeyboardInstant instant = KeyboardInstant.getKeyboardInstant();
            ClientState state = new ClientState(null, zildoId);
            state.keys = instant;

            // Update server
            engineZildo.renderFrame(Collections.singleton(state));

            // Update client
            ClientEngineZildo.spriteDisplay.setEntities(EngineZildo.spriteManagement.getSpriteEntities());

            // Render client
            done = client.render();
            
            // Render sounds
            ClientEngineZildo.soundPlay.playSounds(EngineZildo.soundManagement.getQueue());
            EngineZildo.soundManagement.resetQueue();
        }
        client.cleanUp();
        engineZildo.cleanUp();
    }
}