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

package zildo.server;

import java.util.Collection;

import zildo.client.ClientEngineZildo;
import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.monde.Game;
import zildo.monde.dialog.DialogManagement;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.ChainingPoint.MapLink;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.util.Point;
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
			mapManagement.loadMap(mapName, false);
		}
	}

	static public int spawnClient(ZildoOutfit p_outfit) {

        Point respawnLocation = mapManagement.getRespawnPosition();
        PersoZildo zildo = new PersoZildo(respawnLocation.getX(), respawnLocation.getY(), p_outfit);
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
	
	public void renderFrame(Collection<ClientState> p_clientStates) {
		// Animate the world
		// 1) Players
		boolean block=false;
		boolean blockKeyboard=false;
		for (ClientState 
				state : p_clientStates) {
			
			blockKeyboard=ClientEngineZildo.client.isIngameMenu();
			if (game.multiPlayer) {
				multiplayerManagement.render();
			} else {	// Block everything in single player
				block=blockKeyboard;
				block|=state.event.nature == ClientEventNature.CHANGINGMAP_SCROLL;
				block|=persoManagement.getZildo().isInventoring();
			}
			
			// If client has pressed keys and he's not blocked, we manage them, then clear.
			if (state.keys != null && !blockKeyboard) {
				playerManagement.manageKeyboard(state);
			}
			state.keys=null;
			
			// Look for map change (only in single player for now)
			if (!game.multiPlayer && mapManagement.isChangingMap(state.zildo) && state.event.nature==ClientEventNature.NOEVENT) {
				ChainingPoint ch=mapManagement.getChainingPoint();
				if (ch.isBorder()) {
					state.event=new ClientEvent(ClientEventNature.CHANGINGMAP_SCROLL_ASKED);
				} else {
					MapLink linkType=ch.getLinkType();
					if (ch.isDone() || linkType == MapLink.REGULAR) {
						state.event=new ClientEvent(ClientEventNature.CHANGINGMAP_ASKED);
					} else {
						switch (linkType) {
						case STAIRS_CORNER_RIGHT:
							EngineZildo.scriptManagement.execute("stairsUpCornerRight");
							break;
						case STAIRS_CORNER_LEFT:
							EngineZildo.scriptManagement.execute("stairsUpCornerLeft");
							break;
						case STAIRS_STRAIGHT:
							EngineZildo.scriptManagement.execute("stairsUp");
							break;
						}
						state.event.nature=ClientEventNature.SCRIPT;
						ch.setDone(true);
					}
				}
				state.event.chPoint=ch;
				state.event.mapChange=true;
			} else if (askedEvent != null) {
				// Use the asked event and reset it
				state.event.nature=askedEvent.nature;
				state.event.effect=askedEvent.effect;
				if (askedEvent.chPoint != null) {
					//TODO: check this ! It's sensitive
					state.event.chPoint=askedEvent.chPoint;
				}
				state.event.mapChange = askedEvent.mapChange;
				askedEvent=null;
			}
		}

		
		collideManagement.initFrame();

		if (!block) {
			scriptManagement.render();
		}
		
		// 2) Rest of the world
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
	                ClientEngineZildo.mapDisplay.setPreviousMap(EngineZildo.mapManagement.getPreviousMap());
	                if (p_event.nature == ClientEventNature.CHANGINGMAP_SCROLL_WAIT_MAP) {
	                    retEvent.nature = ClientEventNature.CHANGINGMAP_SCROLL_START;
	                    retEvent.angle = mapManagement.getMapScrollAngle();
	                } else {
	                	// Do the right animation when Zildo came on the new map
	                	ChainingPoint ch = mapManagement.getChainingPoint();
						MapLink linkType=MapLink.REGULAR;
						if (ch != null) {
							linkType=ch.getLinkType();
						}
						switch (linkType) {
							case STAIRS_CORNER_LEFT:
								EngineZildo.scriptManagement.execute("stairsUpCornerLeftEnd");
								retEvent.nature=ClientEventNature.SCRIPT;
								break;
							case STAIRS_CORNER_RIGHT:
								EngineZildo.scriptManagement.execute("stairsUpCornerRightEnd");
								retEvent.nature=ClientEventNature.SCRIPT;
								break;
							case STAIRS_STRAIGHT:
								EngineZildo.scriptManagement.execute("stairsUpEnd");
								retEvent.nature=ClientEventNature.SCRIPT;
								break;
							case REGULAR:
								retEvent.nature = ClientEventNature.CHANGINGMAP_LOADED;
								break;
						}
						retEvent.chPoint = null;
	                }
            	} else {
            		retEvent.nature = ClientEventNature.NOEVENT;
            	}
                break;
			case CHANGINGMAP_WAITSCRIPT:	// Engine is doing 'map script' (see MapscriptElement)
				if (!scriptManagement.isScripting()) {
					retEvent.nature = ClientEventNature.CHANGINGMAP_SCROLL;
				}
				retEvent.chPoint = null;
				break;
            case CHANGINGMAP_SCROLLOVER:
            	persoManagement.getZildo().setGhost(false);
            	spriteManagement.clearSuspendedEntities();
            	mapManagement.notifiyScrollOver();
                ClientEngineZildo.mapDisplay.setPreviousMap(EngineZildo.mapManagement.getPreviousMap());
            	retEvent.nature = ClientEventNature.NOEVENT;
            	retEvent.mapChange = false;
            	retEvent.chPoint = null;
            	break;
            case DIALOG_FULLDISPLAY:
	        	dialogManagement.setFullSentenceDisplayed();
	        	retEvent.nature = ClientEventNature.NOEVENT;
	        	break;
        }

        retEvent.script=scriptManagement.isScripting();
        
        return retEvent;
    }

	
	public static void askEvent(ClientEvent p_event) {
		askedEvent=p_event;
	}

	public static void setGame(Game game) {
		EngineZildo.game = game;
	}
	
	
}