/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zildo.server;

import java.util.Collection;

import zildo.client.ClientEngineZildo;
import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.monde.Game;
import zildo.monde.dialog.DialogManagement;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.Point;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.state.ClientState;
import zildo.server.state.ScriptManagement;

public class EngineZildo {

	// Server
	public static SpriteManagement spriteManagement;
	public static MapManagement mapManagement;
	public static CollideManagement collideManagement;
	public static PlayerManagement playerManagement;
	public static PersoManagement persoManagement;
	public static DialogManagement dialogManagement;
	public static SoundManagement soundManagement;
    public static MultiplayerManagement multiplayerManagement;
    public static ScriptManagement scriptManagement;
    
    public static Game game;
    public static int compteur_animation;
	
    private static ClientEvent askedEvent;
    
	// For debug
	public static int extraSpeed=1;
	
	private void initializeServer(Game p_game) {
		// Inits de départ
		spriteManagement=new SpriteManagement();
		mapManagement=new MapManagement();
		persoManagement=new PersoManagement();
		dialogManagement=new DialogManagement();
		collideManagement=new CollideManagement();
		soundManagement=new SoundManagement();
		playerManagement=new PlayerManagement();
        multiplayerManagement = new MultiplayerManagement();
        scriptManagement = new ScriptManagement();
        
        setGame(p_game);

		// Charge une map
		String mapName=p_game.mapName;
		if (mapName != null) {
			mapManagement.charge_map(mapName);
		}
	}

	static public int spawnClient() {

        Point respawnLocation = mapManagement.getRespawnPosition();
        PersoZildo zildo = new PersoZildo(respawnLocation.getX(), respawnLocation.getY());
        spriteManagement.spawnPerso(zildo);

        if (game.multiPlayer) {
        	zildo.resetForMultiplayer();
        }
        return zildo.getId();
    }

    /**
     * Zildo comes to death, so respawn him another place.
     * @param p_zildo
     */
    static public void respawnClient(PersoZildo p_zildo) {
        Point respawnLocation = mapManagement.getRespawnPosition();
        
        p_zildo.resetForMultiplayer();
        
        p_zildo.placeAt(respawnLocation);
    }

	public EngineZildo(Game p_game) {
		initializeServer(p_game);
	}
	
	public void cleanUp() {

	}
	
	@Override
	public void finalize()
	{
		// L'ordre des suppression est TRES important ! En effet, le vidage des cartes passe
		// par le vidage de tous les sprites/persos qui y sont référencés. Donc on a besoin
		// d'avoir à ce moment là l'objet 'spriteManagement'.
		/*
		delete mapManagement;
		delete spriteManagement;
		delete persoManagement;
		delete playerManagement;
		delete guiManagement;
		delete dialogManagement;
		delete soundManagement;
		delete collideManagement;
	*/
	}	
	
	public void renderFrame(Collection<ClientState> p_clientStates) {
		// Animate the world
		// 1) Players
		boolean block=false;
		for (ClientState 
				state : p_clientStates) {
			
			
			if (!game.multiPlayer) {
				block=ClientEngineZildo.client.isIngameMenu();
			} else {
				multiplayerManagement.render();
			}
			
			// If client has pressed keys and he's not blocked, we manage them, then clear.
			if (state.keys != null && !block) {
				playerManagement.manageKeyboard(state);
			}
			state.keys=null;
			
			// Look for map change (only in single player for now)
			if (!game.multiPlayer && mapManagement.isChangingMap(state.zildo) && state.event.nature==ClientEventNature.NOEVENT) {
				ChainingPoint ch=mapManagement.getChainingPoint();
				if (ch.isBorder()) {
					state.event=new ClientEvent(ClientEventNature.CHANGINGMAP_SCROLL_ASKED);
				} else {
					state.event=new ClientEvent(ClientEventNature.CHANGINGMAP_ASKED);
				}
				state.event.chPoint=ch;
				state.event.mapChange=true;
			} else if (askedEvent != null) {
				// Use the asked event and reset it
				state.event=askedEvent;
				askedEvent=null;
			}
		}

		
		if (!block) {
			scriptManagement.render();
		}
		
		// 2) Rest of the world
		collideManagement.initFrame();
		spriteManagement.updateSprites(block);
		collideManagement.manageCollisions(p_clientStates);
		mapManagement.updateMap();
		
		compteur_animation++;
	}
	
    public ClientEvent renderEvent(ClientEvent p_event) {
        ClientEvent retEvent = p_event;

        switch (p_event.nature) {
            case FADEOUT_OVER:
            case CHANGINGMAP_SCROLL_WAIT_MAP:
            	if (p_event.chPoint != null) {
	                mapManagement.processChangingMap(p_event.chPoint);
	                ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
	                if (p_event.nature == ClientEventNature.CHANGINGMAP_SCROLL_WAIT_MAP) {
	                    retEvent.nature = ClientEventNature.CHANGINGMAP_SCROLL_START;
	                    retEvent.angle = mapManagement.getMapScrollAngle();
	                } else {
	                    retEvent.nature = ClientEventNature.CHANGINGMAP_LOADED;
	                }
            	} else {
            		retEvent.nature = ClientEventNature.NOEVENT;
            	}
                break;
            case CHANGINGMAP_SCROLLOVER:
            	persoManagement.getZildo().setGhost(false);
            	retEvent.nature = ClientEventNature.NOEVENT;
            	retEvent.mapChange = false;
            	break;
        }
        retEvent.script=scriptManagement.isScripting();
        
        return retEvent;
    }
	
	void loadMap(String mapname)
	{
		// Clear existing entities
		persoManagement.clearPersos();
	
		// Load map
		mapManagement.charge_map(mapname);
	}
	
	public static void askEvent(ClientEvent p_event) {
		askedEvent=p_event;
	}

	public static void setGame(Game game) {
		EngineZildo.game = game;
	}
	
	
}