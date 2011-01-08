/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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


package zildo;

import java.util.HashSet;
import java.util.Set;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.Client.ClientType;
import zildo.client.gui.menu.PlayerNameMenu;
import zildo.fwk.input.KeyboardInstant;
import zildo.monde.Game;
import zildo.monde.map.Area;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;
import zildo.server.Server;
import zildo.server.state.ClientState;

/**
 * Represents the game owner core class. It could be :<ul>
 * <li>a single player, with no network support</li>
 * <li>a first player, who can accept client in his game</li>
 * </ul>
 * Life cycle of this class is:<ul>
 * <li>Zildo's life in single player</li>
 * <li>server's presence on multi player</li>
 * </ul>
 * @author tchegito
 */
public class SinglePlayer {

    EngineZildo engineZildo;
    ClientEngineZildo clientEngineZildo;
    Server server;
    static ClientState state;	// The single-player client / or multi-player server's client
    
    /**
     * Multiplayer, where current player is the server.
     * @param p_server
     */
    public SinglePlayer(Server p_server) {
        server = p_server;
        engineZildo = p_server.getEngineZildo();
    }

    /**
     * The real single player. Everything start from here.
     * @param p_game
     */
    public SinglePlayer(Game p_game) {
    	p_game.multiPlayer=false;
        engineZildo = new EngineZildo(p_game);

        if (p_game.brandNew) {
	        // Start 'intro' script
	       	EngineZildo.scriptManagement.execute("intro");
	       	p_game.brandNew=false;
        }
    }

    /**
     * Launch the game. Several cases:
     * -the current player is both server and client
     * -the same, but in fake mode : we emulate a network traffic
     */
    public void launchGame() {
        Client client = ClientEngineZildo.getClientForGame();
        client.setUpNetwork(ClientType.SERVER_AND_CLIENT, null, 0, server!=null);
        ClientEngineZildo clientEngineZildo = client.getEngineZildo();

        // Initialize map
        Area map=EngineZildo.mapManagement.getCurrentMap();
        if (map != null) {
        	ClientEngineZildo.mapDisplay.setCurrentMap(map);
        }

        boolean done = false;
        Set<ClientState> states=new HashSet<ClientState>();

        // Create Zildo !
        int zildoId;
        
        if (server != null) {
        	zildoId=server.connectClient(null, PlayerNameMenu.loadPlayerName());
        	state=server.getClientStates().iterator().next();
    		ClientEngineZildo.guiDisplay.displayMessage("server started");
        } else {
            	PersoZildo zildo = EngineZildo.persoManagement.getZildo();
            	if (zildo == null) {
            	    zildoId = EngineZildo.spawnClient();
            	} else {
            	    zildoId = zildo.getId();
            	}
        	state = new ClientState(null, zildoId);
        }
        ClientEngineZildo.spriteDisplay.setZildoId(zildoId);

        // Focus on zildo
        SpriteEntity zildo=EngineZildo.persoManagement.getZildo();
        ClientEngineZildo.mapDisplay.setFocusedEntity(zildo);
        
        KeyboardInstant instant=new KeyboardInstant();
        
        while (!done && !state.gameOver) {
        	states.clear();

            // Server's network job
            if (server != null) {
                server.networkJob();
                
                states.addAll(server.getClientStates());
            }
            
            // Render events (1: server and 2: client)
            state.event = engineZildo.renderEvent(state.event);
            state.event = clientEngineZildo.renderEvent(state.event);
	            
        	EngineZildo.dialogManagement.resetQueue();
            //if (state.event.nature == ClientEventNature.NOEVENT) {
	            // Reset queues
	        	EngineZildo.soundManagement.resetQueue();
            //}
            // Read keyboard
            instant.update();
            state.keys = instant;
            client.setKbInstant(instant);

            states.add(state);

            // Update server
            engineZildo.renderFrame(states);

            // Dialogs
            if (ClientEngineZildo.dialogDisplay.launchDialog(EngineZildo.dialogManagement.getQueue())) {
            	EngineZildo.dialogManagement.stopDialog(state);
            }

            // Update client
            ClientEngineZildo.spriteDisplay.setEntities(EngineZildo.spriteManagement.getSpriteEntities(state));

            // Render client
            done = client.render();

            // Render sounds
            ClientEngineZildo.soundPlay.playSounds(EngineZildo.soundManagement.getQueue());
        }
        
        if (server != null) {
        	// Tells all clients that we're leaving
        	server.disconnectServer();
        }

        engineZildo.cleanUp();
    }
    
    public static ClientState getClientState() {
    	return state;
    }
}